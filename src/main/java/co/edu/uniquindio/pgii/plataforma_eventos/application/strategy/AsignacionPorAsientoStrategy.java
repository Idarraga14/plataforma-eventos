package co.edu.uniquindio.pgii.plataforma_eventos.application.strategy;

import co.edu.uniquindio.pgii.plataforma_eventos.application.factory.EntradaFactory;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

public class AsignacionPorAsientoStrategy implements AsignacionStrategy {
    @Override
    public Entrada asignarCupo(Evento evento, String idZona, String idAsientoSolicitado) {
        // 1. Buscamos la zona en el recinto (para precio y referencia estructural)
        Zona zonaDestino = evento.getRecinto().getZonas().stream()
                .filter(z -> z.getIdZona().equals(idZona))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada."));

        // 2. Consultamos el inventario comercial del evento (no el estado físico del recinto)
        AsientoEvento asientoEvento = evento.obtenerAsientoEvento(idAsientoSolicitado);

        // 3. Regla de negocio: solo se puede asignar si está disponible en este evento
        if (asientoEvento.getEstado() != AsientoEstado.DISPONIBLE) {
            throw new IllegalStateException("El asiento ya ha sido reservado o vendido.");
        }

        // 4. Bloqueamos en el inventario del evento mientras se completa el pago
        asientoEvento.setEstado(AsientoEstado.BLOQUEADO);

        return EntradaFactory.fabricar(zonaDestino, asientoEvento);
    }
}
