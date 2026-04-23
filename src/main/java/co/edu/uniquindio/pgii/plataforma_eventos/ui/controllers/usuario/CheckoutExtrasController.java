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

    private PlataformaFacade plataformaFacade = new PlataformaFacadeImpl();

    // --- COMPONENTES FXML ---
    @FXML private CheckBox chkPaseVip;
    @FXML private CheckBox chkSeguroCancelacion;
    @FXML private CheckBox chkParqueadero;
    @FXML private Label    lblPrecioPaseVip;
    @FXML private Label    lblPrecioSeguro;
    @FXML private Label    lblPrecioParqueadero;
    @FXML private Label    lblTotalExtras;
    @FXML private Button   btnVolver;
    @FXML private Button   btnContinuar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualizarTotalExtras();
    }

    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    // --- HANDLERS ---

    @FXML
    public void onExtrasChanged(ActionEvent event) {
        actualizarTotalExtras();
    }

    @FXML
    public void onVolverClick(ActionEvent event) {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("AsignacionView.fxml", stage);
    }

    @FXML
    public void onContinuarClick(ActionEvent event) {
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

            Stage stage = (Stage) btnContinuar.getScene().getWindow();
            ViewNavigator.cargarVistaUsuario("PagoView.fxml", stage);
        } catch (RuntimeException ex) {
            mostrarError("No se pudo crear la orden: " + ex.getMessage());
        }
    }

    // --- Helpers ---

    private void actualizarTotalExtras() {
        double total = 0;
        if (chkPaseVip.isSelected())           total += 50_000.0;
        if (chkSeguroCancelacion.isSelected()) total += 30_000.0;
        if (chkParqueadero.isSelected())       total += 7_000.0;
        lblTotalExtras.setText(String.format("$%,.0f", total));
    }

    public List<String> getExtrasSeleccionados() {
        List<String> extras = new ArrayList<>();
        if (chkPaseVip.isSelected())           extras.add("VIP");
        if (chkSeguroCancelacion.isSelected()) extras.add("SEGURO_CANCELACION");
        if (chkParqueadero.isSelected())       extras.add("PARQUEADERO");
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
