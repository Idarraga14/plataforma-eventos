package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminComprasController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML private TextField                  txtFiltroUsuario;
    @FXML private TextField                  txtFiltroEvento;
    @FXML private ComboBox<CompraEstado>     comboFiltroEstado;
    @FXML private DatePicker                 dtpDesde;
    @FXML private Button                     btnFiltrar;
    @FXML private Button                     btnLimpiarFiltros;

    @FXML private TableView<Compra>           tblCompras;
    @FXML private TableColumn<Compra, String> colIdCompra;
    @FXML private TableColumn<Compra, String> colUsuario;
    @FXML private TableColumn<Compra, String> colEvento;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;

    @FXML private Label  lblTotalCompras;
    @FXML private Button btnReasignarAsiento;
    @FXML private Button btnRegistrarReembolso;

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

    public void setAdministracionFacade(AdministracionFacade f) { this.administracionFacade = f; }

    @FXML
    public void onFiltrarClick(ActionEvent event) {
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
    public void onLimpiarFiltrosClick(ActionEvent event) {
        txtFiltroUsuario.clear(); txtFiltroEvento.clear();
        comboFiltroEstado.setValue(null); dtpDesde.setValue(null);
        cargarCompras();
    }

    @FXML
    public void onReasignarAsientoClick(ActionEvent event) {
        Compra sel = tblCompras.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona una compra."); return; }
        if (sel.getEstadoEnum() != CompraEstado.PAGADA && sel.getEstadoEnum() != CompraEstado.CONFIRMADA) {
            mostrarError("Solo se pueden reasignar asientos en compras PAGADAS o CONFIRMADAS.");
            return;
        }

        // Recolectar asientos actuales de la compra (entradas con asiento numerado)
        List<String> asientosActuales = sel.getEntradas().stream()
                .map(AdminComprasController::unwrapAsientoId)
                .filter(id -> id != null)
                .toList();

        if (asientosActuales.isEmpty()) {
            mostrarInfo("Esta compra no tiene entradas con asiento numerado.");
            return;
        }

        // Paso 1: elegir el asiento a reasignar
        ChoiceDialog<String> dlgAntiguo = new ChoiceDialog<>(asientosActuales.get(0), asientosActuales);
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

        ChoiceDialog<String> dlgNuevo = new ChoiceDialog<>(disponibles.get(0), disponibles);
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
    public void onRegistrarReembolsoClick(ActionEvent event) {
        Compra sel = tblCompras.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona una compra."); return; }
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
    @FXML public void onNavDashboard(ActionEvent e) { navegar("AdminDashboardView.fxml"); }
    @FXML public void onNavEventos(ActionEvent e) { navegar("AdminEventosView.fxml"); }
    @FXML public void onNavRecintos(ActionEvent e) { navegar("AdminRecintosView.fxml"); }
    @FXML public void onNavUsuarios(ActionEvent e) { navegar("AdminUsuariosView.fxml"); }
    @FXML public void onNavCompras(ActionEvent e) { }
    @FXML public void onNavAsientos(ActionEvent e) { navegar("AdminGestorAsientosView.fxml"); }
    @FXML public void onNavReportes(ActionEvent e) { navegar("AdminReportesView.fxml"); }
    @FXML public void onNavIncidencias(ActionEvent e) { navegar("AdminIncidenciasView.fxml"); }
    @FXML public void onCerrarSesion(ActionEvent e) {
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

    private String safe(String s) { return s == null ? "" : s.trim(); }

    private void mostrarError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
    private void mostrarInfo(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}
