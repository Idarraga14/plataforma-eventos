package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class HistorialComprasController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Usuario actual: SessionManager.getInstance().getUsuarioActual()
    private PlataformaFacade plataformaFacade;

    // --- COMPONENTES FXML: Filtros ---
    @FXML private TextField              txtBuscarEvento;
    @FXML private DatePicker             dtpFechaDesde;
    @FXML private DatePicker             dtpFechaHasta;
    @FXML private ComboBox<CompraEstado> comboEstado;
    @FXML private Button                 btnFiltrar;
    @FXML private Button                 btnLimpiar;

    // --- COMPONENTES FXML: Tabla ---
    @FXML private TableView<Compra>              tblCompras;
    @FXML private TableColumn<Compra, String>    colIdCompra;
    @FXML private TableColumn<Compra, String>    colEvento;
    @FXML private TableColumn<Compra, String>    colFechaCompra;
    @FXML private TableColumn<Compra, Double>    colTotal;
    @FXML private TableColumn<Compra, String>    colEstadoCompra;
    @FXML private TableColumn<Compra, Integer>   colEntradas;

    // --- COMPONENTES FXML: Acciones ---
    @FXML private Button btnExportarPdf;
    @FXML private Button btnExportarCsv;
    @FXML private Button btnCancelarCompra;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Poblar ComboBox de estados
        comboEstado.setItems(FXCollections.observableArrayList(CompraEstado.values()));

        // TODO: Configurar cellValueFactory de cada columna de tblCompras

        // Binding dinámico del botón Cancelar:
        // btnCancelarCompra.disableProperty() debe estar vinculado a la selección
        // de la tabla. Se habilita SÓLO cuando la compra seleccionada tiene
        // estado CREADA o PAGADA. Ejemplo:
        //
        // tblCompras.getSelectionModel().selectedItemProperty().addListener(
        //     (obs, oldVal, newVal) -> {
        //         boolean cancelable = newVal != null &&
        //             (newVal.getEstadoEnum() == CompraEstado.CREADA ||
        //              newVal.getEstadoEnum() == CompraEstado.PAGADA);
        //         btnCancelarCompra.setDisable(!cancelable);
        //     }
        // );

        // TODO: Cargar historial del usuario actual:
        //   plataformaFacade → obtener compras del usuario
        //   tblCompras.setItems(FXCollections.observableArrayList(compras));
    }

    /** Inyecta la fachada de usuario. */
    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    // --- HANDLERS DE EVENTOS ---

    @FXML
    public void onFiltrarClick(ActionEvent event) {
        // TODO: Leer txtBuscarEvento, dtpFechaDesde, dtpFechaHasta, comboEstado
        // TODO: Aplicar filtros sobre la lista de compras y refrescar tblCompras
    }

    @FXML
    public void onLimpiarFiltrosClick(ActionEvent event) {
        // TODO: Limpiar todos los campos de filtro
        // TODO: Recargar la lista completa de compras del usuario actual
    }

    @FXML
    public void onExportarPdfClick(ActionEvent event) {
        // TODO: Obtener las compras actuales de tblCompras
        // TODO: Delegar la exportación (patrón Strategy recomendado)
        //       El archivo se guarda mediante FileChooser
    }

    @FXML
    public void onExportarCsvClick(ActionEvent event) {
        // TODO: Obtener las compras actuales de tblCompras
        // TODO: Delegar la exportación a CSV
        //       El archivo se guarda mediante FileChooser
    }

    @FXML
    public void onCancelarCompraClick(ActionEvent event) {
        // TODO: Obtener compra seleccionada: tblCompras.getSelectionModel().getSelectedItem()
        // TODO: Mostrar diálogo de confirmación antes de proceder
        // TODO: Delegar la cancelación (compra.cancelar() o a través de la fachada)
        // TODO: Refrescar tblCompras y re-evaluar el estado del btnCancelarCompra
    }
}