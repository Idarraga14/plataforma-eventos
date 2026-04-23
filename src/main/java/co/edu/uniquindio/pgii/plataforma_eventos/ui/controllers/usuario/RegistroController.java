package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistroController {

    private final PlataformaFacade facade = new PlataformaFacadeImpl();

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnRegistrar;
    @FXML private Hyperlink btnVolver;

    @FXML
    public void onRegistrarClick(ActionEvent event) {
        String nombre    = txtNombre.getText().trim();
        String correo    = txtCorreo.getText().trim();
        String telefono  = txtTelefono.getText().trim();
        String password  = txtPassword.getText();
        String confirm   = txtConfirmPassword.getText();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            mostrarError("Nombre, correo y contraseña son obligatorios.");
            return;
        }
        if (!password.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        try {
            Usuario nuevo = facade.registrarUsuario(nombre, correo, telefono, password);
            SessionManager.getInstance().login(nuevo);

            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            ViewNavigator.cargarVistaUsuario("ExplorarEventosView.fxml", stage);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    public void onVolverClick(ActionEvent event) {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error de Registro");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
