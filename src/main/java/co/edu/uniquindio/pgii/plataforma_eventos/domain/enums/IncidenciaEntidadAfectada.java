package co.edu.uniquindio.pgii.plataforma_eventos.domain.enums;

/**
 * Identifica el tipo de entidad del dominio afectada por una {@code Incidencia} registrada.
 *
 * <p>Este clasificador permite al administrador categorizar rápidamente cada reporte de
 * problema e identificar si el impacto recae sobre la operación de un evento, sobre una
 * transacción de compra o sobre la cuenta de un usuario.</p>
 *
 * <p>[Requerimiento: RF-019] - Al registrar una incidencia el administrador debe indicar
 * la entidad afectada ({@code EVENTO}, {@code COMPRA} o {@code USUARIO}) para facilitar
 * su seguimiento, escalamiento y resolución.</p>
 */
public enum IncidenciaEntidadAfectada {

    /** La incidencia afecta a un evento (ej. fallo técnico, cancelación de sala, silla rota). */
    EVENTO,

    /** La incidencia afecta a una transacción de compra (ej. cobro duplicado, pago no acreditado). */
    COMPRA,

    /** La incidencia afecta a la cuenta o acceso de un usuario (ej. acceso sospechoso, bloqueo). */
    USUARIO
}
