package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa a un participante registrado en la plataforma.
 *
 * <p>Un usuario puede ser un comprador regular o un administrador del sistema,
 * diferenciados por el campo {@code esAdmin}. Mantiene sus propios medios de pago
 * registrados y es el titular de todas las compras que realice.</p>
 *
 * <p>[Requerimiento: RF-001] - El usuario se crea durante el proceso de registro y
 * sus credenciales ({@code correo} + {@code password}) se verifican en el login
 * a través de {@code PlataformaFacade#login(String, String)}.</p>
 * <p>[Requerimiento: RF-008] - El perfil de usuario expone y permite editar
 * {@code nombreCompleto}, {@code correo} y {@code numeroTelefono}.</p>
 * <p>[Requerimiento: RF-011] - Los {@link MedioPago} registrados son la fuente de
 * selección durante el proceso de pago de una compra.</p>
 */
public class Usuario {

    /** Identificador único del usuario, generado al registrarse. */
    private String idUsuario;

    /** Nombre completo del usuario (editable desde su perfil). */
    private String nombreCompleto;

    /** Correo electrónico; sirve como nombre de usuario para el login. */
    private String correo;

    /** Contraseña en texto plano (simulación académica; en producción debería ser hash). */
    private String password;

    /** Número de teléfono de contacto del usuario. */
    private String numeroTelefono;

    /** Indica si el usuario tiene privilegios de administrador en la plataforma. */
    private boolean esAdmin;

    /** Lista de medios de pago registrados por el usuario (tarjetas, etc.). */
    private List<MedioPago> mediosPago;

    /**
     * Crea un nuevo usuario con sus datos básicos de registro.
     *
     * <p>[Requerimiento: RF-001] - Invocado desde {@code PlataformaFacade#registrarUsuario(...)}
     * tras validar que el correo no esté ya registrado en el sistema.</p>
     *
     * @param nombreCompleto nombre completo del usuario
     * @param correo         correo electrónico (debe ser único en la plataforma)
     * @param numeroTelefono teléfono de contacto
     * @param password       contraseña del usuario
     * @param esAdmin        {@code true} si es administrador del sistema
     */
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
