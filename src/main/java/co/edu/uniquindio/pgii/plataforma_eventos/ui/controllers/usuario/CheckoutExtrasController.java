package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.event.ActionEvent;
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

public class CheckoutExtrasController implements Initializable {

    private static final double PRECIO_VIP         = 50_000.0;
    private static final double PRECIO_SEGURO      = 15_000.0;
    private static final double PRECIO_PARQUEADERO = 20_000.0;

    private final PlataformaFacade plataformaFacade = new PlataformaFacadeImpl();

    // --- COMPONENTES FXML ---
    @FXML private Label    lblResumen;
    @FXML private Label    lblSubtotal;
    @FXML private Label    lblTotal;
    @FXML private CheckBox chkVip;
    @FXML private CheckBox chkSeguro;
    @FXML private CheckBox chkParqueadero;
    @FXML private Button   btnCancelar;
    @FXML private Button   btnIrAPagar;

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
        actualizarTotal();
    }

    // --- HANDLERS ---

    @FXML
    public void onCancelarClick(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("AsignacionView.fxml", stage);
    }

    @FXML
    public void onIrAPagarClick(ActionEvent event) {
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
        double total = subtotalEntradas;
        if (chkVip.isSelected())         total += PRECIO_VIP;
        if (chkSeguro.isSelected())      total += PRECIO_SEGURO;
        if (chkParqueadero.isSelected()) total += PRECIO_PARQUEADERO;
        lblTotal.setText(String.format("Total a Pagar: $%,.0f", total));
    }

    private List<String> getExtrasSeleccionados() {
        List<String> extras = new ArrayList<>();
        if (chkVip.isSelected())         extras.add("VIP");
        if (chkSeguro.isSelected())      extras.add("SEGURO_CANCELACION");
        if (chkParqueadero.isSelected()) extras.add("PARQUEADERO");
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
