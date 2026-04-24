package co.edu.uniquindio.pgii.plataforma_eventos.domain.enums;

/**
 * Define los posibles estados del inventario comercial de un asiento para un evento concreto.
 *
 * <p>Esta enumeración opera exclusivamente sobre {@code AsientoEvento} — la capa de inventario
 * por función — y nunca sobre el {@code Asiento} físico del recinto, garantizando así el aislamiento
 * entre la plantilla física (Recinto → Zona → Asiento) y la disponibilidad comercial por evento.</p>
 *
 * <p>[Requerimiento: RF-003] - Sólo los asientos en estado {@code DISPONIBLE} pueden ser
 * seleccionados por el usuario durante el flujo de compra de entradas numeradas.</p>
 * <p>[Requerimiento: RF-018] - El administrador cambia asientos al estado {@code BLOQUEADO}
 * para inhabilitar su venta en una función específica (mantenimiento, reserva de organizador).</p>
 * <p>[Patrón: State] - Actúa como el conjunto de estados concretos que puede adoptar el
 * objeto de contexto {@code AsientoEvento}, cuyas transiciones son validadas por la lógica
 * de negocio del propio {@code AsientoEvento} (bloquear / liberar / vender).</p>
 */
public enum AsientoEstado {

    /** El asiento está libre y puede ser adquirido en la función. */
    DISPONIBLE,

    /** El asiento ha sido seleccionado en un carrito activo pero aún no pagado. */
    RESERVADO,

    /** El asiento ha sido pagado y adjudicado a un comprador; no puede volver a venderse. */
    VENDIDO,

    /**
     * El asiento está inhabilitado para la venta en esta función.
     * Puede deberse a mantenimiento físico o a decisión administrativa por evento.
     */
    BLOQUEADO
}
