package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX del módulo de gestión de eventos para el administrador.
 *
 * <p>Presenta un formulario dual (alta/edición) y un {@link TableView} con todos los eventos
 * del sistema. Permite:</p>
 * <ul>
 *   <li><strong>Crear</strong>: construye un nuevo {@link Evento} via
 *       {@link AdministracionFacade#crearEvento} (que invoca internamente el {@code EventoBuilder}).</li>
 *   <li><strong>Editar</strong>: en modo edición sólo permite cambiar el estado del evento
 *       (los campos estructurales se deshabilitan) vía {@code actualizarEstadoEvento}.</li>
 *   <li><strong>Eliminar</strong>: solicita confirmación y delega a {@code eliminarEvento}.</li>
 * </ul>
 *
 * <p>[Requerimiento: RF-012] - Implementa la creación de eventos con todos sus atributos.</p>
 * <p>[Requerimiento: RF-014] - Permite cambiar el estado del evento (BORRADOR, PUBLICADO,
 * PAUSADO, CANCELADO, FINALIZADO) desde el formulario de edición.</p>
 * <p>[Patrón: Builder] - La creación de eventos se delega a {@link AdministracionFacade#crearEvento},
 * que internamente usa {@code Evento.EventoBuilder} para validar campos obligatorios.</p>
 * <p>[Patrón: Facade] - Todas las operaciones CRUD se realizan a través de
 * {@link AdministracionFacade}, sin acceso directo al repositorio.</p>
 */
public class AdminEventosController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML
    private Label lblModoFormulario;
    @FXML
    private TextField txtNombreEvento;
    @FXML
    private ComboBox<EventoCategoria> comboCategoria;
    @FXML
    private DatePicker dtpFechaEvento;
    @FXML
    private TextField txtCiudad;
    @FXML
    private ComboBox<Recinto> comboRecinto;
    @FXML
    private ComboBox<EventoEstado> comboEstado;
    @FXML
    private TextArea txtDescripcion;

    @FXML
    private TableView<Evento> tblEventos;
    @FXML
    private TableColumn<Evento, String> colNombre;
    @FXML
    private TableColumn<Evento, String> colCategoria;
    @FXML
    private TableColumn<Evento, String> colFecha;
    @FXML
    private TableColumn<Evento, String> colCiudad;
    @FXML
    private TableColumn<Evento, String> colEstado;

    private Evento eventoEnEdicion = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboCategoria.setItems(FXCollections.observableArrayList(EventoCategoria.values()));
        comboEstado.setItems(FXCollections.observableArrayList(EventoEstado.values()));
        comboRecinto.setItems(FXCollections.observableArrayList(administracionFacade.listarRecintos()));
        comboRecinto.setConverter(new StringConverter<>() {
            @Override
            public String toString(Recinto r) {
                return r == null ? "" : r.getNombre();
            }

            @Override
            public Recinto fromString(String s) {
                return null;
            }
        });

        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colCategoria.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategoria().name()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFecha().format(FECHA_FMT)));
        colCiudad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCiudad()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado().name()));

        cargarEventos();
    }

    @FXML
    public void onGuardarEventoClick() {
        try {
            String nombre = txtNombreEvento.getText();
            EventoCategoria cat = comboCategoria.getValue();
            LocalDate fecha = dtpFechaEvento.getValue();
            String ciudad = txtCiudad.getText();
            Recinto recinto = comboRecinto.getValue();
            EventoEstado estado = comboEstado.getValue();
            String desc = txtDescripcion.getText();

            if (nombre == null || nombre.isBlank() || cat == null || fecha == null
                    || ciudad == null || ciudad.isBlank() || recinto == null) {
                mostrarError("Completa los campos obligatorios (nombre, categoría, fecha, ciudad, recinto).");
                return;
            }

            if (eventoEnEdicion == null) {
                Evento nuevo = administracionFacade.crearEvento(nombre, cat, desc, ciudad,
                        fecha.atTime(20, 0), recinto.getIdRecinto());
                if (estado != null) administracionFacade.actualizarEstadoEvento(nuevo.getIdEvento(), estado);
            } else {
                if (estado != null && estado != eventoEnEdicion.getEstado()) {
                    administracionFacade.actualizarEstadoEvento(eventoEnEdicion.getIdEvento(), estado);
                }
            }
            cargarEventos();
            limpiarFormulario();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    public void onLimpiarFormularioClick() {
        limpiarFormulario();
    }

    @FXML
    public void onEditarEventoClick() {
        Evento sel = tblEventos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un evento para editar.");
            return;
        }
        eventoEnEdicion = sel;
        lblModoFormulario.setText("Editar Evento (sólo estado editable)");
        txtNombreEvento.setText(sel.getNombre());
        txtNombreEvento.setDisable(true);
        comboCategoria.setValue(sel.getCategoria());
        comboCategoria.setDisable(true);
        dtpFechaEvento.setValue(sel.getFecha().toLocalDate());
        dtpFechaEvento.setDisable(true);
        txtCiudad.setText(sel.getCiudad());
        txtCiudad.setDisable(true);
        comboRecinto.setValue(sel.getRecinto());
        comboRecinto.setDisable(true);
        comboEstado.setValue(sel.getEstado());
        txtDescripcion.setText(sel.getDescripcion());
        txtDescripcion.setDisable(true);
    }

    @FXML
    public void onEliminarEventoClick() {
        Evento sel = tblEventos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un evento.");
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el evento '" + sel.getNombre() + "'?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.YES) return;
        try {
            administracionFacade.eliminarEvento(sel.getIdEvento());
            cargarEventos();
            limpiarFormulario();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // --- Navegación ---
    @FXML
    public void onNavDashboard() {
        navegar("AdminDashboardView.fxml");
    }

    @FXML
    public void onNavEventos() {
    }

    @FXML
    public void onNavRecintos() {
        navegar("AdminRecintosView.fxml");
    }

    @FXML
    public void onNavUsuarios() {
        navegar("AdminUsuariosView.fxml");
    }

    @FXML
    public void onNavCompras() {
        navegar("AdminComprasView.fxml");
    }

    @FXML
    public void onNavAsientos() {
        navegar("AdminGestorAsientosView.fxml");
    }

    @FXML
    public void onNavReportes() {
        navegar("AdminReportesView.fxml");
    }

    @FXML
    public void onNavIncidencias() {
        navegar("AdminIncidenciasView.fxml");
    }

    @FXML
    public void onCerrarSesion() {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) tblEventos.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }

    private void navegar(String fxml) {
        Stage stage = (Stage) tblEventos.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }

    private void cargarEventos() {
        tblEventos.setItems(FXCollections.observableArrayList(administracionFacade.listarEventos()));
        tblEventos.refresh();
    }

    private void limpiarFormulario() {
        eventoEnEdicion = null;
        lblModoFormulario.setText("Nuevo Evento");
        txtNombreEvento.clear();
        txtNombreEvento.setDisable(false);
        comboCategoria.setValue(null);
        comboCategoria.setDisable(false);
        dtpFechaEvento.setValue(null);
        dtpFechaEvento.setDisable(false);
        txtCiudad.clear();
        txtCiudad.setDisable(false);
        comboRecinto.setValue(null);
        comboRecinto.setDisable(false);
        comboEstado.setValue(null);
        txtDescripcion.clear();
        txtDescripcion.setDisable(false);
    }

    private void mostrarError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
