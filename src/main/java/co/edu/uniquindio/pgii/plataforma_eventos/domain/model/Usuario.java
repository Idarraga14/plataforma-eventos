package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.List;

public class Usuario {
    private String idUsuario;
    private String nombreCompleto;
    private String correo;
    private String numeroTelefono;
    private List<MedioPago> mediosPago;

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
}
