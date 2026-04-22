package co.edu.uniquindio.pgii.plataforma_eventos.ui.util;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;

public class SessionManager {
    private static SessionManager instancia;

    private Usuario usuarioActual;
    private Evento eventoSeleccionado;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    // METODOS DE ACCESO A LA SESION
    public void login(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void logout() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // METODOS DE EVENTO
    public void setEventoSeleccionado(Evento evento) {
        this.eventoSeleccionado = evento;
    }

    public Evento getEventoSeleccionado() {
        return eventoSeleccionado;
    }

    public void limpiarEventoSeleccionado() {
        this.eventoSeleccionado = null;
    }
}
