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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CheckoutExtrasController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Usuario actual: SessionManager.getInstance().getUsuarioActual()
    private PlataformaFacade plataformaFacade;

    // Datos del contexto de compra (inyectados desde AsignacionController)
    private Evento eventoSeleccionado;
    private Zona   zonaSeleccionada;
    private Asiento asientoSeleccionado; // puede ser null si modo por zona

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

    // Precios simulados de los extras (se podrían inyectar desde la fachada)
    private static final double PRECIO_VIP        = 150_000.0;
    private static final double PRECIO_SEGURO      = 30_000.0;
    private static final double PRECIO_PARQUEADERO = 20_000.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualizarTotalExtras();
    }

    /** Inyecta la fachada de usuario. */
    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    /** Recibe el contexto de la compra desde AsignacionController. */
    public void setContextoCompra(Evento evento, Zona zona, Asiento asiento) {
        this.eventoSeleccionado   = evento;
        this.zonaSeleccionada     = zona;
        this.asientoSeleccionado  = asiento;
    }

    // --- HANDLERS DE EVENTOS ---

    /** Recalcula el total cada vez que el usuario marca/desmarca un extra. */
    @FXML
    public void onExtrasChanged(ActionEvent event) {
        actualizarTotalExtras();
    }

    @FXML
    public void onVolverClick(ActionEvent event) {
        // TODO: Navegar de regreso a AsignacionView
    }

    @FXML
    public void onContinuarClick(ActionEvent event) {
        // TODO: Construir la lista de IDs de extras seleccionados
        // TODO: Navegar a PagoView pasando el contexto + lista de extras
    }

    // --- LÓGICA AUXILIAR PRIVADA ---

    private void actualizarTotalExtras() {
        double total = 0;
        if (chkPaseVip.isSelected())          total += PRECIO_VIP;
        if (chkSeguroCancelacion.isSelected()) total += PRECIO_SEGURO;
        if (chkParqueadero.isSelected())       total += PRECIO_PARQUEADERO;
        lblTotalExtras.setText(String.format("$%,.0f", total));
    }

    /** Devuelve los identificadores de los extras seleccionados. */
    public List<String> getExtrasSeleccionados() {
        List<String> extras = new ArrayList<>();
        if (chkPaseVip.isSelected())          extras.add("VIP");
        if (chkSeguroCancelacion.isSelected()) extras.add("SEGURO_CANCELACION");
        if (chkParqueadero.isSelected())       extras.add("PARQUEADERO");
        return extras;
    }
}