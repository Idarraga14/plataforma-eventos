package co.edu.uniquindio.pgii.plataforma_eventos.application.strategy;

import co.edu.uniquindio.pgii.plataforma_eventos.application.factory.EntradaFactory;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

public class AsignacionPorAsientoStrategy implements AsignacionStrategy {
    @Override
    public Entrada asignarCupo(Evento evento, String idZona, String idAsientoSolicitado) {
        // 1. Buscamos la zona
        Zona zonaDestino = evento.getRecinto().getZonas().stream()
                .filter(z -> z.getIdZona().equals(idZona))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada."));

        // 2. Buscamos el asiento específico dentro de esa zona
        Asiento asientoDestino = zonaDestino.getAsientos().stream()
                .filter(asiento -> asiento.getIdAsiento().equals(idAsientoSolicitado))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado en la zona solicitada."));

        // 3. Regla de negocio: ¿Está físicamente libre?
        if (asientoDestino.getEstado() != AsientoEstado.DISPONIBLE) {
            throw new IllegalStateException("El asiento ya ha sido reservado o vendido.");
        }

        // 4. Bloqueamos el asiento mientras el usuario termina el flujo de pago.
        //    La transición a VENDIDO ocurre solo cuando el pago es aprobado.
        asientoDestino.setEstado(AsientoEstado.BLOQUEADO);

        // 5. Retornamos la entrada vinculada a la silla
        return EntradaFactory.fabricar(zonaDestino, asientoDestino);
    }
}
