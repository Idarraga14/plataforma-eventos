package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CheckoutExtrasController implements Initializable {

    // --- COMPONENTES DE UI ---
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
    private Button btnIrAPagar;
    @FXML
    private Button btnCancelar;

    // --- VARIABLES DE ESTADO ---
    private Evento evento;
    private Zona zona;
    private int cantidad;
    private double subtotalBase;

    // Precios de UI simulados (Veremos el problema de esto en el Sparring)
    private final double PRECIO_VIP = 50000.0;
    private final double PRECIO_SEGURO = 15000.0;
    private final double PRECIO_PARQUEADERO = 20000.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Recuperamos el estado de la transacción
        SessionManager session = SessionManager.getInstance();
        this.evento = session.getEventoSeleccionado();
        this.zona = session.getZonaSeleccionada();
        this.cantidad = session.getCantidadEntradas();

        // 2. Cálculo base
        this.subtotalBase = zona.getPrecioBase() * cantidad;

        // 3. Poblar textos informativos
        lblResumen.setText(String.format("Evento: %s\nZona: %s\nEntradas solicitadas: %d",
                evento.getNombre(), zona.getNombre(), cantidad));

        lblSubtotal.setText(String.format("Subtotal Entradas: $%,.2f", subtotalBase));

        // 4. Configurar Listeners Reactivos para los CheckBoxes
        chkVip.selectedProperty().addListener(
                (obs, oldV, newV) -> actualizarTotalPantalla());
        chkSeguro.selectedProperty().addListener(
                (obs, oldV, newV) -> actualizarTotalPantalla());
        chkParqueadero.selectedProperty().addListener(
                (obs, oldV, newV) -> actualizarTotalPantalla());

        // Inicializar el total visual
        actualizarTotalPantalla();
    }

    /**
     * Recibe el contexto de la compra desde AsignacionController.
     */
    private void actualizarTotalPantalla() {
        double totalDinamico = subtotalBase;

        // Multiplicamos por la cantidad de entradas asumiendo que el extra se aplica a todas
        if (chkVip.isSelected()) totalDinamico += (PRECIO_VIP * cantidad);
        if (chkSeguro.isSelected()) totalDinamico += (PRECIO_SEGURO * cantidad);
        if (chkParqueadero.isSelected())
            totalDinamico += PRECIO_PARQUEADERO; // El parqueadero suele ser por vehículo, no por persona

        lblTotal.setText(String.format("Total a Pagar: $%,.2f", totalDinamico));
    }

    @FXML
    public void onIrAPagarClick(ActionEvent event) {
        // 1. Recolectar la intención del usuario
        List<String> extras = new ArrayList<>();
        if (chkVip.isSelected()) extras.add("VIP");
        if (chkSeguro.isSelected()) extras.add("SEGURO");
        if (chkParqueadero.isSelected()) extras.add("PARQUEADERO");

        // 2. Guardar en sesión
        SessionManager.getInstance().setExtrasSeleccionados(extras);

        // 3. Navegar al simulador de pasarela
        Stage stage = (Stage) btnIrAPagar.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("PagoView.fxml", stage);
    }

    @FXML
    public void onCancelarClick(ActionEvent event) {
        // Limpiamos el carrito temporal (excepto el usuario logueado)
        SessionManager.getInstance().limpiarEventoSeleccionado();
        SessionManager.getInstance().setZonaSeleccionada(null);
        SessionManager.getInstance().setAsientosSeleccionados(new ArrayList<>());
        SessionManager.getInstance().setExtrasSeleccionados(new ArrayList<>());

        // Volvemos a la tienda
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("ExplorarEventosView.fxml", stage);
    }
}