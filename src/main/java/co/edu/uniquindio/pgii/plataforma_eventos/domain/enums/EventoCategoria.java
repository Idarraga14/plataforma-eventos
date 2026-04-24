package co.edu.uniquindio.pgii.plataforma_eventos.domain.enums;

/**
 * Clasifica los tipos de eventos disponibles en la plataforma.
 *
 * <p>La categoría es asignada por el administrador al crear un evento a través de
 * {@code AdministracionFacade#crearEvento} y es expuesta al usuario como criterio
 * de exploración y filtrado en la vista de catálogo.</p>
 *
 * <p>[Requerimiento: RF-002] - El usuario puede filtrar el catálogo de eventos por
 * categoría ({@code TEATRO}, {@code CONCIERTO}, {@code CONFERENCIA}) para encontrar
 * los espectáculos de su interés.</p>
 * <p>[Requerimiento: RF-012] - El administrador selecciona la categoría al registrar
 * o editar un evento en el módulo CRUD de eventos.</p>
 */
public enum EventoCategoria {

    /** Obras de teatro, musicales y presentaciones escénicas en vivo. */
    TEATRO,

    /** Conciertos, festivales de música y espectáculos musicales. */
    CONCIERTO,

    /** Conferencias, seminarios, charlas y eventos académicos o corporativos. */
    CONFERENCIA
}
