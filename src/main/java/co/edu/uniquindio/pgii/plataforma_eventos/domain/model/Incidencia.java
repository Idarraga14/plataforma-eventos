package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.IncidenciaEntidadAfectada;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de un problema o incidente reportado por el administrador en la plataforma.
 *
 * <p>Una incidencia vincula un problema con una entidad afectada específica (un evento,
 * una compra o un usuario), facilitando el seguimiento y resolución. Nace siempre en
 * estado no resuelta y el administrador la marca como resuelta cuando se cierra.</p>
 *
 * <p>[Requerimiento: RF-019] - El administrador registra incidencias desde el módulo de
 * gestión de incidencias, especificando el tipo, descripción, entidad afectada y su ID.
 * El dashboard muestra el conteo de incidencias abiertas (no resueltas).</p>
 */
public class Incidencia {

    /** Identificador único de la incidencia. */
    private final String idIncidencia;

    /** Tipo o categoría del problema (ej. "Fallo técnico", "Cobro incorrecto"). */
    private final String tipo;

    /** Descripción detallada del problema reportado. */
    private final String descripcion;

    /** Fecha y hora en que se registró la incidencia. */
    private final LocalDateTime fecha;

    /** Tipo de entidad sobre la que recae el problema (EVENTO, COMPRA o USUARIO). */
    private final IncidenciaEntidadAfectada entidadAfectada;

    /** Identificador UUID de la entidad afectada (ID del evento, compra o usuario). */
    private final String idEntidadAfectada;

    /** Nombre o identificador de quien reportó la incidencia (generalmente el admin). */
    private final String reportadoPor;

    /** Indica si la incidencia ha sido cerrada/resuelta por el administrador. */
    private boolean resuelta;

    /**
     * Crea una nueva incidencia no resuelta con los datos del reporte.
     *
     * <p>[Requerimiento: RF-019] - Invocado desde {@code AdministracionFacade#registrarIncidencia(...)}
     * con los datos capturados en el formulario del módulo de incidencias.</p>
     *
     * @param tipo               categoría del problema
     * @param descripcion        detalle del incidente
     * @param entidadAfectada    tipo de entidad impactada (EVENTO / COMPRA / USUARIO)
     * @param idEntidadAfectada  ID de la entidad impactada
     * @param reportadoPor       quien registra la incidencia
     */
    public Incidencia(String tipo, String descripcion,
                      IncidenciaEntidadAfectada entidadAfectada,
                      String idEntidadAfectada, String reportadoPor) {
        this.idIncidencia = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
        this.entidadAfectada = entidadAfectada;
        this.idEntidadAfectada = idEntidadAfectada;
        this.reportadoPor = reportadoPor;
        this.resuelta = false;
    }

    /** @return identificador único de la incidencia */
    public String getIdIncidencia() { return idIncidencia; }

    /** @return tipo o categoría del problema reportado */
    public String getTipo() { return tipo; }

    /** @return descripción detallada del incidente */
    public String getDescripcion() { return descripcion; }

    /** @return fecha y hora de registro de la incidencia */
    public LocalDateTime getFecha() { return fecha; }

    /** @return tipo de entidad afectada (EVENTO, COMPRA o USUARIO) */
    public IncidenciaEntidadAfectada getEntidadAfectada() { return entidadAfectada; }

    /** @return ID de la entidad concreta afectada por la incidencia */
    public String getIdEntidadAfectada() { return idEntidadAfectada; }

    /** @return nombre o identificador de quien reportó el problema */
    public String getReportadoPor() { return reportadoPor; }

    /** @return {@code true} si la incidencia ha sido cerrada/resuelta */
    public boolean isResuelta() { return resuelta; }

    /**
     * Marca la incidencia como resuelta, cerrando el reporte.
     *
     * <p>[Requerimiento: RF-019] - Invocado por el administrador desde el módulo de
     * incidencias al confirmar que el problema ha sido solucionado.</p>
     */
    public void marcarResuelta() { this.resuelta = true; }
}
