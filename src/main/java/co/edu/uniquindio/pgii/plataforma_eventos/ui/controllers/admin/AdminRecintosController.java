package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminRecintosController implements Initializable {

    private AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML private TextField                     txtBuscarRecinto;
    @FXML private Button                        btnBuscarRecinto;

    @FXML private TableView<Recinto>            tblRecintos;
    @FXML private TableColumn<Recinto, String>  colNombreRecinto;
    @FXML private TableColumn<Recinto, String>  colDireccion;
    @FXML private TableColumn<Recinto, String>  colCiudad;
    @FXML private TableColumn<Recinto, Integer> colAforo;
    @FXML private TableColumn<Recinto, Integer> colZonas;

    @FXML private Button btnCrearRecinto;
    @FXML private Button btnEditarRecinto;
    @FXML private Button btnEliminarRecinto;
    @FXML private Button btnAbrirModalAsientos;

    @FXML private Label  lblHintDobleClick;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNombreRecinto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDireccion()));
        colCiudad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCiudad()));
        colAforo.setCellValueFactory(d -> new SimpleIntegerProperty(
                d.getValue().getZonas().stream().mapToInt(Zona::getCapacidad).sum()).asObject());
        colZonas.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getZonas().size()).asObject());
        cargarRecintos();
    }

    public void setAdministracionFacade(AdministracionFacade f) { this.administracionFacade = f; }

    @FXML public void onBuscarRecintoAction(ActionEvent e) { onBuscarRecintoClick(e); }

    @FXML
    public void onBuscarRecintoClick(ActionEvent event) {
        String q = txtBuscarRecinto.getText() == null ? "" : txtBuscarRecinto.getText().trim().toLowerCase();
        List<Recinto> filtrados = administracionFacade.listarRecintos().stream()
                .filter(r -> q.isEmpty() || r.getNombre().toLowerCase().contains(q))
                .collect(Collectors.toList());
        tblRecintos.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    public void onCrearRecintoClick(ActionEvent event) {
        String nombre = pedirTexto("Crear Recinto", "Nombre del recinto:");
        if (nombre == null || nombre.isBlank()) return;
        String direccion = pedirTexto("Crear Recinto", "Dirección:");
        if (direccion == null) return;
        String ciudad = pedirTexto("Crear Recinto", "Ciudad:");
        if (ciudad == null) return;
        try {
            administracionFacade.crearRecinto(nombre, direccion, ciudad);
            cargarRecintos();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    public void onEditarRecintoClick(ActionEvent event) {
        mostrarInfo("La edición de recintos no está habilitada. Elimina y vuelve a crear si es necesario.");
    }

    @FXML
    public void onEliminarRecintoClick(ActionEvent event) {
        Recinto sel = tblRecintos.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un recinto."); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el recinto '" + sel.getNombre() + "'?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.YES) return;
        try {
            administracionFacade.eliminarRecinto(sel.getIdRecinto());
            cargarRecintos();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    public void onAbrirModalAsientos(ActionEvent event) {
        Recinto sel = tblRecintos.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un recinto."); return; }
        abrirModalZonas(sel);
    }

    @FXML
    public void onTablaRecintoClick(MouseEvent event) {
        if (event.getClickCount() == 2) onAbrirModalAsientos(null);
    }

    // --- Navegación ---
    @FXML public void onNavDashboard(ActionEvent e) { navegar("AdminDashboardView.fxml"); }
    @FXML public void onNavEventos(ActionEvent e) { navegar("AdminEventosView.fxml"); }
    @FXML public void onNavRecintos(ActionEvent e) { }
    @FXML public void onNavUsuarios(ActionEvent e) { navegar("AdminUsuariosView.fxml"); }
    @FXML public void onNavCompras(ActionEvent e) { navegar("AdminComprasView.fxml"); }
    @FXML public void onNavIncidencias(ActionEvent e) { navegar("AdminIncidenciasView.fxml"); }
    @FXML public void onCerrarSesion(ActionEvent e) {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) tblRecintos.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }
    private void navegar(String fxml) {
        Stage stage = (Stage) tblRecintos.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }

    private void cargarRecintos() {
        tblRecintos.setItems(FXCollections.observableArrayList(administracionFacade.listarRecintos()));
    }

    private void abrirModalZonas(Recinto recinto) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Zonas de " + recinto.getNombre());

        ListView<String> lvZonas = new ListView<>();
        refrescarLista(lvZonas, recinto);

        TextField txtNombre = new TextField();      txtNombre.setPromptText("Nombre zona");
        TextField txtCap = new TextField();         txtCap.setPromptText("Capacidad");
        TextField txtPrecio = new TextField();      txtPrecio.setPromptText("Precio base");
        Button btnAgregar = new Button("Agregar zona");
        btnAgregar.setOnAction(e -> {
            try {
                int cap = Integer.parseInt(txtCap.getText().trim());
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                administracionFacade.agregarZona(recinto.getIdRecinto(),
                        txtNombre.getText().trim(), cap, precio);
                txtNombre.clear(); txtCap.clear(); txtPrecio.clear();
                refrescarLista(lvZonas, recinto);
                cargarRecintos();
            } catch (RuntimeException ex) {
                mostrarError("Datos inválidos: " + ex.getMessage());
            }
        });

        HBox form = new HBox(8, txtNombre, txtCap, txtPrecio, btnAgregar);
        VBox root = new VBox(10, new Label("Zonas del recinto"), lvZonas, form);
        root.setPadding(new Insets(16));
        modal.setScene(new Scene(root, 520, 360));
        modal.showAndWait();
    }

    private void refrescarLista(ListView<String> lv, Recinto recinto) {
        lv.setItems(FXCollections.observableArrayList(
                recinto.getZonas().stream()
                        .map(z -> z.getNombre() + " — cap " + z.getCapacidad()
                                + " — $" + String.format("%,.0f", z.getPrecioBase()))
                        .collect(Collectors.toList())));
    }

    private String pedirTexto(String titulo, String prompt) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle(titulo); d.setHeaderText(null); d.setContentText(prompt);
        return d.showAndWait().orElse(null);
    }

    private void mostrarError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
    private void mostrarInfo(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}
