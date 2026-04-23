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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AdministracionFacade {

    // === EVENTOS (Builder) ===
    Evento crearEvento(String nombre, EventoCategoria categoria, String descripcion,
                       String ciudad, LocalDateTime fecha, String idRecinto);
    void actualizarEstadoEvento(String idEvento, EventoEstado nuevoEstado);
    void eliminarEvento(String idEvento);
    List<Evento> listarEventos();

    // === USUARIOS ===
    Usuario crearUsuario(String nombre, String correo, String telefono, String password, boolean admin);
    void actualizarUsuario(String idUsuario, String nombre, String correo, String telefono);
    void eliminarUsuario(String idUsuario);
    List<Usuario> listarUsuarios();

    // === RECINTOS / ZONAS / ASIENTOS ===
    Recinto crearRecinto(String nombre, String direccion, String ciudad);
    void eliminarRecinto(String idRecinto);
    List<Recinto> listarRecintos();
    Zona agregarZona(String idRecinto, String nombre, int capacidad, double precioBase);

    // Gestión física (afecta el recinto: silla rota, fuera de servicio)
    void bloquearAsiento(String idAsiento);
    void habilitarAsiento(String idAsiento);

    // Gestión por evento (afecta solo el inventario de ese evento)
    void bloquearAsientoEnEvento(String idEvento, String idAsiento);
    void habilitarAsientoEnEvento(String idEvento, String idAsiento);

    // === COMPRAS ===
    List<Compra> listarCompras();
    void reembolsarCompra(String idCompra);
    void confirmarCompra(String idCompra);

    // === INCIDENCIAS ===
    Incidencia registrarIncidencia(String tipo, String descripcion,
                                   IncidenciaEntidadAfectada entidad,
                                   String idEntidad, String reportadoPor);
    List<Incidencia> listarIncidencias();

    // === MÉTRICAS (Dashboard) ===
    long contarEventosPublicados();
    double totalVentasPeriodo(LocalDate desde, LocalDate hasta);
    Map<String, Double> ingresosPorMes();
    Map<String, Double> ocupacionPorZona(String idEvento);
    Map<String, Double> ingresosPorServicioAdicional();
    long contarUsuarios();
    long contarIncidenciasAbiertas();

    // === OBSERVER ===
    void registrarObserver(EventoObserver obs);
    void desregistrarObserver(EventoObserver obs);
}
