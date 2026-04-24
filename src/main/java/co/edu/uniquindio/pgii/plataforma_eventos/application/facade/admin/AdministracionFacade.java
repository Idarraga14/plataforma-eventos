package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.observer.EventoObserver;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.IncidenciaEntidadAfectada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Incidencia;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ReporteOperativo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Fachada de la capa de aplicación para todas las operaciones del administrador de la plataforma.
 *
 * <p>Centraliza en un único punto de acceso todos los casos de uso administrativos:
 * CRUD de eventos, usuarios, recintos y zonas; gestión de asientos físicos y por evento;
 * reasignación y reembolso de compras; registro de incidencias; cálculo de métricas del
 * dashboard; generación de reportes operativos; y gestión de observers. Los controladores
 * JavaFX del módulo admin dependen exclusivamente de esta interfaz.</p>
 *
 * <p>[Requerimiento: RF-012] - {@link #crearEvento}, {@link #actualizarEstadoEvento},
 * {@link #eliminarEvento} y {@link #listarEventos} cubren el CRUD completo de eventos.</p>
 * <p>[Requerimiento: RF-013] - {@link #crearRecinto}, {@link #agregarZona} y sus operaciones
 * sobre asientos cubren la gestión de infraestructura física.</p>
 * <p>[Requerimiento: RF-015] - {@link #listarCompras}, {@link #confirmarCompra} y
 * {@link #reembolsarCompra} cubren la gestión administrativa de compras.</p>
 * <p>[Requerimiento: RF-016] - {@link #reasignarAsiento} permite reubicar a un comprador
 * en otro asiento del mismo evento.</p>
 * <p>[Requerimiento: RF-018] - {@link #bloquearAsiento}/{@link #habilitarAsiento} (físico)
 * y {@link #bloquearAsientoEnEvento}/{@link #habilitarAsientoEnEvento} (por evento)
 * implementan la dualidad de gestión de asientos.</p>
 * <p>[Requerimiento: RF-019] - {@link #registrarIncidencia} y {@link #listarIncidencias}
 * cubren el módulo de incidencias.</p>
 * <p>[Requerimiento: RF-046] - {@link #generarReporteOperativo} produce el reporte de
 * ingresos con métricas de ventas, extras y top eventos.</p>
 * <p>[Patrón: Facade] - Actúa como la <strong>Fachada</strong> del subsistema admin;
 * {@code AdministracionFacadeImpl} es el implementador concreto.</p>
 * <p>[Patrón: Observer] - {@link #registrarObserver} y {@link #desregistrarObserver}
 * delegan en el sujeto ({@code PlataformaEventosSingleton}) el registro de controladores
 * que reactivan su UI ante cambios de aforo.</p>
 */
public interface AdministracionFacade {

    // === EVENTOS (Builder) ===

    /**
     * Crea un nuevo evento usando el {@code Evento.EventoBuilder} y lo persiste.
     *
     * <p>[Requerimiento: RF-012] / [Patrón: Builder] - Construye el evento validando
     * las precondiciones (nombre, fecha futura, recinto, categoría) y registra el nuevo
     * objeto junto con su inventario de asientos inicializado.</p>
     */
    Evento crearEvento(String nombre, EventoCategoria categoria, String descripcion,
                       String ciudad, LocalDateTime fecha, String idRecinto);

    /**
     * Cambia el estado de publicación de un evento y notifica a los observers.
     *
     * <p>[Requerimiento: RF-012] / [Patrón: Observer] - Al cambiar el estado se invoca
     * {@code PlataformaEventosSingleton#notificarCambio} para actualizar el dashboard.</p>
     */
    void actualizarEstadoEvento(String idEvento, EventoEstado nuevoEstado);

    /**
     * Elimina un evento del sistema si no tiene compras activas.
     *
     * <p>[Requerimiento: RF-012] - Protege la integridad referencial: no permite eliminar
     * un evento con compras en estado CREADA, PAGADA o CONFIRMADA.</p>
     */
    void eliminarEvento(String idEvento);

    /** Devuelve todos los eventos registrados en el sistema. */
    List<Evento> listarEventos();

    // === USUARIOS ===

    /**
     * Crea un usuario (regular o administrador) y lo persiste.
     *
     * <p>[Requerimiento: RF-017] - Operación administrativa para gestionar las cuentas
     * de usuario desde el módulo de administración.</p>
     */
    Usuario crearUsuario(String nombre, String correo, String telefono, String password, boolean admin);

    /** Actualiza nombre, correo y teléfono de un usuario existente. */
    void actualizarUsuario(String idUsuario, String nombre, String correo, String telefono);

    /**
     * Elimina un usuario del sistema.
     * No permite eliminar administradores.
     */
    void eliminarUsuario(String idUsuario);

    /** Devuelve todos los usuarios registrados en el sistema. */
    List<Usuario> listarUsuarios();

    // === RECINTOS / ZONAS / ASIENTOS ===

    /**
     * Crea un nuevo recinto físico y lo persiste.
     *
     * <p>[Requerimiento: RF-013] - Las zonas y asientos se añaden posteriormente mediante
     * {@link #agregarZona} y los métodos de gestión de asientos.</p>
     */
    Recinto crearRecinto(String nombre, String direccion, String ciudad);

    /**
     * Elimina un recinto si no está asociado a ningún evento existente.
     *
     * <p>[Requerimiento: RF-013] - Protege la integridad referencial.</p>
     */
    void eliminarRecinto(String idRecinto);

    /** Devuelve todos los recintos registrados en el sistema. */
    List<Recinto> listarRecintos();

    /**
     * Añade una nueva zona al recinto indicado.
     *
     * <p>[Requerimiento: RF-013] - Las zonas definen los sectores con precio y capacidad propios.</p>
     */
    Zona agregarZona(String idRecinto, String nombre, int capacidad, double precioBase);

    /**
     * Gestión física: marca el asiento como fuera de servicio en el recinto.
     * Afecta a todos los eventos futuros creados a partir de ese recinto.
     *
     * <p>[Requerimiento: RF-018] - Para mantenimiento o rotura permanente del asiento físico.</p>
     */
    void bloquearAsiento(String idAsiento);

    /**
     * Gestión física: devuelve el asiento al servicio en el recinto.
     *
     * <p>[Requerimiento: RF-018] - Revierte el bloqueo físico del asiento.</p>
     */
    void habilitarAsiento(String idAsiento);

    /**
     * Gestión por evento: bloquea el asiento sólo en el inventario del evento indicado.
     * No afecta a otros eventos del mismo recinto.
     *
     * <p>[Requerimiento: RF-018] - Para reservas de organizador o mantenimiento temporal
     * en una función específica.</p>
     */
    void bloquearAsientoEnEvento(String idEvento, String idAsiento);

    /**
     * Gestión por evento: libera el asiento bloqueado sólo en el inventario del evento indicado.
     *
     * <p>[Requerimiento: RF-018] - Revierte el bloqueo por evento sin afectar otras funciones.</p>
     */
    void habilitarAsientoEnEvento(String idEvento, String idAsiento);

    /**
     * Reasigna a un comprador de un asiento a otro dentro del mismo evento.
     *
     * <p>[Requerimiento: RF-016] - Sólo válido para compras en estado PAGADA o CONFIRMADA;
     * el asiento destino debe estar DISPONIBLE.</p>
     */
    void reasignarAsiento(String idCompra, String idAsientoAntiguo, String idAsientoNuevo);

    // === COMPRAS ===

    /** Devuelve todas las compras registradas en el sistema. */
    List<Compra> listarCompras();

    /**
     * Inicia el reembolso de una compra pagada o confirmada.
     *
     * <p>[Requerimiento: RF-011] / [Patrón: State] - Delega en el estado actual de la
     * compra; libera los asientos y notifica a los observers.</p>
     */
    void reembolsarCompra(String idCompra);

    /**
     * Confirma una compra en estado PAGADA, activando definitivamente las entradas.
     *
     * <p>[Requerimiento: RF-006] / [Patrón: State] - Transición PAGADA → CONFIRMADA.</p>
     */
    void confirmarCompra(String idCompra);

    // === INCIDENCIAS ===

    /**
     * Registra una nueva incidencia en el sistema.
     *
     * <p>[Requerimiento: RF-019] - Vincula el problema con la entidad afectada
     * (EVENTO, COMPRA o USUARIO) y lo persiste como no resuelto.</p>
     */
    Incidencia registrarIncidencia(String tipo, String descripcion,
                                   IncidenciaEntidadAfectada entidad,
                                   String idEntidad, String reportadoPor);

    /** Devuelve todas las incidencias registradas en el sistema. */
    List<Incidencia> listarIncidencias();

    // === MÉTRICAS (Dashboard) ===

    /** @return número de eventos en estado {@code PUBLICADO}. [RF-012] */
    long contarEventosPublicados();

    /** @return suma de ventas (PAGADAS + CONFIRMADAS) en el período dado. [RF-015] */
    double totalVentasPeriodo(LocalDate desde, LocalDate hasta);

    /** @return mapa mes → ingresos, para el gráfico de líneas del Dashboard. [RF-012] */
    Map<String, Double> ingresosPorMes();

    /** @return mapa zona → porcentaje de ocupación para el gráfico de pastel. [RF-013] */
    Map<String, Double> ocupacionPorZona(String idEvento);

    /** @return mapa nombre-extra → ingresos totales para el gráfico de barras. [RF-004] */
    Map<String, Double> ingresosPorServicioAdicional();

    /** @return número total de usuarios registrados. [RF-017] */
    long contarUsuarios();

    /** @return número de incidencias no resueltas. [RF-019] */
    long contarIncidenciasAbiertas();

    // === REPORTES OPERATIVOS ===

    /**
     * Genera un reporte operativo con métricas de ventas, extras y top eventos del período.
     *
     * <p>[Requerimiento: RF-046] - Usado por {@code AdminReportesController} para poblar
     * la vista de reportes y exportar en PDF/CSV mediante los adaptadores admin.</p>
     */
    ReporteOperativo generarReporteOperativo(LocalDate desde, LocalDate hasta);

    // === OBSERVER ===

    /**
     * Registra un observer para recibir notificaciones de cambios de aforo.
     *
     * <p>[Patrón: Observer] - Delegado al sujeto {@code PlataformaEventosSingleton}.</p>
     */
    void registrarObserver(EventoObserver obs);

    /**
     * Desregistra un observer previamente suscrito.
     *
     * <p>[Patrón: Observer] - Invocado al destruir el controlador para evitar referencias huérfanas.</p>
     */
    void desregistrarObserver(EventoObserver obs);
}
