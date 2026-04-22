package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminComprasController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Admin actual: SessionManager.getInstance().getUsuarioActual()
    private AdministracionFacade administracionFacade;

    // --- COMPONENTES FXML: Filtros ---
    @FXML private TextField                  txtFiltroUsuario;
    @FXML private TextField                  txtFiltroEvento;
    @FXML private ComboBox<CompraEstado>     comboFiltroEstado;
    @FXML private DatePicker                 dtpDesde;
    @FXML private Button                     btnFiltrar;
    @FXML private Button                     btnLimpiarFiltros;

    // --- COMPONENTES FXML: Tabla ---
    @FXML private TableView<Compra>           tblCompras;
    @FXML private TableColumn<Compra, String> colIdCompra;
    @FXML private TableColumn<Compra, String> colUsuario;
    @FXML private TableColumn<Compra, String> colEvento;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;

    // --- COMPONENTES FXML: Acciones ---
    @FXML private Label  lblTotalCompras;
    @FXML private Button btnReasignarAsiento;
    @FXML private Button btnRegistrarReembolso;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Poblar comboFiltroEstado con CompraEstado.values()
        comboFiltroEstado.setItems(FXCollections.observableArrayList(CompraEstado.values()));

        // TODO: Configurar cellValueFactory de cada columna

        // TODO: Cargar todas las compras del sistema:
        //       tblCompras.setItems(FXCollections.observableArrayList(administracionFacade.listarCompras()));

        // TODO: Actualizar lblTotalCompras con el tamaño de la lista
    }

    /** Inyecta la fachada de administración. */
    public void setAdministracionFacade(AdministracionFacade administracionFacade) {
        this.administracionFacade = administracionFacade;
    }

    // --- HANDLERS ---

    @FXML
    public void onFiltrarClick(ActionEvent event) {
        // TODO: Leer txtFiltroUsuario, txtFiltroEvento, comboFiltroEstado y dtpDesde
        // TODO: Filtrar y actualizar tblCompras con los resultados
        // TODO: Actualizar lblTotalCompras
    }

    @FXML
    public void onLimpiarFiltrosClick(ActionEvent event) {
        // TODO: Limpiar todos los campos de filtro
        // TODO: Recargar la lista completa de compras
    }

    @FXML
    public void onReasignarAsientoClick(ActionEvent event) {
        // TODO: Obtener compra seleccionada: tblCompras.getSelectionModel().getSelectedItem()
        // TODO: Abrir diálogo para seleccionar el nuevo asiento disponible
        // TODO: Confirmar y refrescar la tabla
    }

    @FXML
    public void onRegistrarReembolsoClick(ActionEvent event) {
        // TODO: Obtener compra seleccionada
        // TODO: Validar que la compra sea elegible para reembolso (estado PAGADA o CONFIRMADA)
        // TODO: Confirmar con un Alert y llamar a administracionFacade.reembolsarCompra()
        // TODO: Refrescar la fila en la tabla
    }
}