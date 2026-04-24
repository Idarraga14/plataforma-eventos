package co.edu.uniquindio.pgii.plataforma_eventos.application.strategy;

import co.edu.uniquindio.pgii.plataforma_eventos.application.factory.EntradaFactory;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

/**
 * Estrategia de asignación de cupos para zonas con asientos numerados específicos.
 *
 * <p>Verifica la disponibilidad directamente en el inventario comercial del evento
 * ({@code AsientoEvento}), garantizando el aislamiento entre funciones que compartan
 * el mismo recinto. Si el asiento está {@code DISPONIBLE}, lo pone en {@code BLOQUEADO}
 * (reservado durante el proceso de pago) y genera una {@code EntradaAsiento}.</p>
 *
 * <p>[Requerimiento: RF-003] - Activa cuando el usuario selecciona asientos concretos
 * desde la cuadrícula de asientos; {@code idAsientos} contiene el ID de cada silla elegida.</p>
 * <p>[Requerimiento: RF-018] - El estado {@code BLOQUEADO} impide que otro usuario
 * adquiera el mismo asiento mientras el primero completa el pago.</p>
 * <p>[Patrón: Strategy] - Actúa como <strong>Estrategia Concreta</strong> para asientos numerados.
 * Seleccionada por {@code PlataformaFacadeImpl} cuando {@code idAsientos} es no vacío.</p>
 * <p>[Patrón: Factory] - Delega la creación de la entrada a
 * {@code EntradaFactory#fabricar(Zona, AsientoEvento)}.</p>
 */
public class AsignacionPorAsientoStrategy implements AsignacionStrategy {

    /**
     * Reserva el asiento solicitado en el inventario del evento y retorna una {@code EntradaAsiento}.
     *
     * <p>Pasos de ejecución:</p>
     */
    @Override
    public Entrada asignarCupo(Evento evento, String idZona, String idAsientoSolicitado) {
        // 1. Localizar la zona en el recinto (necesaria para el precio base y la referencia estructural)
        Zona zonaDestino = evento.getRecinto().getZonas().stream()
                .filter(z -> z.getIdZona().equals(idZona))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada."));

        // 2. Consultar el inventario comercial del evento (no el estado físico del recinto)
        AsientoEvento asientoEvento = evento.obtenerAsientoEvento(idAsientoSolicitado);

        // 3. Regla de negocio: solo se puede asignar si el asiento está DISPONIBLE en este evento
        if (asientoEvento.getEstado() != AsientoEstado.DISPONIBLE) {
            throw new IllegalStateException("El asiento ya ha sido reservado o vendido.");
        }

        // 4. Bloquear el asiento en el inventario del evento mientras el usuario completa el pago
        asientoEvento.setEstado(AsientoEstado.BLOQUEADO);

        // 5. Crear y retornar la entrada numerada mediante la Factory
        return EntradaFactory.fabricar(zonaDestino, asientoEvento);
    }
}
