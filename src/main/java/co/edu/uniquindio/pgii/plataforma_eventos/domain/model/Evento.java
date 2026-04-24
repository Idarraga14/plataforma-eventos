package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Agregado raíz que representa una función o espectáculo disponible en la plataforma.
 *
 * <p>Posee su propio <strong>inventario comercial</strong> de asientos ({@code inventarioAsientos}),
 * inicializado automáticamente a partir de la plantilla física del {@link Recinto} asignado.
 * Esto garantiza que los cambios de disponibilidad en una función no afecten a otras que
 * compartan el mismo recinto.</p>
 *
 * <p>[Requerimiento: RF-002] - Los eventos en estado {@link EventoEstado#PUBLICADO} son
 * listados en el catálogo que el usuario explora y filtra por categoría/ciudad/fecha.</p>
 * <p>[Requerimiento: RF-003] - El inventario de {@link AsientoEvento} de este objeto es
 * consultado durante la selección de asientos numerados en el flujo de compra.</p>
 * <p>[Requerimiento: RF-012] - El administrador crea eventos a través del facade de
 * administración, que internamente usa el {@link EventoBuilder} para construirlos.</p>
 * <p>[Requerimiento: RF-014] - Al cancelar un evento el administrador actualiza su
 * {@code estado} a {@link EventoEstado#CANCELADO} y notifica a los compradores.</p>
 * <p>[Patrón: Builder] - La instanciación se realiza exclusivamente a través de la clase
 * interna {@link EventoBuilder}, que valida las precondiciones antes de crear el objeto.</p>
 * <p>[Patrón: Observer] - Es el objeto del cual {@code PlataformaEventosSingleton} notifica
 * cambios de aforo a los {@code EventoObserver} suscritos (ej. Dashboard).</p>
 */
public class Evento {

    /** Identificador único del evento, generado en la construcción. */
    private final String idEvento;

    /** Nombre del espectáculo o función. */
    private final String nombre;

    /** Tipo de espectáculo (Teatro, Concierto, Conferencia). */
    private final EventoCategoria categoria;

    /** Descripción textual del evento para el catálogo. */
    private final String descripcion;

    /** Ciudad donde se celebra el evento. */
    private final String ciudad;

    /** Fecha y hora de inicio del evento. */
    private final LocalDateTime fecha;

    /** Estado de publicación del evento; el único campo mutable de este agregado. */
    private EventoEstado estado;

    /** Lista de políticas y condiciones de acceso del evento. */
    private final List<String> politicas;

    /** Recinto físico donde se celebra el evento. */
    private final Recinto recinto;

    /**
     * Inventario comercial: proyección de cada silla física del recinto para ESTA función.
     * Clave = {@code idAsiento} físico; valor = {@link AsientoEvento} con su disponibilidad.
     */
    private final Map<String, AsientoEvento> inventarioAsientos = new HashMap<>();

    /**
     * Constructor privado invocado únicamente por {@link EventoBuilder#build()}.
     * Delega la inicialización del inventario de asientos al método {@link #inicializarInventario()}.
     *
     * @param builder builder con todos los campos validados
     */
    private Evento(EventoBuilder builder) {
        this.idEvento = UUID.randomUUID().toString();
        this.nombre = builder.nombre;
        this.categoria = builder.categoria;
        this.descripcion = builder.descripcion;
        this.ciudad = builder.ciudad;
        this.fecha = builder.fecha;
        this.recinto = builder.recinto;
        this.estado = EventoEstado.BORRADOR;
        this.politicas = new ArrayList<>();
        inicializarInventario();
    }

    /**
     * Construye el inventario comercial a partir de los asientos físicos del recinto.
     * Cada {@link Asiento} del recinto genera un {@link AsientoEvento} con disponibilidad propia.
     */
    private void inicializarInventario() {
        for (Zona zona : recinto.getZonas()) {
            for (Asiento asiento : zona.getAsientos()) {
                inventarioAsientos.put(asiento.getIdAsiento(), new AsientoEvento(asiento));
            }
        }
    }

    /**
     * Devuelve el estado comercial de una silla para este evento concreto.
     *
     * <p>[Requerimiento: RF-003] - Consultado por {@code AsignacionPorAsientoStrategy}
     * para verificar si el asiento está disponible antes de reservarlo.</p>
     *
     * @param idAsiento identificador del asiento físico
     * @return {@link AsientoEvento} con la disponibilidad del asiento en esta función
     * @throws NoSuchElementException si el asiento no pertenece al inventario del evento
     */
    public AsientoEvento obtenerAsientoEvento(String idAsiento) {
        AsientoEvento ae = inventarioAsientos.get(idAsiento);
        if (ae == null) {
            throw new NoSuchElementException("Asiento no encontrado en el inventario del evento: " + idAsiento);
        }
        return ae;
    }

    /**
     * Devuelve todos los {@link AsientoEvento} que corresponden a una zona específica.
     * Usado por la UI para pintar la cuadrícula de asientos de la zona activa.
     *
     * @param zona zona del recinto cuyos asientos se desean consultar
     * @return colección de inventarios comerciales de los asientos de la zona
     */
    public Collection<AsientoEvento> getInventarioDe(Zona zona) {
        return zona.getAsientos().stream()
                .map(a -> inventarioAsientos.get(a.getIdAsiento()))
                .filter(ae -> ae != null)
                .toList();
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public EventoCategoria getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EventoEstado getEstado() {
        return estado;
    }

    public List<String> getPoliticas() {
        return politicas;
    }

    public Recinto getRecinto() {
        return recinto;
    }

    public void setEstado(EventoEstado estado) {
        this.estado = estado;
    }

    /**
     * Builder que construye instancias de {@link Evento} de forma fluida y validada.
     *
     * <p>[Requerimiento: RF-012] - El administrador configura todos los atributos del evento
     * a través de métodos encadenados antes de invocarlo; el {@code build()} valida las
     * precondiciones y devuelve un evento consistente listo para su publicación.</p>
     * <p>[Patrón: Builder] - Actúa como el <strong>Builder Concreto</strong> del patrón;
     * el constructor privado de {@code Evento} sólo es accesible desde aquí, forzando
     * el uso de esta clase para instanciar eventos.</p>
     */
    public static class EventoBuilder {

        /** Nombre del evento a construir. */
        private String nombre;

        /** Categoría del espectáculo. */
        private EventoCategoria categoria;

        /** Descripción del evento. */
        private String descripcion;

        /** Ciudad donde se celebrará el evento. */
        private String ciudad;

        /** Fecha y hora de inicio del evento. */
        private LocalDateTime fecha;

        /** Recinto donde se celebrará el evento. */
        private Recinto recinto;

        /** Políticas de acceso del evento. */
        private List<String> politicas = new ArrayList<>();

        /**
         * Establece el nombre del evento.
         *
         * @param nombre nombre del espectáculo
         * @return este builder (fluent API)
         */
        public EventoBuilder conNombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        /**
         * Establece la categoría del evento.
         *
         * @param categoria tipo de espectáculo
         * @return este builder (fluent API)
         */
        public EventoBuilder deCategoria(EventoCategoria categoria) {
            this.categoria = categoria;
            return this;
        }

        /**
         * Establece la descripción del evento.
         *
         * @param descripcion texto descriptivo para el catálogo
         * @return este builder (fluent API)
         */
        public EventoBuilder conDescripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        /**
         * Establece la ciudad del evento.
         *
         * @param ciudad ciudad de celebración
         * @return este builder (fluent API)
         */
        public EventoBuilder enCiudad(String ciudad) {
            this.ciudad = ciudad;
            return this;
        }

        /**
         * Establece la fecha y hora de inicio del evento.
         *
         * @param fecha fecha/hora de inicio (debe ser futura)
         * @return este builder (fluent API)
         */
        public EventoBuilder paraLaFecha(LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        /**
         * Asigna el recinto físico al evento.
         * El inventario de asientos se inicializará a partir de este recinto en {@code build()}.
         *
         * @param recinto recinto donde se celebrará el evento
         * @return este builder (fluent API)
         */
        public EventoBuilder enRecinto(Recinto recinto) {
            this.recinto = recinto;
            return this;
        }

        /**
         * Añade una política de acceso al evento.
         *
         * @param politica texto de la política (ej. "Prohibido ingreso de menores de edad")
         * @return este builder (fluent API)
         */
        public EventoBuilder agregandoPolitica(String politica) {
            this.politicas.add(politica);
            return this;
        }

        /**
         * Valida los campos obligatorios y construye la instancia de {@link Evento}.
         *
         * <p>Precondiciones verificadas: nombre no vacío, fecha futura, recinto asignado
         * y categoría especificada. Cualquier violación lanza {@link IllegalStateException}.</p>
         *
         * @return nueva instancia de {@code Evento} con su inventario de asientos inicializado
         * @throws IllegalStateException si algún campo obligatorio no está correctamente configurado
         */
        public Evento build() {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalStateException("El evento debe tener un nombre.");
            }
            if (fecha == null || fecha.isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("El evento debe tener una fecha válida en el futuro.");
            }
            if (recinto == null) {
                throw new IllegalStateException("El evento no puede existir sin un recinto físico asignado.");
            }
            if (categoria == null) {
                throw new IllegalStateException("Debe especificar la categoría del evento.");
            }

            return new Evento(this);
        }
    }
}
