package co.edu.uniquindio.pgii.plataforma_eventos.application.strategy;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;

/**
 * Interfaz de la estrategia de asignación de cupos durante el proceso de compra.
 *
 * <p>Define el contrato único {@link #asignarCupo} que las estrategias concretas implementan
 * con políticas de asignación distintas: por zona de aforo libre ({@code AsignacionPorZonaStrategy})
 * o por asiento numerado ({@code AsignacionPorAsientoStrategy}). El cliente (facade) selecciona
 * la estrategia apropiada en tiempo de ejecución según la solicitud del usuario.</p>
 *
 * <p>[Requerimiento: RF-003] - La elección de la estrategia determina el tipo de entrada
 * generada: una {@code EntradaZona} para acceso libre o una {@code EntradaAsiento} numerada.</p>
 * <p>[Requerimiento: RF-049] - Implementación explícita del patrón de comportamiento
 * <strong>Strategy</strong>, exigido como requerimiento no funcional del proyecto.</p>
 * <p>[Patrón: Strategy] - Actúa como la <strong>Interfaz de Estrategia</strong>.
 * Las implementaciones concretas son {@link AsignacionPorZonaStrategy} y
 * {@link AsignacionPorAsientoStrategy}; el Contexto es {@code PlataformaFacadeImpl}.</p>
 */
public interface AsignacionStrategy {

    /**
     * Valida la disponibilidad de cupo en el evento/zona y, si existe, reserva el recurso
     * y devuelve la entrada correspondiente.
     *
     * @param evento              evento para el que se solicita el cupo
     * @param idZona              identificador de la zona dentro del recinto del evento
     * @param idAsientoSolicitado ID del asiento específico; {@code null} para zona de aforo libre
     * @return nueva {@code Entrada} (concreta o base para decorar) con el cupo reservado
     * @throws IllegalArgumentException si la zona no existe en el evento
     * @throws IllegalStateException    si no hay cupo disponible o el asiento no está libre
     */
    Entrada asignarCupo(Evento evento, String idZona, String idAsientoSolicitado);
}
