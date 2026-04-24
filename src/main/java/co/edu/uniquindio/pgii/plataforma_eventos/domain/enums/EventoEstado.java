package co.edu.uniquindio.pgii.plataforma_eventos.domain.enums;

/**
 * Modela el ciclo de vida de publicación de un {@code Evento} en la plataforma.
 *
 * <p>El administrador controla las transiciones de estado a través de
 * {@code AdministracionFacade#actualizarEstadoEvento}. Sólo los eventos en estado
 * {@code PUBLICADO} son visibles en el catálogo de usuario y permiten la venta de entradas.</p>
 *
 * <p>[Requerimiento: RF-012] - El administrador gestiona el estado de publicación de cada
 * evento (BORRADOR → PUBLICADO → PAUSADO / CANCELADO / FINALIZADO) desde el módulo de
 * administración de eventos.</p>
 * <p>[Requerimiento: RF-002] - El catálogo de eventos filtrado para el usuario sólo incluye
 * aquellos cuyo estado es {@code PUBLICADO}.</p>
 * <p>[Requerimiento: RF-014] - Al cancelar un evento ({@code CANCELADO}), el sistema
 * dispara el proceso de notificación a compradores con entradas activas.</p>
 */
public enum EventoEstado {

    /** Evento creado pero aún no visible para el público; en configuración inicial. */
    BORRADOR,

    /** Evento activo y visible en el catálogo; venta de entradas habilitada. */
    PUBLICADO,

    /** Venta de entradas suspendida temporalmente; el evento sigue visible pero no vendible. */
    PAUSADO,

    /** Evento cancelado definitivamente; se inicia proceso de reembolso a compradores. */
    CANCELADO,

    /** Evento ya celebrado; ventas cerradas y entradas no válidas para acceso. */
    FINALIZADO
}
