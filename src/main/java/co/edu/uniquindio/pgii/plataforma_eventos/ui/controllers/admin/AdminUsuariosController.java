package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminUsuariosController implements Initializable {

    private AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML private TextField                    txtBuscarUsuario;
    @FXML private Button                       btnBuscarUsuario;
    @FXML private Button                       btnLimpiarBusqueda;

    @FXML private TableView<Usuario>           tblUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colTelefono;
    @FXML private TableColumn<Usuario, String> colEstado;

    @FXML private Button btnCrearUsuario;
    @FXML private Button btnEditarUsuario;
    @FXML private Button btnBloquearUsuario;

    @FXML private Label  lblTotalUsuarios;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreCompleto()));
        colCorreo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCorreo()));
        colTelefono.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNumeroTelefono()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getEsAdmin() ? "ADMIN" : "CLIENTE"));
        cargarUsuarios();
    }

    public void setAdministracionFacade(AdministracionFacade f) { this.administracionFacade = f; }

    @FXML public void onBuscarUsuarioAction(ActionEvent e) { onBuscarUsuarioClick(e); }

    @FXML
    public void onBuscarUsuarioClick(ActionEvent event) {
        String q = txtBuscarUsuario.getText() == null ? "" : txtBuscarUsuario.getText().trim().toLowerCase();
        List<Usuario> filtrados = administracionFacade.listarUsuarios().stream()
                .filter(u -> q.isEmpty()
                        || u.getNombreCompleto().toLowerCase().contains(q)
                        || u.getCorreo().toLowerCase().contains(q))
                .collect(Collectors.toList());
        tblUsuarios.setItems(FXCollections.observableArrayList(filtrados));
        lblTotalUsuarios.setText("Total: " + filtrados.size() + " usuarios");
    }

    @FXML
    public void onLimpiarBusquedaClick(ActionEvent event) {
        txtBuscarUsuario.clear();
        cargarUsuarios();
    }

    @FXML
    public void onCrearUsuarioClick(ActionEvent event) {
        Optional<Usuario> res = mostrarDialogoUsuario(null);
        res.ifPresent(ignored -> cargarUsuarios());
    }

    @FXML
    public void onEditarUsuarioClick(ActionEvent event) {
        Usuario sel = tblUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un usuario."); return; }
        mostrarDialogoUsuario(sel).ifPresent(ignored -> cargarUsuarios());
    }

    @FXML
    public void onBloquearUsuarioClick(ActionEvent event) {
        Usuario sel = tblUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un usuario."); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al usuario '" + sel.getNombreCompleto() + "'?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.YES) return;
        try {
            administracionFacade.eliminarUsuario(sel.getIdUsuario());
            cargarUsuarios();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    // --- Navegación ---
    @FXML public void onNavDashboard(ActionEvent e) { navegar("AdminDashboardView.fxml"); }
    @FXML public void onNavEventos(ActionEvent e) { navegar("AdminEventosView.fxml"); }
    @FXML public void onNavRecintos(ActionEvent e) { navegar("AdminRecintosView.fxml"); }
    @FXML public void onNavUsuarios(ActionEvent e) { }
    @FXML public void onNavCompras(ActionEvent e) { navegar("AdminComprasView.fxml"); }
    @FXML public void onNavIncidencias(ActionEvent e) { navegar("AdminIncidenciasView.fxml"); }
    @FXML public void onCerrarSesion(ActionEvent e) {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) tblUsuarios.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }
    private void navegar(String fxml) {
        Stage stage = (Stage) tblUsuarios.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }

    private void cargarUsuarios() {
        List<Usuario> lista = administracionFacade.listarUsuarios();
        tblUsuarios.setItems(FXCollections.observableArrayList(lista));
        tblUsuarios.refresh();
        lblTotalUsuarios.setText("Total: " + lista.size() + " usuarios");
    }

    private Optional<Usuario> mostrarDialogoUsuario(Usuario existente) {
        Dialog<Usuario> dlg = new Dialog<>();
        dlg.setTitle(existente == null ? "Crear Usuario" : "Editar Usuario");
        dlg.setHeaderText(null);
        ButtonType ok = new ButtonType(existente == null ? "Crear" : "Guardar", ButtonType.OK.getButtonData());
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        TextField txtNombre = new TextField(existente == null ? "" : existente.getNombreCompleto());
        TextField txtCorreo = new TextField(existente == null ? "" : existente.getCorreo());
        TextField txtTel    = new TextField(existente == null ? "" : existente.getNumeroTelefono());
        TextField txtPass   = new TextField(); txtPass.setPromptText("Contraseña");
        CheckBox  chkAdmin  = new CheckBox("Administrador");
        if (existente != null) { txtPass.setDisable(true); chkAdmin.setSelected(existente.getEsAdmin()); chkAdmin.setDisable(true); }

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(8); gp.setPadding(new Insets(12));
        gp.addRow(0, new Label("Nombre:"), txtNombre);
        gp.addRow(1, new Label("Correo:"), txtCorreo);
        gp.addRow(2, new Label("Teléfono:"), txtTel);
        gp.addRow(3, new Label("Password:"), txtPass);
        gp.add(chkAdmin, 1, 4);
        dlg.getDialogPane().setContent(gp);

        dlg.setResultConverter(bt -> {
            if (bt != ok) return null;
            try {
                if (existente == null) {
                    return administracionFacade.crearUsuario(txtNombre.getText().trim(),
                            txtCorreo.getText().trim(), txtTel.getText().trim(),
                            txtPass.getText(), chkAdmin.isSelected());
                }
                administracionFacade.actualizarUsuario(existente.getIdUsuario(),
                        txtNombre.getText().trim(), txtCorreo.getText().trim(), txtTel.getText().trim());
                return existente;
            } catch (RuntimeException ex) {
                mostrarError(ex.getMessage());
                return null;
            }
        });
        return dlg.showAndWait();
    }

    private void mostrarError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}
