package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.factory.EntradaFactory;
import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionPorAsientoStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionPorZonaStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.application.strategy.AsignacionStrategy;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.PaqueteVIPDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.ParqueaderoDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.SeguroCancelacionDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
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

import java.util.List;

public class PlataformaFacadeImpl implements PlataformaFacade {

    private final PlataformaEventosSingleton plat = PlataformaEventosSingleton.getInstance();
    private final ProcesadorPago pasarela = new SimuladorPagoAdapter();

    @Override
    public void realizarCompra(String idUsuario, String idEvento, String idZona, String idAsiento,
                               List<String> extras, String numTarjeta, String cvv) {
        // 1. Recuperar entidades
        Usuario usuario = plat.buscarUsuario(idUsuario);
        Evento evento = plat.buscarEvento(idEvento);

        // 2. Definir Estrategia de Asignación (STRATEGY)
        // Aquí podrías tener un selector de estrategia basado en el tipo de evento
        AsignacionStrategy motor = (evento.getCategoria() == EventoCategoria.CONCIERTO)
                ? new AsignacionPorZonaStrategy()
                : new AsignacionPorAsientoStrategy();

        // 3. Intentar asignar cupo y fabricar entrada (STRATEGY + FACTORY)
        Entrada ticket = motor.asignarCupo(evento, idZona, idAsiento);

        // 4. Aplicar servicios adicionales (DECORATOR)
        for (String extra : extras) {
            if (extra.equals("VIP")) ticket = new PaqueteVIPDecorator(ticket);
            if (extra.equals("SEGURO")) ticket = new SeguroCancelacionDecorator(ticket, 10000.0);
            if (extra.equals("PARQUEADERO")) ticket = new ParqueaderoDecorator(ticket);
        }

        // 5. Crear la Compra en estado inicial (STATE)
        Compra nuevaCompra = new Compra(usuario, evento);
        nuevaCompra.agregarEntrada(ticket);
        nuevaCompra.calcularTotal();

        // 6. Procesar Pago (ADAPTER)
        boolean pagoExitoso = pasarela.procesarPago(nuevaCompra, numTarjeta, cvv);

        if (pagoExitoso) {
            nuevaCompra.pagar(); // Cambia de CREADA a PAGADA vía STATE
            plat.getCompras().add(nuevaCompra);

            // 7. Notificar a la UI (OBSERVER)
            plat.notificarCambio(evento);
        } else {
            throw new RuntimeException("Pago rechazado por el banco.");
        }
    }

    @Override
    public List<Evento> obtenerEventosDisponibles() {
        return PlataformaEventosSingleton.getInstance().getEventos().stream()
                .filter(evento -> evento.getEstado() == EventoEstado.PUBLICADO)
                .toList();
    }

    @Override
    public int obtenerCuposDisponibles(String idEvento, String idZona) {
        // 1. Buscamos la capacidad total de la zona
        Evento evento = plat.buscarEvento(idEvento);
        Zona zona = evento.getRecinto().getZonas().stream()
                .filter(z -> z.getIdZona().equals(idZona))
                .findFirst()
                .orElseThrow();

        // 2. Contamos cuántas entradas se han vendido realmente
        long entradasVendidas = plat.getCompras().stream()
                // Filtramos las compras de este evento
                .filter(c -> c.getEvento().getIdEvento().equals(idEvento))
                // REGLA CLAVE: No contamos las compras canceladas o devueltas
                .filter(c -> c.getEstadoEnum() != CompraEstado.CANCELADA && c.getEstadoEnum() != CompraEstado.REEMBOLSADA)
                // Extraemos todas las entradas de esas compras
                .flatMap(c -> c.getEntradas().stream())
                // Filtramos solo las que pertenecen a la zona que estamos consultando
                .filter(e -> e instanceof EntradaZona && ((EntradaZona) e).getZona().getIdZona().equals(idZona))
                .count();

        // 3. Retornamos la resta matemática
        return (int) (zona.getCapacidad() - entradasVendidas);
    }

    @Override
    public double cotizarTotal(String idEvento, String idZona, int cantidadEntradas, List<String> extras) {

        return 0;
    }

    @Override
    public Usuario login(String correo, String password) {
        return PlataformaEventosSingleton.getInstance().getUsuarios().stream()
                .filter(u -> u.getCorreo().equals(correo) && u.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));
    }

    @Override
    public void actualizarPerfil(Usuario usuario) {

    }
}
