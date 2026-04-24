package co.edu.uniquindio.pgii.plataforma_eventos.domain.enums;

/**
 * Define el ciclo de vida de una {@code Entrada} generada tras una compra exitosa.
 *
 * <p>Una entrada pasa a estado {@code ACTIVA} al ser emitida, {@code USADA} cuando
 * el asistente accede al evento, y {@code ANULADA} cuando la compra asociada es
 * cancelada o reembolsada.</p>
 *
 * <p>[Requerimiento: RF-007] - Al confirmar el pago, todas las entradas de la compra
 * se emiten en estado {@code ACTIVA}, haciendo válida la asistencia del usuario al evento.</p>
 * <p>[Requerimiento: RF-009] - El historial de compras muestra el estado de cada entrada
 * ({@code ACTIVA}, {@code USADA} o {@code ANULADA}) como parte del comprobante.</p>
 * <p>[Patrón: Decorator] - Esta enumeración es consultada por la clase base {@code Entrada}
 * y sus decoradores concretos para exponer el estado de validez de la entrada enriquecida.</p>
 */
public enum EntradaEstado {

    /** Entrada emitida y válida para acceder al evento. */
    ACTIVA,

    /** Entrada ya utilizada; el asistente ingresó al evento. */
    USADA,

    /** Entrada invalidada por cancelación o reembolso de la compra. */
    ANULADA
}
