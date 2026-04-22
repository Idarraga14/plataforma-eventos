package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario.PlataformaFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class DetalleEventoController implements Initializable {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Usuario actual: SessionManager.getInstance().getUsuarioActual()
    private PlataformaFacade plataformaFacade;

    /** Evento que se inyecta desde la vista anterior (ExplorarEventos). */
    private Evento eventoSeleccionado;

    // --- COMPONENTES FXML ---
    @FXML private Label         lblTitulo;
    @FXML private Label         lblCategoria;
    @FXML private Label         lblDescripcion;
    @FXML private Label         lblFecha;
    @FXML private Label         lblCiudad;
    @FXML private Label         lblRecinto;
    @FXML private Label         lblAforo;
    @FXML private Label         lblEstado;
    @FXML private Label         lblReglas;
    @FXML private ListView<String> listPoliticas;
    @FXML private Button        btnVolver;
    @FXML private Button        btnComprar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // La vista se puebla en setEvento(), una vez que el controlador
        // anterior inyecta el evento seleccionado.
    }

    /** Inyecta la fachada de usuario. */
    public void setPlataformaFacade(PlataformaFacade plataformaFacade) {
        this.plataformaFacade = plataformaFacade;
    }

    /**
     * Recibe el evento seleccionado desde la vista anterior y puebla todos
     * los Labels/ListView con su información.
     */
    public void setEvento(Evento evento) {
        this.eventoSeleccionado = evento;
        // TODO: Poblar los componentes:
        //   lblTitulo.setText(evento.getNombre());
        //   lblCategoria.setText(evento.getCategoria().name());
        //   lblDescripcion.setText(evento.getDescripcion());
        //   lblFecha.setText(evento.getFecha().toString());
        //   lblCiudad.setText(evento.getCiudad());
        //   lblRecinto.setText(evento.getRecinto().getNombre());
        //   lblAforo.setText(String.valueOf(evento.getRecinto().getAforo()));
        //   lblEstado.setText(evento.getEstado().name());
        //   listPoliticas.setItems(FXCollections.observableArrayList(evento.getPoliticas()));
    }

    // --- HANDLERS DE EVENTOS ---

    @FXML
    public void onVolverClick(ActionEvent event) {
        // TODO: Navegar de regreso a ExplorarEventosView
    }

    @FXML
    public void onComprarClick(ActionEvent event) {
        // TODO: Navegar a AsignacionView pasando eventoSeleccionado
        // La AsignacionView necesita saber si el evento usa asientos numerados
        // para llamar a asignacionController.configurarVista(esNumerado)
    }
}
