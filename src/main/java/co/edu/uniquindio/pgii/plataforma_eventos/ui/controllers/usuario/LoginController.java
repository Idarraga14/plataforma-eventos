package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Se asigna desde el exterior antes de mostrar la vista.
    // Ej: loader.getController().setPlataformaFacade(facade);
    private PlataformaFacade plataformaFacade;

    // --- COMPONENTES FXML ---
    @FXML private TextField     txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblMensajeError;
    @FXML private Button        btnLogin;
    @FXML private Hyperlink     btnIrARegistro;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblMensajeError.setVisible(false);
        lblMensajeError.setManaged(false);
    }

    /** Inyecta la fachada de usuario. */
    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    // --- HANDLERS DE EVENTOS ---

    @FXML
    public void onLoginClick(ActionEvent event) {
        // TODO: Validar campos no vacíos
        // TODO: Delegar → plataformaFacade.login(txtCorreo.getText(), txtPassword.getText())
        // TODO: Si éxito  → SessionManager.getInstance().login(usuario) y navegar a ExplorarEventosView
        // TODO: Si falla  → mostrar lblMensajeError con el mensaje correspondiente
    }

    @FXML
    public void onIrARegistroClick(ActionEvent event) {
        // TODO: Navegar a la vista de Registro
    }
}