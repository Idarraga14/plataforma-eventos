package co.edu.uniquindio.pgii.plataforma_eventos.domain.enums;

/**
 * Representa los estados posibles dentro del ciclo de vida de una {@code Compra}.
 *
 * <p>Esta enumeración es la "fotografía" observable del estado interno de {@code Compra};
 * el estado real y las transiciones válidas son gestionados por las clases concretas del
 * patrón State ({@code EstadoCreada}, {@code EstadoPagada}, etc.), que delegan en
 * {@code Compra#setEstado(CompraEstado)} para mantener la coherencia entre ambas capas.</p>
 *
 * <p>[Requerimiento: RF-005] - El estado {@code CREADA} corresponde al momento en que el
 * usuario inicia el proceso de compra y se reservan los recursos (entradas/asientos).</p>
 * <p>[Requerimiento: RF-005] - La transición a {@code PAGADA} ocurre al procesar exitosamente
 * el pago mediante el Adapter {@code SimuladorPagoAdapter}.</p>
 * <p>[Requerimiento: RF-006] - {@code CONFIRMADA} refleja la validación final de la orden
 * por parte del sistema tras verificar el pago.</p>
 * <p>[Requerimiento: RF-010] - {@code CANCELADA} se asigna cuando el usuario o el administrador
 * anulan la compra, liberando los recursos reservados.</p>
 * <p>[Requerimiento: RF-011] - {@code REEMBOLSADA} indica que el monto fue devuelto al comprador
 * partiendo siempre desde el estado {@code PAGADA} o {@code CONFIRMADA}.</p>
 * <p>[Patrón: State] - Actúa como enumeración de estados del contexto {@code Compra}.
 * Cada valor tiene su clase concreta correspondiente en el paquete {@code domain.state}
 * que implementa la interfaz {@code CompraState}.</p>
 */
public enum CompraEstado {

    /** Compra recién iniciada; recursos reservados pero pago aún no procesado. */
    CREADA,

    /** Pago aprobado por la pasarela; en espera de confirmación definitiva. */
    PAGADA,

    /** Compra completamente validada y confirmada; entradas disponibles para uso. */
    CONFIRMADA,

    /** Compra anulada; los recursos reservados han sido liberados. */
    CANCELADA,

    /** Monto devuelto al comprador tras cancelación de una compra ya pagada. */
    REEMBOLSADA,

    /** Compra marcada con incidencia activa registrada por el administrador. */
    INCIDENCIA
}
