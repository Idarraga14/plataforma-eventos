package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminRecintosController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Admin actual: SessionManager.getInstance().getUsuarioActual()
    private AdministracionFacade administracionFacade;

    // --- COMPONENTES FXML ---
    @FXML private TextField                     txtBuscarRecinto;
    @FXML private Button                        btnBuscarRecinto;

    @FXML private TableView<Recinto>            tblRecintos;
    @FXML private TableColumn<Recinto, String>  colNombreRecinto;
    @FXML private TableColumn<Recinto, String>  colDireccion;
    @FXML private TableColumn<Recinto, String>  colCiudad;
    @FXML private TableColumn<Recinto, Integer> colAforo;
    @FXML private TableColumn<Recinto, Integer> colZonas;

    @FXML private Button btnCrearRecinto;
    @FXML private Button btnEditarRecinto;
    @FXML private Button btnEliminarRecinto;
    @FXML private Button btnAbrirModalAsientos;

    @FXML private Label  lblHintDobleClick;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Configurar cellValueFactory de cada columna

        // TODO: Cargar todos los recintos:
        //       tblRecintos.setItems(FXCollections.observableArrayList(administracionFacade.listarRecintos()));
    }

    /** Inyecta la fachada de administración. */
    public void setAdministracionFacade(AdministracionFacade administracionFacade) {
        this.administracionFacade = administracionFacade;
    }

    // --- HANDLERS ---

    @FXML
    public void onBuscarRecintoAction(ActionEvent event) {
        onBuscarRecintoClick(event);
    }

    @FXML
    public void onBuscarRecintoClick(ActionEvent event) {
        // TODO: Filtrar tblRecintos por txtBuscarRecinto.getText()
    }

    @FXML
    public void onCrearRecintoClick(ActionEvent event) {
        // TODO: Abrir formulario/diálogo de creación de recinto
        // TODO: Llamar a administracionFacade.crearRecinto() y refrescar tabla
    }

    @FXML
    public void onEditarRecintoClick(ActionEvent event) {
        // TODO: Obtener recinto seleccionado: tblRecintos.getSelectionModel().getSelectedItem()
        // TODO: Abrir diálogo con datos precargados
        // TODO: Llamar a administracionFacade.actualizarRecinto() y refrescar tabla
    }

    @FXML
    public void onEliminarRecintoClick(ActionEvent event) {
        // TODO: Obtener recinto seleccionado
        // TODO: Confirmar con un Alert de tipo CONFIRMATION
        // TODO: Llamar a administracionFacade.eliminarRecinto() y refrescar tabla
    }

    /**
     * Abre la ventana secundaria (Stage modal) para el CRUD de asientos físicos
     * del recinto seleccionado. También se dispara con doble clic en la tabla.
     */
    @FXML
    public void onAbrirModalAsientos(ActionEvent event) {
        // TODO: Obtener recinto seleccionado: tblRecintos.getSelectionModel().getSelectedItem()
        // TODO: Abrir una nueva Stage modal con la vista de gestión de Zonas/Asientos
        //       pasando el Recinto seleccionado al controlador del modal
    }

    /**
     * Detecta doble clic sobre la tabla para abrir el modal de asientos.
     * Enlazado mediante onMouseClicked="#onTablaRecintoClick" en el FXML.
     */
    @FXML
    public void onTablaRecintoClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            // TODO: Llamar a onAbrirModalAsientos(null) reutilizando la lógica
        }
    }
}