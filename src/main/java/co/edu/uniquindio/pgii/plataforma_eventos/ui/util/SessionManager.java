package co.edu.uniquindio.pgii.plataforma_eventos.ui.util;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static SessionManager instancia;

    private Usuario usuarioActual;
    private Evento eventoSeleccionado;
    private Zona zonaSeleccionadaTemporal;
    private List<Asiento> asientosSeleccionadosTemporales = new ArrayList<>();
    private int cantidadEntradasZonaTemporal = 1;
    private List<String> extrasSeleccionados = new ArrayList<>();

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

    public void setEventoSeleccionado(Evento evento) {
        this.eventoSeleccionado = evento;
    }

    public Evento getEventoSeleccionado() {
        return eventoSeleccionado;
    }

    public void limpiarEventoSeleccionado() {
        this.eventoSeleccionado = null;
    }

    public void setZonaSeleccionada(Zona zona) {
        this.zonaSeleccionadaTemporal = zona;
    }

    public Zona getZonaSeleccionada() {
        return zonaSeleccionadaTemporal;
    }

    public void setAsientosSeleccionados(List<Asiento> asientos) {
        this.asientosSeleccionadosTemporales = asientos;
    }

    public List<Asiento> getAsientosSeleccionados() {
        return asientosSeleccionadosTemporales;
    }

    public void setCantidadEntradas(int cantidad) {
        this.cantidadEntradasZonaTemporal = cantidad;
    }

    public int getCantidadEntradas() {
        return cantidadEntradasZonaTemporal;
    }

    public void setExtrasSeleccionados(List<String> extras) {
        this.extrasSeleccionados = extras;
    }

    public List<String> getExtrasSeleccionados() {
        return extrasSeleccionados;
    }
}
