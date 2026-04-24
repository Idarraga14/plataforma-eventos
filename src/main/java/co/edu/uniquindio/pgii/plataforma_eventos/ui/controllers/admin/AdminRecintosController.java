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

/**
 * Controlador JavaFX del módulo de gestión de recintos para el administrador.
 *
 * <p>Lista los recintos registrados con columnas de aforo total y número de zonas.
 * Permite crear nuevos recintos mediante diálogos secuenciales de texto, eliminarlos
 * con confirmación y gestionar sus zonas a través de un modal que lista las zonas
 * existentes y permite añadir nuevas (nombre, capacidad, precio base).
 * El doble clic sobre una fila abre directamente el modal de zonas.</p>
 *
 * <p>[Requerimiento: RF-013] - Implementa la gestión completa de recintos físicos
 * (crear, eliminar) y la administración de sus zonas (agregar zonas con asientos/capacidad).</p>
 * <p>[Patrón: Facade] - Delega todas las operaciones sobre recintos y zonas a
 * {@link AdministracionFacade}, sin manipular directamente las listas del repositorio.</p>
 */
public class AdminRecintosController implements Initializable {

    private final AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML
    private TextField txtBuscarRecinto;

    @FXML
    private TableView<Recinto> tblRecintos;
    @FXML
    private TableColumn<Recinto, String> colNombreRecinto;
    @FXML
    private TableColumn<Recinto, String> colDireccion;
    @FXML
    private TableColumn<Recinto, String> colCiudad;
    @FXML
    private TableColumn<Recinto, Integer> colAforo;
    @FXML
    private TableColumn<Recinto, Integer> colZonas;

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

    @FXML
    public void onBuscarRecintoAction(ActionEvent e) {
        onBuscarRecintoClick(e);
    }

    @FXML
    public void onBuscarRecintoClick(ActionEvent event) {
        String q = txtBuscarRecinto.getText() == null ? "" : txtBuscarRecinto.getText().trim().toLowerCase();
        List<Recinto> filtrados = administracionFacade.listarRecintos().stream()
                .filter(r -> q.isEmpty() || r.getNombre().toLowerCase().contains(q))
                .collect(Collectors.toList());
        tblRecintos.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    public void onCrearRecintoClick() {
        String nombre = pedirTexto("Nombre del recinto:");
        if (nombre == null || nombre.isBlank()) return;
        String direccion = pedirTexto("Dirección:");
        if (direccion == null) return;
        String ciudad = pedirTexto("Ciudad:");
        if (ciudad == null) return;
        try {
            administracionFacade.crearRecinto(nombre, direccion, ciudad);
            cargarRecintos();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    public void onEditarRecintoClick() {
        mostrarInfo();
    }

    @FXML
    public void onEliminarRecintoClick() {
        Recinto sel = tblRecintos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un recinto.");
            return;
        }
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
    public void onAbrirModalAsientos() {
        Recinto sel = tblRecintos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un recinto.");
            return;
        }
        abrirModalZonas(sel);
    }

    @FXML
    public void onTablaRecintoClick(MouseEvent event) {
        if (event.getClickCount() == 2) onAbrirModalAsientos();
    }

    // --- Navegación ---
    @FXML
    public void onNavDashboard() {
        navegar("AdminDashboardView.fxml");
    }

    @FXML
    public void onNavEventos() {
        navegar("AdminEventosView.fxml");
    }

    @FXML
    public void onNavRecintos() {
    }

    @FXML
    public void onNavUsuarios() {
        navegar("AdminUsuariosView.fxml");
    }

    @FXML
    public void onNavCompras() {
        navegar("AdminComprasView.fxml");
    }

    @FXML
    public void onNavAsientos() {
        navegar("AdminGestorAsientosView.fxml");
    }

    @FXML
    public void onNavReportes() {
        navegar("AdminReportesView.fxml");
    }

    @FXML
    public void onNavIncidencias() {
        navegar("AdminIncidenciasView.fxml");
    }

    @FXML
    public void onCerrarSesion() {
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

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre zona");
        TextField txtCap = new TextField();
        txtCap.setPromptText("Capacidad");
        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Precio base");
        Button btnAgregar = new Button("Agregar zona");
        btnAgregar.setOnAction(e -> {
            try {
                int cap = Integer.parseInt(txtCap.getText().trim());
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                administracionFacade.agregarZona(recinto.getIdRecinto(),
                        txtNombre.getText().trim(), cap, precio);
                txtNombre.clear();
                txtCap.clear();
                txtPrecio.clear();
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

    private String pedirTexto(String prompt) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Crear Recinto");
        d.setHeaderText(null);
        d.setContentText(prompt);
        return d.showAndWait().orElse(null);
    }

    private void mostrarError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    private void mostrarInfo() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText("La edición de recintos no está habilitada. Elimina y vuelve a crear si es necesario.");
        a.showAndWait();
    }
}
