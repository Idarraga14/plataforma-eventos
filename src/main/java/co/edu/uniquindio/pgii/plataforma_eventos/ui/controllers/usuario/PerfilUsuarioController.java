package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.MedioPago;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX de la pantalla de perfil del usuario autenticado.
 *
 * <p>Permite al usuario ver y editar sus datos personales (nombre, correo, teléfono) y
 * gestionar sus medios de pago (tarjetas de crédito/débito): agregar nuevas y eliminar
 * las existentes. Los cambios de perfil se persisten en el objeto {@link Usuario} de la
 * sesión vía {@link PlataformaFacade#actualizarPerfil}.</p>
 *
 * <p>[Requerimiento: RF-008] - Implementa la consulta y actualización de datos personales
 * del usuario, incluyendo la gestión de medios de pago.</p>
 * <p>[Patrón: Facade] - Delega la actualización del perfil a {@link PlataformaFacade}
 * para mantener la validación de negocio fuera del controlador.</p>
 */
public class PerfilUsuarioController implements Initializable {

    private final PlataformaFacade plataformaFacade = new PlataformaFacadeImpl();

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtTelefono;
    @FXML
    private Label lblMensajePerfil;
    @FXML
    private Button btnGuardarPerfil;

    @FXML
    private TableView<MedioPago> tblMediosPago;
    @FXML
    private TableColumn<MedioPago, String> colTitular;
    @FXML
    private TableColumn<MedioPago, String> colDigitos;
    @FXML
    private TextField txtTitularTarjeta;
    @FXML
    private TextField txtNumeroTarjeta;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblMensajePerfil.setVisible(false);
        lblMensajePerfil.setManaged(false);

        colTitular.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitular()));
        colDigitos.setCellValueFactory(data ->
                new SimpleStringProperty("•••• " + data.getValue().getUltimosCuatro()));

        Usuario actual = SessionManager.getInstance().getUsuarioActual();
        if (actual != null) {
            txtNombre.setText(actual.getNombreCompleto());
            txtCorreo.setText(actual.getCorreo());
            txtTelefono.setText(actual.getNumeroTelefono());
            refrescarMedios();
        }
    }

    // --- HANDLERS ---

    @FXML
    public void onGuardarPerfilClick() {
        Usuario actual = SessionManager.getInstance().getUsuarioActual();
        if (actual == null) return;

        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();
        String tel = txtTelefono.getText();

        if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank()) {
            mostrarMensaje("Nombre y correo son obligatorios.", true);
            return;
        }

        String prevNombre = actual.getNombreCompleto();
        String prevCorreo = actual.getCorreo();
        String prevTel = actual.getNumeroTelefono();

        actual.setNombreCompleto(nombre.trim());
        actual.setCorreo(correo.trim());
        actual.setNumeroTelefono(tel == null ? "" : tel.trim());

        try {
            plataformaFacade.actualizarPerfil(actual);
            mostrarMensaje("Perfil actualizado correctamente.", false);
        } catch (RuntimeException ex) {
            actual.setNombreCompleto(prevNombre);
            actual.setCorreo(prevCorreo);
            actual.setNumeroTelefono(prevTel);
            mostrarMensaje("No se pudo guardar: " + ex.getMessage(), true);
        }
    }

    @FXML
    public void onAgregarMedioClick() {
        Usuario actual = SessionManager.getInstance().getUsuarioActual();
        if (actual == null) return;

        String titular = txtTitularTarjeta.getText();
        String numero = txtNumeroTarjeta.getText();

        if (titular == null || titular.isBlank() || numero == null || numero.length() < 4) {
            mostrarMensaje("Titular y número de tarjeta (mínimo 4 dígitos) son obligatorios.", true);
            return;
        }

        actual.getMediosPago().add(new MedioPago(titular.trim(), numero.trim()));
        txtTitularTarjeta.clear();
        txtNumeroTarjeta.clear();
        refrescarMedios();
        mostrarMensaje("Tarjeta agregada.", false);
    }

    @FXML
    public void onEliminarMedioClick() {
        MedioPago seleccionado = tblMediosPago.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("Selecciona una tarjeta para eliminar.", true);
            return;
        }
        Usuario actual = SessionManager.getInstance().getUsuarioActual();
        actual.getMediosPago().removeIf(mp -> mp.getIdMedioPago().equals(seleccionado.getIdMedioPago()));
        refrescarMedios();
    }

    // --- NAVEGACIÓN ---

    @FXML
    public void onNavEventos() {
        Stage stage = (Stage) btnGuardarPerfil.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("ExplorarEventosView.fxml", stage);
    }

    @FXML
    public void onNavHistorial() {
        Stage stage = (Stage) btnGuardarPerfil.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("HistorialComprasView.fxml", stage);
    }

    @FXML
    public void onNavPerfil() {
        // ya estamos aquí
    }

    @FXML
    public void onCerrarSesion() {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) btnGuardarPerfil.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }

    // --- Helpers ---

    private void refrescarMedios() {
        Usuario actual = SessionManager.getInstance().getUsuarioActual();
        tblMediosPago.setItems(FXCollections.observableArrayList(actual.getMediosPago()));
    }

    private void mostrarMensaje(String texto, boolean error) {
        lblMensajePerfil.setText(texto);
        lblMensajePerfil.setTextFill(error ? Color.CRIMSON : Color.GREEN);
        lblMensajePerfil.setVisible(true);
        lblMensajePerfil.setManaged(true);
    }
}
