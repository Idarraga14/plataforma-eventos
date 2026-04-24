package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.FormatoReporte;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX de la pantalla de historial de compras del usuario.
 *
 * <p>Lista todas las compras del usuario en sesión con filtros combinables (nombre de evento,
 * rango de fechas, estado). Permite tres acciones sobre la compra seleccionada:</p>
 * <ul>
 *   <li><strong>Exportar PDF/CSV</strong>: genera el comprobante vía
 *       {@link PlataformaFacade#generarComprobante} y abre un {@code FileChooser} para guardarlo
 *       (solo compras PAGADAS o CONFIRMADAS).</li>
 *   <li><strong>Cancelar compra</strong>: invoca {@link PlataformaFacade#cancelarOrdenCompra}
 *       (solo compras CREADAS o PAGADAS).</li>
 * </ul>
 *
 * <p>[Requerimiento: RF-009] - Implementa la exportación del comprobante de compra en PDF/CSV
 *   usando el Adaptador concreto ({@code ExportadorPDFAdapter}/{@code ExportadorCSVAdapter}).</p>
 * <p>[Requerimiento: RF-011] - Permite al usuario cancelar compras elegibles desde la UI.</p>
 * <p>[Patrón: Adapter] - Desencadena el flujo de exportación mediante la fachada, que
 *   selecciona el {@code ExportadorReporte} concreto según el {@code FormatoReporte}.</p>
 * <p>[Patrón: Facade] - Todas las operaciones (listar, cancelar, exportar) se realizan
 *   a través de {@link PlataformaFacade} sin acceder al repositorio.</p>
 */
public class HistorialComprasController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final PlataformaFacade plataformaFacade = new PlataformaFacadeImpl();

    @FXML
    private TextField txtBuscarEvento;
    @FXML
    private DatePicker dtpFechaDesde;
    @FXML
    private DatePicker dtpFechaHasta;
    @FXML
    private ComboBox<CompraEstado> comboEstado;

    @FXML
    private TableView<Compra> tblCompras;
    @FXML
    private TableColumn<Compra, String> colIdCompra;
    @FXML
    private TableColumn<Compra, String> colEvento;
    @FXML
    private TableColumn<Compra, String> colFechaCompra;
    @FXML
    private TableColumn<Compra, Double> colTotal;
    @FXML
    private TableColumn<Compra, String> colEstadoCompra;
    @FXML
    private TableColumn<Compra, Integer> colEntradas;
    @FXML
    private Button btnCancelarCompra;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboEstado.setItems(FXCollections.observableArrayList(CompraEstado.values()));

        configurarColumnas();

        tblCompras.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean cancelable = newVal != null
                    && (newVal.getEstadoEnum() == CompraEstado.CREADA
                    || newVal.getEstadoEnum() == CompraEstado.PAGADA);
            btnCancelarCompra.setDisable(!cancelable);
        });

        cargarHistorial();
    }

    @FXML
    public void onFiltrarClick() {
        String texto = txtBuscarEvento.getText() == null ? "" : txtBuscarEvento.getText().trim().toLowerCase();
        LocalDate desde = dtpFechaDesde.getValue();
        LocalDate hasta = dtpFechaHasta.getValue();
        CompraEstado estado = comboEstado.getValue();

        List<Compra> base = obtenerComprasUsuario();
        ObservableList<Compra> filtradas = FXCollections.observableArrayList();
        for (Compra c : base) {
            if (!texto.isEmpty() && !c.getEvento().getNombre().toLowerCase().contains(texto)) continue;
            LocalDate fecha = c.getFecha().toLocalDate();
            if (desde != null && fecha.isBefore(desde)) continue;
            if (hasta != null && fecha.isAfter(hasta)) continue;
            if (estado != null && c.getEstadoEnum() != estado) continue;
            filtradas.add(c);
        }
        tblCompras.setItems(filtradas);
    }

    @FXML
    public void onLimpiarFiltrosClick() {
        txtBuscarEvento.clear();
        dtpFechaDesde.setValue(null);
        dtpFechaHasta.setValue(null);
        comboEstado.setValue(null);
        cargarHistorial();
    }

    @FXML
    public void onExportarPdfClick() {
        exportarSeleccionada(FormatoReporte.PDF, "pdf", "Documento PDF (*.pdf)", "*.pdf");
    }

    @FXML
    public void onExportarCsvClick() {
        exportarSeleccionada(FormatoReporte.CSV, "csv", "Archivo CSV (*.csv)", "*.csv");
    }

    @FXML
    public void onCancelarCompraClick() {
        Compra seleccionada = tblCompras.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas cancelar la compra " + seleccionada.getIdCompra() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.YES) return;

        try {
            plataformaFacade.cancelarOrdenCompra(seleccionada.getIdCompra());
            cargarHistorial();
        } catch (RuntimeException ex) {
            mostrarError("No se pudo cancelar la compra: " + ex.getMessage());
        }
    }

    // --- NAVEGACIÓN ---

    @FXML
    public void onNavEventos() {
        Stage stage = (Stage) tblCompras.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("ExplorarEventosView.fxml", stage);
    }

    @FXML
    public void onNavHistorial() {
        // ya estamos aquí
    }

    @FXML
    public void onNavPerfil() {
        Stage stage = (Stage) tblCompras.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("PerfilUsuarioView.fxml", stage);
    }

    @FXML
    public void onCerrarSesion() {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) tblCompras.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }

    // --- Helpers ---

    private void exportarSeleccionada(FormatoReporte formato, String ext, String desc, String pattern) {
        Compra seleccionada = tblCompras.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarInfo("Selecciona una compra para exportar.");
            return;
        }
        CompraEstado estado = seleccionada.getEstadoEnum();
        if (estado != CompraEstado.PAGADA && estado != CompraEstado.CONFIRMADA) {
            mostrarInfo("Sólo se pueden exportar compras en estado PAGADA o CONFIRMADA.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar comprobante");
        chooser.setInitialFileName("compra_" + seleccionada.getIdCompra() + "." + ext);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, pattern));
        File destino = chooser.showSaveDialog(tblCompras.getScene().getWindow());
        if (destino == null) return;

        try {
            byte[] datos = plataformaFacade.generarComprobante(seleccionada.getIdCompra(), formato);
            Files.write(destino.toPath(), datos);
            mostrarInfo("Comprobante guardado en: " + destino.getAbsolutePath());
        } catch (IOException | RuntimeException ex) {
            mostrarError("No se pudo exportar: " + ex.getMessage());
        }
    }

    private void configurarColumnas() {
        colIdCompra.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getIdCompra()));
        colEvento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEvento().getNombre()));
        colFechaCompra.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha().format(FECHA_FMT)));
        colTotal.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getTotal()));
        colEstadoCompra.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstadoEnum().name()));
        colEntradas.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getEntradas().size()).asObject());
    }

    private void cargarHistorial() {
        tblCompras.setItems(FXCollections.observableArrayList(obtenerComprasUsuario()));
    }

    private List<Compra> obtenerComprasUsuario() {
        return plataformaFacade.obtenerComprasPorUsuario(
                SessionManager.getInstance().getUsuarioActual().getIdUsuario());
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
