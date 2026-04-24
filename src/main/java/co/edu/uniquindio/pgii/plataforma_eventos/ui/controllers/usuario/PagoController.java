package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.MedioPago;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX de la pantalla de pago de la orden de compra.
 *
 * <p>Presenta el total de la orden activa y dos pestañas de pago: tarjeta guardada
 * (seleccionada desde un {@code ComboBox} con los medios del usuario en sesión) o
 * tarjeta nueva (campos titular, número, CVV, vencimiento). Al pulsar "Pagar" delega
 * en {@link PlataformaFacade#procesarPagoOrden}, que invoca el Adaptador de pago
 * ({@code SimuladorPagoAdapter}) y aplica las transiciones de Estado a la Compra.</p>
 *
 * <p>Si el banco rechaza el pago, la fachada ya cancela la orden y libera los asientos;
 * el controlador sólo limpia la sesión y muestra el mensaje de rechazo.
 * Si el usuario pulsa "Volver", cancela la orden activa explícitamente para liberar los
 * asientos que quedaron bloqueados durante la reserva.</p>
 *
 * <p>[Requerimiento: RF-005] - Implementa el flujo de pago con tarjeta, incluyendo la
 * integración con el simulador de pasarela y la transición CREADA → PAGADA.</p>
 * <p>[Requerimiento: RF-010] - Al volver, cancela la orden activa (CREADA → CANCELADA),
 * liberando los asientos reservados.</p>
 * <p>[Patrón: Adapter] - Consume el Adaptador de pago a través de la fachada
 * ({@code PlataformaFacade#procesarPagoOrden}), sin acoplarse directamente al simulador.</p>
 * <p>[Patrón: State] - La transición de estado de la compra (CREADA→PAGADA o CREADA→CANCELADA)
 * la ejecuta el Patrón State en el dominio; la UI sólo interpreta el resultado.</p>
 */
public class PagoController implements Initializable {

    private final PlataformaFacade plataformaFacade = new PlataformaFacadeImpl();

    // --- COMPONENTES FXML ---
    @FXML
    private Label lblTotalAPagar;
    @FXML
    private TabPane tabPanePago;
    @FXML
    private ComboBox<MedioPago> comboMediosPago;
    @FXML
    private Label lblSinTarjetas;
    @FXML
    private TextField txtNumTarjeta;
    @FXML
    private TextField txtCvv;
    @FXML
    private Label lblMensajePago;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnPagar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblMensajePago.setVisible(false);
        lblMensajePago.setManaged(false);

        // Total desde la orden CREADA
        Compra orden = SessionManager.getInstance().getOrdenActual();
        if (orden != null) {
            lblTotalAPagar.setText(String.format("$%,.0f", orden.getTotal()));
        }

        configurarMediosPago();
    }

    // --- HANDLERS ---

    @FXML
    public void onVolverClick() {
        // Volver equivale a cancelar la orden creada para liberar los asientos bloqueados.
        cancelarOrdenActual();
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("CheckoutExtrasView.fxml", stage);
    }

    @FXML
    public void onPagarClick() {
        Compra orden = SessionManager.getInstance().getOrdenActual();
        if (orden == null) {
            mostrarMensaje("No hay una orden activa para procesar.");
            return;
        }

        String numTarjeta;
        String cvv;

        Tab tabActivo = tabPanePago.getSelectionModel().getSelectedItem();
        boolean esNuevaTarjeta = tabActivo != null
                && tabActivo.getText() != null
                && tabActivo.getText().toLowerCase().contains("nueva");

        if (esNuevaTarjeta || comboMediosPago.getValue() == null) {
            numTarjeta = txtNumTarjeta.getText();
            cvv = txtCvv.getText();
            if (numTarjeta == null || numTarjeta.isBlank() || cvv == null || cvv.isBlank()) {
                mostrarMensaje("Complete los datos de la tarjeta.");
                return;
            }
        } else {
            // Usa el medio de pago guardado (CVV no está almacenado en el dominio → lo pide igual)
            numTarjeta = comboMediosPago.getValue().getUltimosCuatro();
            cvv = txtCvv.getText() == null ? "" : txtCvv.getText();
        }

        try {
            plataformaFacade.procesarPagoOrden(orden.getIdCompra(), numTarjeta, cvv);
            SessionManager.getInstance().limpiarOrdenActual();
            Stage stage = (Stage) btnPagar.getScene().getWindow();
            ViewNavigator.cargarVistaUsuario("HistorialComprasView.fxml", stage);
        } catch (RuntimeException ex) {
            // El facade ya cancela la orden y libera los asientos si el banco rechaza.
            SessionManager.getInstance().limpiarOrdenActual();
            mostrarMensaje("Pago rechazado: " + ex.getMessage());
            btnPagar.setDisable(true);
        }
    }

    // --- Helpers ---

    private void configurarMediosPago() {
        List<MedioPago> medios = SessionManager.getInstance().getUsuarioActual().getMediosPago();

        comboMediosPago.setConverter(new StringConverter<>() {
            @Override
            public String toString(MedioPago mp) {
                return mp == null ? "" : mp.getTitular() + " •••• " + mp.getUltimosCuatro();
            }

            @Override
            public MedioPago fromString(String s) {
                return null;
            }
        });
        comboMediosPago.setItems(FXCollections.observableArrayList(medios));

        boolean sinTarjetas = medios.isEmpty();
        lblSinTarjetas.setVisible(sinTarjetas);
        lblSinTarjetas.setManaged(sinTarjetas);
        if (sinTarjetas && tabPanePago.getTabs().size() > 1) {
            tabPanePago.getSelectionModel().select(1);
        }
    }

    private void cancelarOrdenActual() {
        Compra orden = SessionManager.getInstance().getOrdenActual();
        if (orden == null) return;
        try {
            plataformaFacade.cancelarOrdenCompra(orden.getIdCompra());
        } catch (RuntimeException ignored) {
            // Si ya estaba cancelada por otra vía, no hay nada que hacer
        } finally {
            SessionManager.getInstance().limpiarOrdenActual();
        }
    }

    private void mostrarMensaje(String texto) {
        lblMensajePago.setText(texto);
        lblMensajePago.setTextFill(true ? Color.CRIMSON : Color.GREEN);
        lblMensajePago.setVisible(true);
        lblMensajePago.setManaged(true);
    }
}
