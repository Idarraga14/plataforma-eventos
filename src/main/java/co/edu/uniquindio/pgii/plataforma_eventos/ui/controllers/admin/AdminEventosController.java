package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminEventosController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Admin actual: SessionManager.getInstance().getUsuarioActual()
    private AdministracionFacade administracionFacade;

    // --- COMPONENTES FXML: Formulario ---
    @FXML private Label                   lblModoFormulario;
    @FXML private TextField               txtNombreEvento;
    @FXML private ComboBox<EventoCategoria> comboCategoria;
    @FXML private DatePicker              dtpFechaEvento;
    @FXML private TextField               txtCiudad;
    @FXML private ComboBox<Recinto>       comboRecinto;
    @FXML private ComboBox<EventoEstado>  comboEstado;
    @FXML private TextArea                txtDescripcion;
    @FXML private Button                  btnLimpiarFormulario;
    @FXML private Button                  btnGuardarEvento;

    // --- COMPONENTES FXML: Tabla ---
    @FXML private TableView<Evento>           tblEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colCategoria;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colCiudad;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private Button                      btnEditarEvento;
    @FXML private Button                      btnEliminarEvento;

    /** Evento seleccionado actualmente para edición; null cuando se crea uno nuevo. */
    private Evento eventoEnEdicion = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Poblar combos con los valores de los enums
        comboCategoria.setItems(FXCollections.observableArrayList(EventoCategoria.values()));
        comboEstado.setItems(FXCollections.observableArrayList(EventoEstado.values()));

        // TODO: Cargar recintos disponibles en comboRecinto
        //       comboRecinto.setItems(FXCollections.observableArrayList(administracionFacade.listarRecintos()));

        // TODO: Configurar cellValueFactory de cada columna de tblEventos

        // TODO: Cargar todos los eventos en la tabla:
        //       tblEventos.setItems(FXCollections.observableArrayList(administracionFacade.listarEventos()));
    }

    /** Inyecta la fachada de administración. */
    public void setAdministracionFacade(AdministracionFacade administracionFacade) {
        this.administracionFacade = administracionFacade;
    }

    // --- HANDLERS: Formulario ---

    @FXML
    public void onGuardarEventoClick(ActionEvent event) {
        if (eventoEnEdicion == null) {
            // TODO: Leer campos y delegar a administracionFacade.crearEvento()
        } else {
            // TODO: Actualizar el eventoEnEdicion con los datos del formulario
            // TODO: Delegar a administracionFacade.actualizarEvento()
        }
        // TODO: Refrescar tblEventos y limpiar formulario
        limpiarFormulario();
    }

    @FXML
    public void onLimpiarFormularioClick(ActionEvent event) {
        limpiarFormulario();
    }

    // --- HANDLERS: Tabla ---

    @FXML
    public void onEditarEventoClick(ActionEvent event) {
        // TODO: Obtener evento seleccionado: tblEventos.getSelectionModel().getSelectedItem()
        // TODO: Precargar campos del formulario con los datos del evento
        // TODO: Cambiar lblModoFormulario a "Editar Evento" y setear eventoEnEdicion
    }

    @FXML
    public void onEliminarEventoClick(ActionEvent event) {
        // TODO: Obtener evento seleccionado
        // TODO: Confirmar con un Alert
        // TODO: Llamar a administracionFacade.eliminarEvento() y refrescar tabla
    }

    // --- MÉTODOS PRIVADOS ---

    private void limpiarFormulario() {
        eventoEnEdicion = null;
        lblModoFormulario.setText("Nuevo Evento");
        txtNombreEvento.clear();
        comboCategoria.setValue(null);
        dtpFechaEvento.setValue(null);
        txtCiudad.clear();
        comboRecinto.setValue(null);
        comboEstado.setValue(null);
        txtDescripcion.clear();
    }
}
