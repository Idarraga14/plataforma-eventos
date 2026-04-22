package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
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

import java.net.URL;
import java.util.ResourceBundle;

public class ExplorarEventosController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Usuario actual: SessionManager.getInstance().getUsuarioActual()
    private PlataformaFacade plataformaFacade;

    // --- COMPONENTES FXML ---
    @FXML private DatePicker               dtpFecha;
    @FXML private ComboBox<String>         comboCiudad;
    @FXML private ComboBox<EventoCategoria> comboCategoria;
    @FXML private Button                   btnBuscar;
    @FXML private Button                   btnLimpiarFiltros;
    @FXML private TableView<Evento>        tblEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colFecha;
    @FXML private TableColumn<Evento, String> colCiudad;
    @FXML private TableColumn<Evento, String> colCategoria;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private Button                   btnVerDetalle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Poblar comboCategoria con los valores de EventoCategoria.values()
        comboCategoria.setItems(FXCollections.observableArrayList(EventoCategoria.values()));

        // TODO: Configurar cellValueFactory de cada columna de tblEventos
        // Ej: colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));

        // TODO: Cargar todos los eventos al inicializar:
        //       tblEventos.setItems(FXCollections.observableArrayList(plataformaFacade.obtenerEventosDisponibles()));
    }

    /** Inyecta la fachada de usuario. */
    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    // --- HANDLERS DE EVENTOS ---

    @FXML
    public void onBuscarClick(ActionEvent event) {
        // TODO: Leer dtpFecha.getValue(), comboCiudad.getValue(), comboCategoria.getValue()
        // TODO: Delegar filtrado a plataformaFacade y actualizar tblEventos
    }

    @FXML
    public void onLimpiarFiltrosClick(ActionEvent event) {
        // TODO: Limpiar dtpFecha, comboCiudad y comboCategoria
        // TODO: Recargar la lista completa de eventos
    }

    @FXML
    public void onVerDetalleClick(ActionEvent event) {
        // TODO: Obtener el Evento seleccionado: tblEventos.getSelectionModel().getSelectedItem()
        // TODO: Navegar a DetalleEventoView pasando el evento seleccionado
    }
}