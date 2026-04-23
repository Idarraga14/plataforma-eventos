package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionPorAsientoStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionPorZonaStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.PaqueteVIPDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.ParqueaderoDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.SeguroCancelacionDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.PlataformaEventosSingleton;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.ProcesadorPago;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.SimuladorPagoAdapter;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ExportadorCSVAdapter;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ExportadorPDFAdapter;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ExportadorReporte;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.FormatoReporte;

import java.util.List;
import java.util.NoSuchElementException;

public class PlataformaFacadeImpl implements PlataformaFacade {

    private final PlataformaEventosSingleton plat = PlataformaEventosSingleton.getInstance();
    private final ProcesadorPago pasarela = new SimuladorPagoAdapter();

    @Override
    public Compra crearOrdenCompra(String idUsuario, String idEvento, String idZona,
                                   List<String> idAsientos, int cantidad, List<String> extras) {
        // 1. Recuperar entidades
        Usuario usuario = plat.buscarUsuario(idUsuario);
        Evento evento = plat.buscarEvento(idEvento);

        // 2. Crear la Compra (arranca en EstadoCreada por default)
        Compra orden = new Compra(usuario, evento);

        // 3. Elegir STRATEGY según haya asientos específicos o no
        boolean modoAsiento = idAsientos != null && !idAsientos.isEmpty();
        AsignacionStrategy motor = modoAsiento
                ? new AsignacionPorAsientoStrategy()
                : new AsignacionPorZonaStrategy();

        try {
            if (modoAsiento) {
                for (String idAsiento : idAsientos) {
                    Entrada ticket = motor.asignarCupo(evento, idZona, idAsiento);
                    ticket = aplicarExtras(ticket, extras);
                    orden.agregarEntrada(ticket);
                }
            } else {
                int n = Math.max(1, cantidad);
                for (int i = 0; i < n; i++) {
                    Entrada ticket = motor.asignarCupo(evento, idZona, null);
                    ticket = aplicarExtras(ticket, extras);
                    orden.agregarEntrada(ticket);
                }
            }
        } catch (RuntimeException ex) {
            // Si algo falla a mitad de la asignación, liberamos lo que ya se bloqueó
            orden.getEntradas().forEach(Entrada::liberarRecursos);
            throw ex;
        }

        orden.calcularTotal();
        plat.getCompras().add(orden);
        return orden;
    }

    @Override
    public void procesarPagoOrden(String idCompra, String numTarjeta, String cvv) {
        Compra orden = buscarCompra(idCompra);
        if (orden.getEstadoEnum() != CompraEstado.CREADA) {
            throw new IllegalStateException("La orden ya no está en estado CREADA.");
        }

        boolean pagoExitoso = pasarela.procesarPago(orden, numTarjeta, cvv);

        if (pagoExitoso) {
            orden.pagar(); // STATE: CREADA -> PAGADA
            // Consolidar la venta de los asientos físicos bloqueados
            orden.getEntradas().forEach(Entrada::confirmarVenta);
            plat.notificarCambio(orden.getEvento());
        } else {
            // El banco rechazó: cancelamos la orden y liberamos recursos
            orden.cancelar(); // STATE: CREADA -> CANCELADA
            orden.getEntradas().forEach(Entrada::liberarRecursos);
            plat.notificarCambio(orden.getEvento());
            throw new RuntimeException("Pago rechazado por el banco.");
        }
    }

    @Override
    public void cancelarOrdenCompra(String idCompra) {
        Compra orden = buscarCompra(idCompra);
        orden.cancelar(); // STATE delega en el estado actual; lanza si no se permite
        orden.getEntradas().forEach(Entrada::liberarRecursos);
        plat.notificarCambio(orden.getEvento());
    }

    @Override
    public Compra buscarCompra(String idCompra) {
        return plat.getCompras().stream()
                .filter(c -> c.getIdCompra().equals(idCompra))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Compra no encontrada: " + idCompra));
    }

    @Override
    public List<Evento> obtenerEventosDisponibles() {
        return plat.getEventos().stream()
                .filter(evento -> evento.getEstado() == EventoEstado.PUBLICADO)
                .toList();
    }

    @Override
    public int obtenerCuposDisponibles(String idEvento, String idZona) {
        Evento evento = plat.buscarEvento(idEvento);
        Zona zona = evento.getRecinto().getZonas().stream()
                .filter(z -> z.getIdZona().equals(idZona))
                .findFirst()
                .orElseThrow();

        // Zonas con asientos físicos: contar asientos DISPONIBLES.
        if (!zona.getAsientos().isEmpty()) {
            return (int) zona.getAsientos().stream()
                    .filter(a -> a.getEstado() == co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado.DISPONIBLE)
                    .count();
        }

        // Zonas de aforo libre: capacidad - entradas activas (desenvuelve decoradores).
        long entradasOcupadas = plat.getCompras().stream()
                .filter(c -> c.getEvento().getIdEvento().equals(idEvento))
                .filter(c -> c.getEstadoEnum() != CompraEstado.CANCELADA
                        && c.getEstadoEnum() != CompraEstado.REEMBOLSADA)
                .flatMap(c -> c.getEntradas().stream())
                .map(PlataformaFacadeImpl::entradaBase)
                .filter(e -> e instanceof EntradaZona && ((EntradaZona) e).getZona().getIdZona().equals(idZona))
                .count();

        return (int) (zona.getCapacidad() - entradasOcupadas);
    }

    private static co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada entradaBase(
            co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada e) {
        co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada actual = e;
        while (actual instanceof co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.EntradaDecorator d) {
            actual = d.getEntradaEnvuelta();
        }
        return actual;
    }

    @Override
    public byte[] generarComprobante(String idCompra, FormatoReporte formato) {
        Compra compra = buscarCompra(idCompra);
        ExportadorReporte exportador = switch (formato) {
            case PDF -> new ExportadorPDFAdapter();
            case CSV -> new ExportadorCSVAdapter();
        };
        return exportador.exportar(compra);
    }

    @Override
    public List<Compra> obtenerComprasPorUsuario(String idUsuario) {
        return plat.getCompras().stream()
                .filter(c -> c.getUsuario().getIdUsuario().equals(idUsuario))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .toList();
    }

    @Override
    public Usuario login(String correo, String password) {
        return plat.getUsuarios().stream()
                .filter(u -> u.getCorreo().equals(correo) && u.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));
    }

    @Override
    public void actualizarPerfil(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Usuario nulo.");

        boolean correoEnUso = plat.getUsuarios().stream()
                .anyMatch(u -> !u.getIdUsuario().equals(usuario.getIdUsuario())
                        && u.getCorreo().equalsIgnoreCase(usuario.getCorreo()));
        if (correoEnUso) {
            throw new IllegalArgumentException("El correo ya está registrado por otro usuario.");
        }

        Usuario existente = plat.buscarUsuario(usuario.getIdUsuario());
        existente.setNombreCompleto(usuario.getNombreCompleto());
        existente.setCorreo(usuario.getCorreo());
        existente.setNumeroTelefono(usuario.getNumeroTelefono());
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            existente.setPassword(usuario.getPassword());
        }
    }

    // --- Helpers privados ---

    private Entrada aplicarExtras(Entrada ticket, List<String> extras) {
        if (extras == null) return ticket;
        for (String extra : extras) {
            switch (extra) {
                case "VIP"                 -> ticket = new PaqueteVIPDecorator(ticket);
                case "SEGURO_CANCELACION"  -> ticket = new SeguroCancelacionDecorator(ticket, 15_000.0);
                case "PARQUEADERO"         -> ticket = new ParqueaderoDecorator(ticket);
            }
        }
        return ticket;
    }
}
