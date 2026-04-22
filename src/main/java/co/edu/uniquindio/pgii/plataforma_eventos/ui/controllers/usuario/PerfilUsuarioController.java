package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.MedioPago;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class PerfilUsuarioController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Usuario actual: SessionManager.getInstance().getUsuarioActual()
    private PlataformaFacade plataformaFacade;

    // --- COMPONENTES FXML: Datos personales ---
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private Label     lblMensajePerfil;
    @FXML private Button    btnGuardarPerfil;

    // --- COMPONENTES FXML: Medios de pago ---
    @FXML private TableView<MedioPago>               tblMediosPago;
    @FXML private TableColumn<MedioPago, String>     colTitular;
    @FXML private TableColumn<MedioPago, String>     colDigitos;
    @FXML private TextField                          txtTitularTarjeta;
    @FXML private TextField                          txtNumeroTarjeta;
    @FXML private Button                             btnAgregarMedio;
    @FXML private Button                             btnEliminarMedio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblMensajePerfil.setVisible(false);
        lblMensajePerfil.setManaged(false);

        // TODO: Configurar cellValueFactory de colTitular y colDigitos

        // TODO: Pre-cargar los datos del usuario actual en los TextField:
        //   Usuario actual = SessionManager.getInstance().getUsuarioActual();
        //   txtNombre.setText(actual.getNombreCompleto());
        //   txtCorreo.setText(actual.getCorreo());
        //   txtTelefono.setText(actual.getNumeroTelefono());
        //   tblMediosPago.setItems(FXCollections.observableArrayList(actual.getMediosPago()));
    }

    /** Inyecta la fachada de usuario. */
    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    // --- HANDLERS DE EVENTOS ---

    @FXML
    public void onGuardarPerfilClick(ActionEvent event) {
        // TODO: Leer valores de txtNombre, txtCorreo, txtTelefono
        // TODO: Construir o actualizar el objeto Usuario
        // TODO: Delegar → plataformaFacade.actualizarPerfil(usuario)
        // TODO: Mostrar lblMensajePerfil con confirmación o error
    }

    @FXML
    public void onAgregarMedioClick(ActionEvent event) {
        // TODO: Leer txtTitularTarjeta y txtNumeroTarjeta
        // TODO: Crear MedioPago y agregarlo al usuario actual
        // TODO: Refrescar tblMediosPago
        // TODO: Limpiar los campos del formulario de tarjeta
    }

    @FXML
    public void onEliminarMedioClick(ActionEvent event) {
        // TODO: Obtener selección: tblMediosPago.getSelectionModel().getSelectedItem()
        // TODO: Eliminar el MedioPago del usuario actual
        // TODO: Refrescar tblMediosPago
    }
}