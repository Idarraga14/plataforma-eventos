package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX de la pantalla de detalle de un evento seleccionado.
 *
 * <p>Lee el {@link Evento} inyectado desde {@link SessionManager} y puebla las etiquetas
 * (nombre, categoría, ciudad, fecha, recinto, descripción) y el {@link ListView} de zonas,
 * mostrando para cada zona el precio base y los cupos disponibles en tiempo real consultados
 * a la fachada. El botón "Comprar" navega a la vista de asignación.</p>
 *
 * <p>[Requerimiento: RF-002] - Muestra la información completa del evento para que el
 * usuario decida si continúa con la compra.</p>
 * <p>[Requerimiento: RF-003] - Muestra los cupos disponibles por zona consultando
 * {@link PlataformaFacade#obtenerCuposDisponibles(String, String)}.</p>
 * <p>[Patrón: Facade] - Delega el cálculo de cupos a {@link PlataformaFacade} sin
 * iterar directamente sobre el inventario de asientos.</p>
 */
public class DetalleEventoController implements Initializable {

    // Evento seleccionado (inyectado desde ExplorarEventosController)
    private Evento eventoActual;

    private final PlataformaFacade facade = new PlataformaFacadeImpl();

    // --- COMPONENTES FXML ---
    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblCategoria;

    @FXML
    private Label lblCiudad;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblRecinto;

    @FXML
    private TextArea lblDescripcion;

    @FXML
    private ListView<String> listZonas;

    @FXML
    private Button btnVolver;

    @FXML
    private Button btnComprar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.eventoActual = SessionManager.getInstance().getEventoSeleccionado();

        // Validación de seguridad (por si alguien navega aquí por error sin seleccionar evento)
        if (this.eventoActual == null) {
            btnComprar.setDisable(true);
            lblTitulo.setText("Error: No se pudo cargar el evento.");
            return;
        }

        // 2. Poblar la interfaz
        poblarDatos();
    }

    private void poblarDatos() {
        lblTitulo.setText(eventoActual.getNombre());
        lblCategoria.setText(eventoActual.getCategoria().name());
        lblCiudad.setText(eventoActual.getCiudad());
        lblRecinto.setText(eventoActual.getRecinto().getNombre());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblFecha.setText(eventoActual.getFecha().format(formatter));

        // Formatear descripción y políticas (si las hubiera)
        lblDescripcion.setText(eventoActual.getDescripcion());
        lblDescripcion.setEditable(false);
        lblDescripcion.setWrapText(true);

        // Mostrar Zonas y Precios en la lista
        listZonas.getItems().clear();
        for (Zona zona : eventoActual.getRecinto().getZonas()) {
            int disponibles = facade.obtenerCuposDisponibles(eventoActual.getIdEvento(), zona.getIdZona());
            String infoZona = String.format("%s - Precio: $%,.2f - Cupos Disponibles: %d",
                    zona.getNombre(), zona.getPrecioBase(), disponibles);
            listZonas.getItems().add(infoZona);
        }
    }

    // --- HANDLERS DE EVENTOS ---

    @FXML
    public void onVolverClick() {
        // Limpiamos la selección para no dejar basura en memoria
        SessionManager.getInstance().limpiarEventoSeleccionado();

        Stage stage = (Stage) btnVolver.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("ExplorarEventosView.fxml", stage);
    }

    @FXML
    public void onComprarClick() {
        Stage stage = (Stage) btnComprar.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("AsignacionView.fxml", stage);
    }
}
