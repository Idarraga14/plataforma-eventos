package co.edu.uniquindio.pgii.plataforma_eventos.application.strategy;

import co.edu.uniquindio.pgii.plataforma_eventos.application.factory.EntradaFactory;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.PlataformaEventosSingleton;

public class AsignacionPorZonaStrategy implements AsignacionStrategy {
    @Override
    public Entrada asignarCupo(Evento evento, String idZona, String idAsientoSolicitado) {
        // 1. Buscamos la zona dentro del recinto del evento
        Zona zonaDestino = evento.getRecinto().getZonas().stream()
                .filter(zona -> zona.getIdZona().equals(idZona))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada en el evento"));

        // 2. Calculamos el aforo actual consultando el Singleton.
        //    Las órdenes CREADA/PAGADA/CONFIRMADA reservan cupo; las CANCELADA/REEMBOLSADA no.
        long entradasVendidas = PlataformaEventosSingleton.getInstance().getCompras().stream()
                .filter(compra -> compra.getEvento().getIdEvento().equals(evento.getIdEvento()))
                .filter(compra -> compra.getEstadoEnum() != CompraEstado.CANCELADA
                        && compra.getEstadoEnum() != CompraEstado.REEMBOLSADA)
                .flatMap(compra -> compra.getEntradas().stream())
                .filter(entrada -> entrada instanceof EntradaZona &&
                        ((EntradaZona) entrada).getZona().getIdZona().equals(idZona))
                .count();

        // 3. Regla de negocio: Validar capacidad
        if (entradasVendidas >= zonaDestino.getCapacidad()) {
            throw new IllegalStateException("La zona " + zonaDestino.getNombre() + " está totalmente agotada.");
        }

        // 4. Si llegamos aquí, hay cupo. Retornamos la entrada.
        return EntradaFactory.fabricar(zonaDestino);
    }
}
