package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class HistorialComprasController implements Initializable {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private PlataformaFacade plataformaFacade = new PlataformaFacadeImpl();

    @FXML private TextField              txtBuscarEvento;
    @FXML private DatePicker             dtpFechaDesde;
    @FXML private DatePicker             dtpFechaHasta;
    @FXML private ComboBox<CompraEstado> comboEstado;
    @FXML private Button                 btnFiltrar;
    @FXML private Button                 btnLimpiar;

    @FXML private TableView<Compra>              tblCompras;
    @FXML private TableColumn<Compra, String>    colIdCompra;
    @FXML private TableColumn<Compra, String>    colEvento;
    @FXML private TableColumn<Compra, String>    colFechaCompra;
    @FXML private TableColumn<Compra, Double>    colTotal;
    @FXML private TableColumn<Compra, String>    colEstadoCompra;
    @FXML private TableColumn<Compra, Integer>   colEntradas;

    @FXML private Button btnExportarPdf;
    @FXML private Button btnExportarCsv;
    @FXML private Button btnCancelarCompra;

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

    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    @FXML
    public void onFiltrarClick(ActionEvent event) {
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
    public void onLimpiarFiltrosClick(ActionEvent event) {
        txtBuscarEvento.clear();
        dtpFechaDesde.setValue(null);
        dtpFechaHasta.setValue(null);
        comboEstado.setValue(null);
        cargarHistorial();
    }

    @FXML
    public void onExportarPdfClick(ActionEvent event) {
        mostrarInfo("Exportación a PDF aún no implementada.");
    }

    @FXML
    public void onExportarCsvClick(ActionEvent event) {
        mostrarInfo("Exportación a CSV aún no implementada.");
    }

    @FXML
    public void onCancelarCompraClick(ActionEvent event) {
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

    // --- Helpers ---

    private void configurarColumnas() {
        colIdCompra.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
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
