package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminUsuariosController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Admin actual: SessionManager.getInstance().getUsuarioActual()
    private AdministracionFacade administracionFacade;

    // --- COMPONENTES FXML ---
    @FXML private TextField                    txtBuscarUsuario;
    @FXML private Button                       btnBuscarUsuario;
    @FXML private Button                       btnLimpiarBusqueda;

    @FXML private TableView<Usuario>           tblUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colTelefono;
    @FXML private TableColumn<Usuario, String> colEstado;

    @FXML private Button btnCrearUsuario;
    @FXML private Button btnEditarUsuario;
    @FXML private Button btnBloquearUsuario;

    @FXML private Label  lblTotalUsuarios;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Configurar cellValueFactory de cada columna
        // TODO: Cargar lista completa de usuarios:
        //       tblUsuarios.setItems(FXCollections.observableArrayList(administracionFacade.listarUsuarios()));
        // TODO: Actualizar lblTotalUsuarios con el tamaño de la lista
    }

    /** Inyecta la fachada de administración. */
    public void setAdministracionFacade(AdministracionFacade administracionFacade) {
        this.administracionFacade = administracionFacade;
    }

    // --- HANDLERS ---

    @FXML
    public void onBuscarUsuarioAction(ActionEvent event) {
        onBuscarUsuarioClick(event);
    }

    @FXML
    public void onBuscarUsuarioClick(ActionEvent event) {
        // TODO: Filtrar tblUsuarios por txtBuscarUsuario.getText()
    }

    @FXML
    public void onLimpiarBusquedaClick(ActionEvent event) {
        // TODO: Limpiar txtBuscarUsuario y recargar lista completa
    }

    @FXML
    public void onCrearUsuarioClick(ActionEvent event) {
        // TODO: Abrir diálogo/formulario de creación de usuario
        // TODO: Llamar a administracionFacade.crearUsuario() y refrescar tabla
    }

    @FXML
    public void onEditarUsuarioClick(ActionEvent event) {
        // TODO: Obtener usuario seleccionado: tblUsuarios.getSelectionModel().getSelectedItem()
        // TODO: Abrir diálogo con datos precargados
        // TODO: Llamar a administracionFacade.actualizarUsuario() y refrescar tabla
    }

    @FXML
    public void onBloquearUsuarioClick(ActionEvent event) {
        // TODO: Obtener usuario seleccionado
        // TODO: Confirmar acción con un Alert
        // TODO: Llamar a administracionFacade.actualizarUsuario() alternando el estado de bloqueo
        // TODO: Refrescar la fila en la tabla
    }
}