package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador JavaFX de la pantalla de autenticación de usuarios.
 *
 * <p>Recoge las credenciales (correo y contraseña), las delega a {@link PlataformaFacade#login}
 * y, según el rol del usuario devuelto, enruta al Dashboard de administración o a la vista de
 * exploración de eventos. Ante credenciales inválidas muestra un {@code Alert} de error sin
 * exponer detalles internos del sistema.</p>
 *
 * <p>[Requerimiento: RF-001] - Implementa la autenticación de usuarios y el enrutamiento
 * diferenciado por rol (admin → {@code AdminDashboardView}, cliente → {@code ExplorarEventosView}).</p>
 * <p>[Patrón: Facade] - Consume {@link PlataformaFacade} como punto de entrada único a la
 * lógica de autenticación; el controlador no accede directamente al repositorio.</p>
 */
public class LoginController {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    private final PlataformaFacade facade = new PlataformaFacadeImpl();

    // --- COMPONENTES FXML ---
    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Hyperlink btnIrARegistro;

    // --- HANDLERS DE EVENTOS ---
    @FXML
    public void onLoginClick() {
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
    public void onIrARegistroClick() {
        Stage stage = (Stage) btnIrARegistro.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("RegistroView.fxml", stage);
    }
}