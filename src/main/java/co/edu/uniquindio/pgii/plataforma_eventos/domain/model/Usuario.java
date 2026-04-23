package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Usuario {
    private String idUsuario;
    private String nombreCompleto;
    private String correo;
    private String password;
    private String numeroTelefono;
    private boolean esAdmin;
    private List<MedioPago> mediosPago;

    public Usuario(String nombreCompleto, String correo, String numeroTelefono, String password, boolean esAdmin) {
        this.idUsuario = UUID.randomUUID().toString();
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.numeroTelefono = numeroTelefono;
        this.password = password;
        this.mediosPago = new ArrayList<>();
        this.esAdmin = esAdmin;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public List<MedioPago> getMediosPago() {
        return mediosPago;
    }

    public String getPassword() {
        return password;
    }

    public boolean getEsAdmin() {
        return esAdmin;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
