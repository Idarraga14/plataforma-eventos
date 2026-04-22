package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.MedioPago;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PagoController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Usuario actual: SessionManager.getInstance().getUsuarioActual()
    private PlataformaFacade plataformaFacade;

    // Contexto de la compra (inyectado desde CheckoutExtrasController)
    private Evento         eventoSeleccionado;
    private Zona           zonaSeleccionada;
    private Asiento        asientoSeleccionado; // puede ser null si modo por zona
    private List<String>   extrasSeleccionados;

    // --- COMPONENTES FXML ---
    @FXML private Label                   lblTotalAPagar;
    @FXML private TabPane                 tabPanePago;
    @FXML private ComboBox<MedioPago>     comboMediosPago;
    @FXML private Label                   lblSinTarjetas;
    @FXML private TextField               txtTitularNueva;
    @FXML private TextField               txtNumTarjeta;
    @FXML private TextField               txtCvv;
    @FXML private TextField               txtVencimiento;
    @FXML private Label                   lblMensajePago;
    @FXML private Button                  btnVolver;
    @FXML private Button                  btnPagar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblMensajePago.setVisible(false);
        lblMensajePago.setManaged(false);

        // TODO: Poblar comboMediosPago con SessionManager.getInstance().getUsuarioActual().getMediosPago()
        // TODO: Si la lista está vacía → mostrar lblSinTarjetas y seleccionar Tab "Nueva Tarjeta"
    }

    /** Inyecta la fachada de usuario. */
    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    /**
     * Recibe el contexto completo de la compra para ejecutar el pago.
     *
     * @param evento   el evento a comprar
     * @param zona     la zona seleccionada
     * @param asiento  el asiento (null si es modo por zona)
     * @param extras   IDs de los extras seleccionados
     * @param total    el monto calculado a cobrar
     */
    public void setContextoCompra(Evento evento, Zona zona, Asiento asiento,
                                  List<String> extras, double total) {
        this.eventoSeleccionado  = evento;
        this.zonaSeleccionada    = zona;
        this.asientoSeleccionado = asiento;
        this.extrasSeleccionados = extras;
        lblTotalAPagar.setText(String.format("$%,.0f", total));
    }

    // --- HANDLERS DE EVENTOS ---

    @FXML
    public void onVolverClick(ActionEvent event) {
        // TODO: Navegar de regreso a CheckoutExtrasView
    }

    @FXML
    public void onPagarClick(ActionEvent event) {
        // TODO: Determinar si se usa tarjeta guardada o nueva tarjeta según tabPanePago.getSelectionModel()
        // TODO: Delegar TODA la lógica de compra a:
        //       plataformaFacade.realizarCompra(
        //           SessionManager.getInstance().getUsuarioActual().getIdUsuario(),
        //           eventoSeleccionado.getIdEvento(),
        //           zonaSeleccionada.getIdZona(),
        //           asientoSeleccionado != null ? asientoSeleccionado.getIdAsiento() : null,
        //           extrasSeleccionados,
        //           txtNumTarjeta.getText(),
        //           txtCvv.getText()
        //       );
        // TODO: Si éxito → navegar a HistorialComprasView o mostrar confirmación
        // TODO: Si falla → mostrar lblMensajePago con el error
    }
}
