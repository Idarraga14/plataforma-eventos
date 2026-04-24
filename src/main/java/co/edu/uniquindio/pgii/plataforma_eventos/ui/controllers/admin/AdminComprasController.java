package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador JavaFX del módulo de gestión de compras para el administrador.
 *
 * <p>Lista todas las compras del sistema con filtros combinables (usuario, evento, estado,
 * fecha desde). Expone dos operaciones administrativas sobre la compra seleccionada:</p>
 * <ul>
 *   <li><strong>Reasignar asiento</strong>: flujo de dos {@code ChoiceDialog} — primero el
 *       asiento actual (extraído recorriendo la cadena de decoradores hasta {@code EntradaAsiento})
 *       y luego el asiento destino libre en el inventario del evento. Delega a
 *       {@link AdministracionFacade#reasignarAsiento}.</li>
 *   <li><strong>Registrar reembolso</strong>: confirma y delega a
 *       {@link AdministracionFacade#reembolsarCompra} (solo PAGADAS o CONFIRMADAS).</li>
 * </ul>
 *
 * <p>[Requerimiento: RF-011] - Permite al administrador registrar reembolsos de compras.</p>
 * <p>[Requerimiento: RF-018] - Implementa la reasignación de asiento numerado dentro de la
 * misma compra, actualizando el inventario comercial ({@code AsientoEvento}) del evento.</p>
 * <p>[Patrón: Decorator] - El helper privado {@code unwrapAsientoId} recorre la cadena de
 * decoradores hasta encontrar el {@code EntradaAsiento} concreto para obtener el ID.</p>
 * <p>[Patrón: Facade] - Las operaciones de reasignación y reembolso se delegan a
 * {@link AdministracionFacade}, que aplica las reglas de negocio y las transiciones de State.</p>
 */
public class AdminComprasController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML
    private TextField txtFiltroUsuario;
    @FXML
    private TextField txtFiltroEvento;
    @FXML
    private ComboBox<CompraEstado> comboFiltroEstado;
    @FXML
    private DatePicker dtpDesde;

    @FXML
    private TableView<Compra> tblCompras;
    @FXML
    private TableColumn<Compra, String> colIdCompra;
    @FXML
    private TableColumn<Compra, String> colUsuario;
    @FXML
    private TableColumn<Compra, String> colEvento;
    @FXML
    private TableColumn<Compra, String> colFecha;
    @FXML
    private TableColumn<Compra, String> colTotal;
    @FXML
    private TableColumn<Compra, String> colEstado;

    @FXML
    private Label lblTotalCompras;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboFiltroEstado.setItems(FXCollections.observableArrayList(CompraEstado.values()));
        colIdCompra.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIdCompra()));
        colUsuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsuario().getNombreCompleto()));
        colEvento.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEvento().getNombre()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFecha().format(FECHA_FMT)));
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(String.format("$ %,.0f", d.getValue().getTotal())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstadoEnum().name()));
        cargarCompras();
    }

    @FXML
    public void onFiltrarClick() {
        String qU = safe(txtFiltroUsuario.getText()).toLowerCase();
        String qE = safe(txtFiltroEvento.getText()).toLowerCase();
        CompraEstado est = comboFiltroEstado.getValue();
        LocalDate desde = dtpDesde.getValue();
        List<Compra> filtradas = administracionFacade.listarCompras().stream()
                .filter(c -> qU.isEmpty()
                        || c.getUsuario().getNombreCompleto().toLowerCase().contains(qU)
                        || c.getUsuario().getCorreo().toLowerCase().contains(qU))
                .filter(c -> qE.isEmpty() || c.getEvento().getNombre().toLowerCase().contains(qE))
                .filter(c -> est == null || c.getEstadoEnum() == est)
                .filter(c -> desde == null || !c.getFecha().toLocalDate().isBefore(desde))
                .collect(Collectors.toList());
        tblCompras.setItems(FXCollections.observableArrayList(filtradas));
        lblTotalCompras.setText("Total: " + filtradas.size() + " compras");
    }

    @FXML
    public void onLimpiarFiltrosClick() {
        txtFiltroUsuario.clear();
        txtFiltroEvento.clear();
        comboFiltroEstado.setValue(null);
        dtpDesde.setValue(null);
        cargarCompras();
    }

    @FXML
    public void onReasignarAsientoClick() {
        Compra sel = tblCompras.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona una compra.");
            return;
        }
        if (sel.getEstadoEnum() != CompraEstado.PAGADA && sel.getEstadoEnum() != CompraEstado.CONFIRMADA) {
            mostrarError("Solo se pueden reasignar asientos en compras PAGADAS o CONFIRMADAS.");
            return;
        }

        // Recolectar asientos actuales de la compra (entradas con asiento numerado)
        List<String> asientosActuales = sel.getEntradas().stream()
                .map(AdminComprasController::unwrapAsientoId)
                .filter(Objects::nonNull)
                .toList();

        if (asientosActuales.isEmpty()) {
            mostrarInfo("Esta compra no tiene entradas con asiento numerado.");
            return;
        }

        // Paso 1: elegir el asiento a reasignar
        ChoiceDialog<String> dlgAntiguo = new ChoiceDialog<>(asientosActuales.getFirst(), asientosActuales);
        dlgAntiguo.setTitle("Reasignar Asiento");
        dlgAntiguo.setHeaderText("Selecciona el asiento actual a cambiar:");
        dlgAntiguo.setContentText("Asiento:");
        Optional<String> idAntiguo = dlgAntiguo.showAndWait();
        if (idAntiguo.isEmpty()) return;

        // Paso 2: elegir el nuevo asiento (disponibles en la misma zona del evento)
        List<String> disponibles = sel.getEvento().getRecinto().getZonas().stream()
                .flatMap(z -> sel.getEvento().getInventarioDe(z).stream())
                .filter(ae -> ae.getEstado() == AsientoEstado.DISPONIBLE)
                .map(ae -> ae.getAsientoFisico().getSalida() + " [" + ae.getIdAsiento() + "]")
                .toList();

        if (disponibles.isEmpty()) {
            mostrarInfo("No hay asientos disponibles en este evento.");
            return;
        }

        ChoiceDialog<String> dlgNuevo = new ChoiceDialog<>(disponibles.getFirst(), disponibles);
        dlgNuevo.setTitle("Reasignar Asiento");
        dlgNuevo.setHeaderText("Selecciona el nuevo asiento:");
        dlgNuevo.setContentText("Asiento destino:");
        Optional<String> selNuevo = dlgNuevo.showAndWait();
        if (selNuevo.isEmpty()) return;

        // Extraer el idAsiento del formato "Etiqueta [uuid]"
        String idNuevo = selNuevo.get().replaceAll(".*\\[(.+)]", "$1");

        try {
            administracionFacade.reasignarAsiento(sel.getIdCompra(), idAntiguo.get(), idNuevo);
            mostrarInfo("Asiento reasignado correctamente.");
            cargarCompras();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private static String unwrapAsientoId(co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada e) {
        co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada actual = e;
        while (actual instanceof co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.EntradaDecorator d) {
            actual = d.getEntradaEnvuelta();
        }
        if (actual instanceof EntradaAsiento ea) return ea.getAsientoEvento().getIdAsiento();
        return null;
    }

    @FXML
    public void onRegistrarReembolsoClick() {
        Compra sel = tblCompras.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona una compra.");
            return;
        }
        if (sel.getEstadoEnum() != CompraEstado.PAGADA && sel.getEstadoEnum() != CompraEstado.CONFIRMADA) {
            mostrarError("Sólo se pueden reembolsar compras PAGADAS o CONFIRMADAS.");
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Reembolsar la compra " + sel.getIdCompra() + "?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.YES) return;
        try {
            administracionFacade.reembolsarCompra(sel.getIdCompra());
            cargarCompras();
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
        navegar("AdminEventosView.fxml");
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
        Stage stage = (Stage) tblCompras.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }

    private void navegar(String fxml) {
        Stage stage = (Stage) tblCompras.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }

    private void cargarCompras() {
        List<Compra> lista = administracionFacade.listarCompras();
        tblCompras.setItems(FXCollections.observableArrayList(lista));
        lblTotalCompras.setText("Total: " + lista.size() + " compras");
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private void mostrarError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    private void mostrarInfo(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
