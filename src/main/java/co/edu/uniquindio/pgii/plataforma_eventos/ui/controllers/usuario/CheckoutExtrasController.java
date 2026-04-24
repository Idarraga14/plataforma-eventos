package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.AccesoPreferencialDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.MerchandisingDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.PaqueteVIPDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.ParqueaderoDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.SeguroCancelacionDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX de la pantalla de selección de servicios adicionales (extras) y
 * creación de la orden de compra.
 *
 * <p>Muestra el resumen del pedido (evento, zona, cantidad) y cinco {@code CheckBox} para
 * los decoradores disponibles: VIP, Seguro de Cancelación, Parqueadero, Merchandising y
 * Acceso Preferencial. El total se recalcula en tiempo real al marcar/desmarcar cada extra,
 * usando las constantes de costo de los decoradores como fuente de verdad.</p>
 *
 * <p>Al pulsar "Ir a Pagar", llama a {@link PlataformaFacade#crearOrdenCompra} que aplica
 * la Strategy de asignación, envuelve cada entrada con los decoradores seleccionados
 * (Patrón Decorator) y devuelve la {@link Compra} en estado CREADA. Esta se deposita en
 * {@link SessionManager} y se navega a {@code PagoView}.</p>
 *
 * <p>[Requerimiento: RF-004] - Permite al usuario elegir servicios adicionales (decoradores)
 * para cada entrada; el precio se acumula por entrada seleccionada.</p>
 * <p>[Requerimiento: RF-005] - Inicia el proceso de pago al crear la orden y navegar a la
 * vista de pago.</p>
 * <p>[Patrón: Decorator] - Construye la lista de nombres de extras que la fachada usará para
 * envolver las entradas con los decoradores correspondientes.</p>
 * <p>[Patrón: Facade] - Delega a {@link PlataformaFacade#crearOrdenCompra} la asignación de
 * cupos, aplicación de decoradores y creación del objeto {@link Compra}.</p>
 */
public class CheckoutExtrasController implements Initializable {

    private final PlataformaFacade plataformaFacade = new PlataformaFacadeImpl();

    // --- COMPONENTES FXML ---
    @FXML
    private Label lblResumen;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblTotal;
    @FXML
    private CheckBox chkVip;
    @FXML
    private CheckBox chkSeguro;
    @FXML
    private CheckBox chkParqueadero;
    @FXML
    private CheckBox chkMerchandising;
    @FXML
    private CheckBox chkAccesoPreferencial;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnIrAPagar;

    private double subtotalEntradas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SessionManager sesion = SessionManager.getInstance();
        Evento evento = sesion.getEventoSeleccionado();
        Zona zona = sesion.getZonaSeleccionada();
        int cantidad = Math.max(1, sesion.getCantidadEntradas());

        if (evento != null && zona != null) {
            subtotalEntradas = zona.getPrecioBase() * cantidad;
            lblResumen.setText(evento.getNombre() + " — " + zona.getNombre() + " (" + cantidad + " entradas)");
            lblSubtotal.setText(String.format("Subtotal Entradas: $%,.0f", subtotalEntradas));
        }

        chkVip.selectedProperty().addListener((obs, oldV, newV) -> actualizarTotal());
        chkSeguro.selectedProperty().addListener((obs, oldV, newV) -> actualizarTotal());
        chkParqueadero.selectedProperty().addListener((obs, oldV, newV) -> actualizarTotal());
        chkMerchandising.selectedProperty().addListener((obs, oldV, newV) -> actualizarTotal());
        chkAccesoPreferencial.selectedProperty().addListener((obs, oldV, newV) -> actualizarTotal());
        actualizarTotal();
    }

    // --- HANDLERS ---

    @FXML
    public void onCancelarClick() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("AsignacionView.fxml", stage);
    }

    @FXML
    public void onIrAPagarClick() {
        SessionManager sesion = SessionManager.getInstance();
        Evento evento = sesion.getEventoSeleccionado();
        Zona zona = sesion.getZonaSeleccionada();
        List<Asiento> asientos = sesion.getAsientosSeleccionados();
        int cantidad = sesion.getCantidadEntradas();

        List<String> idAsientos = new ArrayList<>();
        if (asientos != null) {
            for (Asiento a : asientos) idAsientos.add(a.getIdAsiento());
        }

        try {
            Compra orden = plataformaFacade.crearOrdenCompra(
                    sesion.getUsuarioActual().getIdUsuario(),
                    evento.getIdEvento(),
                    zona.getIdZona(),
                    idAsientos,
                    cantidad,
                    getExtrasSeleccionados()
            );
            sesion.setOrdenActual(orden);

            Stage stage = (Stage) btnIrAPagar.getScene().getWindow();
            ViewNavigator.cargarVistaUsuario("PagoView.fxml", stage);
        } catch (RuntimeException ex) {
            mostrarError("No se pudo crear la orden: " + ex.getMessage());
        }
    }

    // --- Helpers ---

    private void actualizarTotal() {
        int cantidad = Math.max(1, SessionManager.getInstance().getCantidadEntradas());
        double total = subtotalEntradas;
        // Los decoradores son la fuente de verdad del costo de cada extra; cada
        // entrada paga el extra individualmente, igual que en la fachada al crear la orden.
        if (chkVip.isSelected()) total += PaqueteVIPDecorator.COSTO * cantidad;
        if (chkSeguro.isSelected()) total += SeguroCancelacionDecorator.COSTO_DEFAULT * cantidad;
        if (chkParqueadero.isSelected()) total += ParqueaderoDecorator.COSTO * cantidad;
        if (chkMerchandising.isSelected()) total += MerchandisingDecorator.COSTO * cantidad;
        if (chkAccesoPreferencial.isSelected()) total += AccesoPreferencialDecorator.COSTO * cantidad;
        lblTotal.setText(String.format("Total a Pagar: $%,.0f", total));
    }

    private List<String> getExtrasSeleccionados() {
        List<String> extras = new ArrayList<>();
        if (chkVip.isSelected()) extras.add("VIP");
        if (chkSeguro.isSelected()) extras.add("SEGURO_CANCELACION");
        if (chkParqueadero.isSelected()) extras.add("PARQUEADERO");
        if (chkMerchandising.isSelected()) extras.add("MERCHANDISING");
        if (chkAccesoPreferencial.isSelected()) extras.add("ACCESO_PREFERENCIAL");
        return extras;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al crear la orden");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
