package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.IncidenciaEntidadAfectada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Incidencia;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminIncidenciasController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML private ComboBox<IncidenciaEntidadAfectada> comboEntidadAfectada;
    @FXML private TextField                           txtIdEntidad;
    @FXML private TextField                           txtReportadoPor;
    @FXML private TextArea                            txtDescripcion;
    @FXML private Label                               lblMensajeIncidencia;
    @FXML private Button                              btnLimpiarIncidencia;
    @FXML private Button                              btnRegistrarIncidencia;

    @FXML private TableView<Incidencia>                tblIncidencias;
    @FXML private TableColumn<Incidencia, String>      colFecha;
    @FXML private TableColumn<Incidencia, String>      colEntidad;
    @FXML private TableColumn<Incidencia, String>      colIdEntidad;
    @FXML private TableColumn<Incidencia, String>      colReportadoPor;
    @FXML private TableColumn<Incidencia, String>      colDescripcion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboEntidadAfectada.setItems(FXCollections.observableArrayList(IncidenciaEntidadAfectada.values()));

        if (SessionManager.getInstance().getUsuarioActual() != null) {
            txtReportadoPor.setText(SessionManager.getInstance().getUsuarioActual().getNombreCompleto());
        }

        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFecha().format(FECHA_FMT)));
        colEntidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEntidadAfectada().name()));
        colIdEntidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIdEntidadAfectada()));
        colReportadoPor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReportadoPor()));
        colDescripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescripcion()));

        cargarHistorial();
    }

    public void setAdministracionFacade(AdministracionFacade f) { this.administracionFacade = f; }

    @FXML
    public void onRegistrarIncidenciaClick(ActionEvent event) {
        IncidenciaEntidadAfectada ent = comboEntidadAfectada.getValue();
        String idEnt = safe(txtIdEntidad.getText());
        String rep = safe(txtReportadoPor.getText());
        String desc = safe(txtDescripcion.getText());
        if (ent == null || idEnt.isEmpty() || rep.isEmpty() || desc.isEmpty()) {
            mostrarMensaje("Completa todos los campos.", true);
            return;
        }
        try {
            administracionFacade.registrarIncidencia("GENERAL", desc, ent, idEnt, rep);
            mostrarMensaje("Incidencia registrada exitosamente.", false);
            cargarHistorial();
            limpiarFormulario();
        } catch (RuntimeException ex) {
            mostrarMensaje("Error: " + ex.getMessage(), true);
        }
    }

    @FXML
    public void onLimpiarIncidenciaClick(ActionEvent event) { limpiarFormulario(); }

    // --- Navegación ---
    @FXML public void onNavDashboard(ActionEvent e) { navegar("AdminDashboardView.fxml"); }
    @FXML public void onNavEventos(ActionEvent e) { navegar("AdminEventosView.fxml"); }
    @FXML public void onNavRecintos(ActionEvent e) { navegar("AdminRecintosView.fxml"); }
    @FXML public void onNavUsuarios(ActionEvent e) { navegar("AdminUsuariosView.fxml"); }
    @FXML public void onNavCompras(ActionEvent e) { navegar("AdminComprasView.fxml"); }
    @FXML public void onNavAsientos(ActionEvent e) { navegar("AdminGestorAsientosView.fxml"); }
    @FXML public void onNavReportes(ActionEvent e) { navegar("AdminReportesView.fxml"); }
    @FXML public void onNavIncidencias(ActionEvent e) { }
    @FXML public void onCerrarSesion(ActionEvent e) {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) tblIncidencias.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }
    private void navegar(String fxml) {
        Stage stage = (Stage) tblIncidencias.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }

    private void cargarHistorial() {
        tblIncidencias.setItems(FXCollections.observableArrayList(administracionFacade.listarIncidencias()));
    }

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

    private String safe(String s) { return s == null ? "" : s.trim(); }
}
