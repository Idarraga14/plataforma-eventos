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
    private List<MedioPago> mediosPago;

    public Usuario(String nombreCompleto, String correo, String numeroTelefono) {
        this.idUsuario = UUID.randomUUID().toString();
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.numeroTelefono = numeroTelefono;
        this.mediosPago = new ArrayList<>();
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
}
