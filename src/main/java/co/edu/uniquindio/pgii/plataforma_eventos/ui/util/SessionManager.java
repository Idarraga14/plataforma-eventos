package co.edu.uniquindio.pgii.plataforma_eventos.ui.util;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;

public class SessionManager {
    private static SessionManager instancia;
    private Usuario usuarioActual;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    public void login(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void logout() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
