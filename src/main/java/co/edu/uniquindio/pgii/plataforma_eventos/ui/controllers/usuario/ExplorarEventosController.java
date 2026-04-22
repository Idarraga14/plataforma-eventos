package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExplorarEventosController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    private final PlataformaFacade facade = new PlataformaFacadeImpl();

    private ObservableList<Evento> listaMaestraEventos;

    // --- COMPONENTES FXML ---
    @FXML private ComboBox<String>         comboCiudad;
    @FXML private ComboBox<EventoCategoria> comboCategoria;
    @FXML private DatePicker               dtpFecha;
    @FXML private Button                   btnVerDetalle;

    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colCategoria;
    @FXML private TableColumn<Evento, String> colCiudad;
    @FXML private TableColumn<Evento, String> colFecha;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarDatosYFiltros();

        btnVerDetalle.setDisable(true);
        tablaEventos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> btnVerDetalle.setDisable(newValue == null)
        );
    }

    private void configurarColumnas() {
        // Usamos SimpleStringProperty para evitar el uso de Reflection (PropertyValueFactory) que es más propenso a errores
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().name()));
        colCiudad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCiudad()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colFecha.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFecha().format(formatter))
        );
    }

    private void cargarDatosYFiltros() {
        // Obtenemos los eventos desde la capa de aplicación
        List<Evento> eventos = facade.obtenerEventosDisponibles();
        listaMaestraEventos = FXCollections.observableArrayList(eventos);
        tablaEventos.setItems(listaMaestraEventos);

        // Poblar ComboBox de Categorías usando el Enum
        comboCategoria.getItems().addAll(EventoCategoria.values());

        // Poblar ComboBox de Ciudades extrayendo las ciudades únicas de los eventos
        List<String> ciudadesUnicas = eventos.stream()
                .map(Evento::getCiudad)
                .distinct()
                .toList();
        comboCiudad.getItems().addAll(ciudadesUnicas);
    }

    // --- HANDLERS DE EVENTOS ---
    @FXML
    public void onBuscarClick() {
        String ciudadBuscada = comboCiudad.getValue();
        EventoCategoria categoriaBuscada = comboCategoria.getValue();
        LocalDate fechaBuscada = dtpFecha.getValue();

        // Usamos Streams para filtrar la lista maestra de forma elegante
        List<Evento> eventosFiltrados = listaMaestraEventos.stream()
                .filter(e -> ciudadBuscada == null || e.getCiudad().equals(ciudadBuscada))
                .filter(e -> categoriaBuscada == null || e.getCategoria() == categoriaBuscada)
                .filter(e -> fechaBuscada == null || e.getFecha().toLocalDate().equals(fechaBuscada))
                .collect(Collectors.toList());

        // Actualizamos la tabla con el resultado
        tablaEventos.setItems(FXCollections.observableArrayList(eventosFiltrados));
    }

    @FXML
    public void onLimpiarFiltrosClick() {
        comboCiudad.setValue(null);
        comboCategoria.setValue(null);
        dtpFecha.setValue(null);
        tablaEventos.setItems(listaMaestraEventos);
    }

    @FXML
    public void onVerDetalleClick() {
        Evento eventoSeleccionado = tablaEventos.getSelectionModel().getSelectedItem();

        if (eventoSeleccionado != null) {
            // Guardamos el evento en nuestro SessionManager para la siguiente pantalla
            SessionManager.getInstance().setEventoSeleccionado(eventoSeleccionado);

            // Navegamos a la vista de detalles
            Stage stage = (Stage) btnVerDetalle.getScene().getWindow();
            ViewNavigator.cargarVistaUsuario("DetalleEventoView.fxml", stage);
        }
    }
}