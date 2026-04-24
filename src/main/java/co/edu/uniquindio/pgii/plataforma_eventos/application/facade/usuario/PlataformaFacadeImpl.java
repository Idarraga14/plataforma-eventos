package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionPorAsientoStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionPorZonaStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.AccesoPreferencialDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.MerchandisingDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.PaqueteVIPDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.ParqueaderoDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.SeguroCancelacionDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.MedioPago;
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

/**
 * Implementación concreta de {@link PlataformaFacade} para los casos de uso del comprador.
 *
 * <p>Orquesta la colaboración entre el dominio (Compra, Evento, Usuario), los patrones
 * de comportamiento (Strategy para asignación, State para el ciclo de vida de la compra),
 * los patrones estructurales (Decorator para servicios extras, Adapter para pago y exportación)
 * y el repositorio en memoria ({@code PlataformaEventosSingleton}).</p>
 *
 * <p>[Patrón: Facade] - Actúa como la <strong>Implementación Concreta de la Fachada</strong>
 * para el módulo de usuario; los controladores de JavaFX nunca conocen esta clase directamente.</p>
 * <p>[Patrón: Strategy] - Selecciona dinámicamente {@code AsignacionPorAsientoStrategy}
 * o {@code AsignacionPorZonaStrategy} en {@link #crearOrdenCompra} según la solicitud.</p>
 * <p>[Patrón: Decorator] - Aplica los decoradores de servicios extras en {@code aplicarExtras}
 * según los códigos de la lista {@code extras} recibida del controlador.</p>
 * <p>[Patrón: Adapter] - Usa {@code SimuladorPagoAdapter} ({@link ProcesadorPago}) para
 * abstraer la pasarela de pago y {@code ExportadorReporte} para los comprobantes.</p>
 */
public class PlataformaFacadeImpl implements PlataformaFacade {

    /** Repositorio singleton en memoria con todos los datos de la plataforma. */
    private final PlataformaEventosSingleton plat = PlataformaEventosSingleton.getInstance();

    /**
     * Adapter de la pasarela de pago.
     *
     * <p>[Patrón: Adapter] - {@code SimuladorPagoAdapter} traduce la interfaz {@code ProcesadorPago}
     * al contrato del servicio externo simulado {@code SimuladorPagoExterno}.</p>
     */
    private final ProcesadorPago pasarela = new SimuladorPagoAdapter();

    /**
     * Crea una orden de compra orquestando Strategy + Decorator y persiste la compra.
     *
     * <p>Flujo completo:</p>
     */
    @Override
    public Compra crearOrdenCompra(String idUsuario, String idEvento, String idZona,
                                   List<String> idAsientos, int cantidad, List<String> extras) {
        // 1. Recuperar las entidades del repositorio en memoria
        Usuario usuario = plat.buscarUsuario(idUsuario);
        Evento evento = plat.buscarEvento(idEvento);

        // 2. Crear la Compra — arranca automáticamente en EstadoCreada (patrón State)
        Compra orden = new Compra(usuario, evento);

        // 3. Seleccionar STRATEGY en tiempo de ejecución: asiento numerado vs. zona libre
        boolean modoAsiento = idAsientos != null && !idAsientos.isEmpty();
        AsignacionStrategy motor = modoAsiento
                ? new AsignacionPorAsientoStrategy()
                : new AsignacionPorZonaStrategy();

        try {
            if (modoAsiento) {
                // 4a. Modo asiento: una entrada por cada ID de asiento solicitado
                for (String idAsiento : idAsientos) {
                    Entrada ticket = motor.asignarCupo(evento, idZona, idAsiento);
                    ticket = aplicarExtras(ticket, extras); // Aplicar cadena Decorator
                    orden.agregarEntrada(ticket);
                }
            } else {
                // 4b. Modo zona: generar 'n' entradas de aforo libre
                int n = Math.max(1, cantidad);
                for (int i = 0; i < n; i++) {
                    Entrada ticket = motor.asignarCupo(evento, idZona, null);
                    ticket = aplicarExtras(ticket, extras); // Aplicar cadena Decorator
                    orden.agregarEntrada(ticket);
                }
            }
        } catch (RuntimeException ex) {
            // 5. Rollback parcial: si algo falla a mitad de la asignación, liberar lo ya bloqueado
            orden.getEntradas().forEach(Entrada::liberarRecursos);
            throw ex;
        }

        // 6. Calcular el total (suma de precios con decoradores) y persistir
        orden.calcularTotal();
        plat.getCompras().add(orden);
        return orden;
    }

    /**
     * Procesa el pago de una orden {@code CREADA} usando el Adapter de pasarela.
     * Si el banco aprueba: State CREADA → PAGADA; confirma asientos como {@code VENDIDO}.
     * Si rechaza: State CREADA → CANCELADA; libera asientos y notifica observers.
     */
    @Override
    public void procesarPagoOrden(String idCompra, String numTarjeta, String cvv) {
        Compra orden = buscarCompra(idCompra);
        if (orden.getEstadoEnum() != CompraEstado.CREADA) {
            throw new IllegalStateException("La orden ya no está en estado CREADA.");
        }

        // 1. Delegar el cobro al Adapter de pasarela de pago
        boolean pagoExitoso = pasarela.procesarPago(orden, numTarjeta, cvv);

        if (pagoExitoso) {
            // 2a. Pago aprobado: transicionar State (CREADA → PAGADA)
            orden.pagar();
            // 3a. Consolidar la venta de los asientos bloqueados (BLOQUEADO → VENDIDO)
            orden.getEntradas().forEach(Entrada::confirmarVenta);
            // 4a. Notificar al Dashboard y otros observers del cambio de aforo
            plat.notificarCambio(orden.getEvento());
        } else {
            // 2b. Pago rechazado: cancelar orden (CREADA → CANCELADA)
            orden.cancelar();
            // 3b. Liberar todos los asientos reservados (BLOQUEADO → DISPONIBLE)
            orden.getEntradas().forEach(Entrada::liberarRecursos);
            // 4b. Notificar cambio y lanzar excepción para que la UI informe al usuario
            plat.notificarCambio(orden.getEvento());
            throw new RuntimeException("Pago rechazado por el banco.");
        }
    }

    /**
     * Cancela una orden {@code CREADA} y libera los recursos (asientos) asociados.
     *
     * <p>[Patrón: State] - Delega en el estado actual; lanza excepción si el estado no lo permite.</p>
     */
    @Override
    public void cancelarOrdenCompra(String idCompra) {
        Compra orden = buscarCompra(idCompra);
        // 1. Delegar en el estado concreto actual (solo EstadoCreada permite cancelar)
        orden.cancelar();
        // 2. Liberar los asientos reservados (BLOQUEADO → DISPONIBLE)
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

    /**
     * Calcula los cupos disponibles en una zona, diferenciando entre zonas numeradas y libres.
     *
     * <p>Para zonas numeradas: cuenta los {@code AsientoEvento} en estado {@code DISPONIBLE}
     * en el inventario del evento (independiente de otros eventos del mismo recinto).</p>
     * <p>Para zonas de aforo libre: descuenta del aforo total las entradas activas,
     * desenrollando la cadena de decoradores para encontrar el tipo de entrada base.</p>
     */
    @Override
    public int obtenerCuposDisponibles(String idEvento, String idZona) {
        Evento evento = plat.buscarEvento(idEvento);
        Zona zona = evento.getRecinto().getZonas().stream()
                .filter(z -> z.getIdZona().equals(idZona))
                .findFirst()
                .orElseThrow();

        // Zonas numeradas: consultar el inventario comercial del evento directamente
        if (!zona.getAsientos().isEmpty()) {
            return (int) evento.getInventarioDe(zona).stream()
                    .filter(ae -> ae.getEstado() == AsientoEstado.DISPONIBLE)
                    .count();
        }

        // Zonas de aforo libre: capacidad total menos entradas activas (desenvuelve decoradores)
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

    /**
     * Desenvuelve la cadena de decoradores de una entrada hasta llegar al componente base concreto.
     *
     * <p>[Patrón: Decorator] - Necesario para identificar el tipo real de entrada
     * ({@code EntradaZona} o {@code EntradaAsiento}) cuando la entrada está envuelta en decoradores.</p>
     */
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
    public Usuario registrarUsuario(String nombre, String correo, String telefono, String password) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (correo == null || !correo.contains("@"))
            throw new IllegalArgumentException("El correo no es válido.");
        if (password == null || password.length() < 4)
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");

        boolean correoEnUso = plat.getUsuarios().stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo));
        if (correoEnUso)
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo.");

        Usuario nuevo = new Usuario(nombre.trim(), correo.trim(), telefono, password, false);
        plat.getUsuarios().add(nuevo);
        return nuevo;
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

    /**
     * Aplica los decoradores de servicios extras a una entrada base, formando una cadena Decorator.
     *
     * <p>[Patrón: Decorator] - Cada código en {@code extras} envuelve la entrada anterior
     * con un nuevo decorador, acumulando precio y descripción. El orden de aplicación
     * es el mismo que el orden de la lista recibida.</p>
     *
     * @param ticket entrada base (o entrada ya parcialmente decorada)
     * @param extras lista de códigos de servicios adicionales solicitados por el usuario
     * @return entrada final con todos los decoradores aplicados en cadena
     */
    private Entrada aplicarExtras(Entrada ticket, List<String> extras) {
        if (extras == null) return ticket;
        for (String extra : extras) {
            switch (extra) {
                case "VIP"                  -> ticket = new PaqueteVIPDecorator(ticket);
                case "SEGURO_CANCELACION"   -> ticket = new SeguroCancelacionDecorator(ticket);
                case "PARQUEADERO"          -> ticket = new ParqueaderoDecorator(ticket);
                case "MERCHANDISING"        -> ticket = new MerchandisingDecorator(ticket);
                case "ACCESO_PREFERENCIAL"  -> ticket = new AccesoPreferencialDecorator(ticket);
            }
        }
        return ticket;
    }
}
