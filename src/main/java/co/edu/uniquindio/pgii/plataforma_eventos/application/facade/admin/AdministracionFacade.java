package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin;

public interface AdministracionFacade {
    // CRUD de Eventos
    void crearEvento();
    void actualizarEvento();
    void eliminarEvento();

    // CRUD de Usuarios
    void crearUsuario();
    void actualizarUsuario();
    void eliminarUsuario();

    // CRUD de Recintos
    void crearRecinto();
    void actualizarRecinto();
    void eliminarRecinto();

    // CRUD de Zonas
    void crearZona();
    void actualizarZona();
    void eliminarZona();

    // CRUD de Asientos
    void crearAsiento();
    void actualizarAsiento();
    void eliminarAsiento();

    // CRUD de Compras
    void cancelarCompra();
    void reembolsarCompra();
}
