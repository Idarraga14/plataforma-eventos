package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.IncidenciaEntidadAfectada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Incidencia;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminIncidenciasController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Admin actual: SessionManager.getInstance().getUsuarioActual()
    private AdministracionFacade administracionFacade;

    // --- COMPONENTES FXML: Formulario ---
    @FXML private ComboBox<IncidenciaEntidadAfectada> comboEntidadAfectada;
    @FXML private TextField                           txtIdEntidad;
    @FXML private TextField                           txtReportadoPor;
    @FXML private TextArea                            txtDescripcion;
    @FXML private Label                               lblMensajeIncidencia;
    @FXML private Button                              btnLimpiarIncidencia;
    @FXML private Button                              btnRegistrarIncidencia;

    // --- COMPONENTES FXML: Tabla histórico ---
    @FXML private TableView<Incidencia>                tblIncidencias;
    @FXML private TableColumn<Incidencia, String>      colFecha;
    @FXML private TableColumn<Incidencia, String>      colEntidad;
    @FXML private TableColumn<Incidencia, String>      colIdEntidad;
    @FXML private TableColumn<Incidencia, String>      colReportadoPor;
    @FXML private TableColumn<Incidencia, String>      colDescripcion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Poblar comboEntidadAfectada con IncidenciaEntidadAfectada.values()
        comboEntidadAfectada.setItems(
                FXCollections.observableArrayList(IncidenciaEntidadAfectada.values()));

        // TODO: Pre-rellenar txtReportadoPor con el nombre del admin en sesión:
        //       txtReportadoPor.setText(SessionManager.getInstance().getUsuarioActual().getNombre());

        // TODO: Configurar cellValueFactory de cada columna del histórico

        // TODO: Cargar el histórico de incidencias:
        //       tblIncidencias.setItems(FXCollections.observableArrayList(administracionFacade.listarIncidencias()));
    }

    /** Inyecta la fachada de administración. */
    public void setAdministracionFacade(AdministracionFacade administracionFacade) {
        this.administracionFacade = administracionFacade;
    }

    // --- HANDLERS ---

    @FXML
    public void onRegistrarIncidenciaClick(ActionEvent event) {
        // TODO: Validar que comboEntidadAfectada, txtIdEntidad y txtDescripcion no estén vacíos
        // TODO: Construir el objeto Incidencia con los datos del formulario
        // TODO: Delegar el registro (pendiente de método en AdministracionFacade)
        // TODO: Refrescar tblIncidencias y limpiar formulario

        mostrarMensaje("Incidencia registrada exitosamente.", false);
        limpiarFormulario();
    }

    @FXML
    public void onLimpiarIncidenciaClick(ActionEvent event) {
        limpiarFormulario();
    }

    // --- MÉTODOS PRIVADOS ---

    private void limpiarFormulario() {
        comboEntidadAfectada.setValue(null);
        txtIdEntidad.clear();
        txtDescripcion.clear();
        lblMensajeIncidencia.setVisible(false);
        lblMensajeIncidencia.setManaged(false);
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensajeIncidencia.setText(mensaje);
        lblMensajeIncidencia.setStyle(esError
                ? "-fx-text-fill: #e74c3c;"
                : "-fx-text-fill: #27ae60;");
        lblMensajeIncidencia.setVisible(true);
        lblMensajeIncidencia.setManaged(true);
    }
}