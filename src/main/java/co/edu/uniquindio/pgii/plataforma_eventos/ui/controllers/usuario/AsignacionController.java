package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AsignacionController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Usuario actual: SessionManager.getInstance().getUsuarioActual()
    private PlataformaFacade plataformaFacade;
    private Evento eventoSeleccionado;

    /** Asiento elegido (modo numerado) o null (modo por zona). */
    private Asiento asientoSeleccionado;

    // --- COMPONENTES FXML ---
    @FXML private ComboBox<Zona> comboZonas;
    @FXML private Label          lblPrecioZona;

    /** Panel del GridPane de asientos — se muestra sólo en modo numerado. */
    @FXML private VBox           pnlAsientos;
    @FXML private GridPane       gridAsientos;

    /** Panel de spinner de cantidad — se muestra sólo en modo por zona. */
    @FXML private VBox           pnlCantidad;
    @FXML private Spinner<Integer> spinnerCantidad;

    @FXML private Label          lblResumenSeleccion;
    @FXML private Button         btnVolver;
    @FXML private Button         btnContinuar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Configurar comboZonas con la lista de zonas del evento inyectado
        // TODO: Listener en comboZonas para actualizar lblPrecioZona y el grid/spinner
    }

    /** Inyecta la fachada de usuario. */
    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    /** Recibe el evento desde la vista anterior. */
    public void setEvento(Evento evento) {
        this.eventoSeleccionado = evento;
        // TODO: Poblar comboZonas con evento.getRecinto().getZonas()
    }

    /**
     * MÉTODO DINÁMICO CLAVE.
     * Configura la vista según si el evento es numerado (con asientos) o por zona.
     *
     * @param esNumerado true → muestra GridPane de asientos y oculta el spinner.
     *                   false → muestra el spinner de cantidad y oculta el GridPane.
     */
    public void configurarVista(boolean esNumerado) {
        pnlAsientos.setVisible(esNumerado);
        pnlAsientos.setManaged(esNumerado);

        pnlCantidad.setVisible(!esNumerado);
        pnlCantidad.setManaged(!esNumerado);

        if (esNumerado) {
            // TODO: Construir dinámicamente el GridPane con los botones de asiento
            //       recorriendo zona.getAsientos() y coloreando según AsientoEstado
        }
    }

    // --- HANDLERS DE EVENTOS ---

    @FXML
    public void onZonaSeleccionada(ActionEvent event) {
        // TODO: Obtener Zona seleccionada y actualizar lblPrecioZona
        // TODO: Limpiar selección anterior (asientoSeleccionado = null, lblResumenSeleccion)
        // TODO: Si esNumerado, reconstruir gridAsientos con los asientos de la nueva zona
    }

    @FXML
    public void onVolverClick(ActionEvent event) {
        // TODO: Navegar de regreso a DetalleEventoView
    }

    @FXML
    public void onContinuarClick(ActionEvent event) {
        // TODO: Validar que haya una zona/asiento seleccionado
        // TODO: Navegar a CheckoutExtrasView pasando la zona, asiento y evento
    }
}
