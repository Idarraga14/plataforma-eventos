package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    private final PlataformaFacade facade = new PlataformaFacadeImpl();

    // --- COMPONENTES FXML ---
    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblMensajeError;
    @FXML
    private Button btnLogin;
    @FXML
    private Hyperlink btnIrARegistro;

    // --- HANDLERS DE EVENTOS ---
    @FXML
    public void onLoginClick(ActionEvent event) {
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText();

        if (correo.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, ingrese su correo y contraseña");
            return;
        }

        try {
            // Delegamos la lógica al Facade
            Usuario usuarioLogueado = facade.login(correo, password);

            // Inversión de Control: Guardamos la sesión
            SessionManager.getInstance().login(usuarioLogueado);

            // Obtenemos la ventana actual para navegar
            Stage stage = (Stage) btnLogin.getScene().getWindow();

            // Enrutamiento dinámico basado en el dominio
            if (usuarioLogueado.getEsAdmin()) {
                System.out.println("Usuario administrador logueado: " + usuarioLogueado.getNombreCompleto());
                ViewNavigator.cargarVistaAdmin("AdminDashboardView.fxml", stage);
            } else {
                System.out.println("Usuario logueado: " + usuarioLogueado.getNombreCompleto());
                ViewNavigator.cargarVistaUsuario("ExplorarEventosView.fxml", stage);
            }

        } catch (IllegalArgumentException e) {
            // Atrapamos el error de negocio y se lo mostramos al usuario
            mostrarError(e.getMessage());
        }
    }

    /**
     * Método auxiliar para mostrar alertas sin ensuciar la lógica principal
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Autenticación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void onIrARegistroClick(ActionEvent event) {
        // TODO: Navegar a la vista de Registro
    }
}