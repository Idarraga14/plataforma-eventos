package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminGestorAsientosController implements Initializable {

    private final AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML private ComboBox<Evento> comboEventos;
    @FXML private VBox             panelZonas;
    @FXML private Label            lblEstado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Solo mostramos eventos que tienen zonas con asientos numerados
        List<Evento> conAsientos = administracionFacade.listarEventos().stream()
                .filter(e -> e.getRecinto().getZonas().stream()
                        .anyMatch(z -> !z.getAsientos().isEmpty()))
                .toList();

        comboEventos.setItems(FXCollections.observableArrayList(conAsientos));
        comboEventos.setConverter(new StringConverter<>() {
            @Override public String toString(Evento e)    { return e == null ? "" : e.getNombre(); }
            @Override public Evento fromString(String s) { return null; }
        });
    }

    @FXML
    public void onEventoSeleccionado(ActionEvent event) {
        Evento eventoSel = comboEventos.getValue();
        panelZonas.getChildren().clear();

        if (eventoSel == null) return;

        lblEstado.setText("Evento: " + eventoSel.getNombre()
                + " | Haz clic en una silla para cambiar su estado.");

        for (Zona zona : eventoSel.getRecinto().getZonas()) {
            if (zona.getAsientos().isEmpty()) continue;

            Label lblZona = new Label(zona.getNombre());
            lblZona.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 0 0 6 0;");

            GridPane grid = new GridPane();
            grid.setHgap(5);
            grid.setVgap(5);
            grid.setPadding(new Insets(6));

            int col = 0, fila = 0;
            for (Asiento asiento : zona.getAsientos()) {
                AsientoEvento ae = eventoSel.obtenerAsientoEvento(asiento.getIdAsiento());
                Button btn = crearBotonAsiento(asiento, ae, eventoSel);
                grid.add(btn, col, fila);
                col++;
                if (col == 10) { col = 0; fila++; }
            }

            VBox bloque = new VBox(4, lblZona, grid);
            bloque.setStyle("-fx-border-color: #dfe6e9; -fx-border-width: 1; -fx-padding: 10;");
            panelZonas.getChildren().add(bloque);
        }
    }

    private Button crearBotonAsiento(Asiento asiento, AsientoEvento ae, Evento evento) {
        Button btn = new Button(asiento.getSalida());
        btn.setPrefSize(44, 44);
        aplicarEstilo(btn, ae.getEstado());

        if (ae.getEstado() == AsientoEstado.VENDIDO) {
            btn.setDisable(true);
            return btn;
        }

        btn.setOnAction(e -> {
            if (ae.getEstado() == AsientoEstado.DISPONIBLE) {
                Optional<ButtonType> r = confirmar(
                        "¿Bloquear silla " + asiento.getSalida() + "?",
                        "Quedará BLOQUEADA para este evento.");
                if (r.isPresent() && r.get() == ButtonType.YES) {
                    administracionFacade.bloquearAsientoEnEvento(
                            evento.getIdEvento(), asiento.getIdAsiento());
                    aplicarEstilo(btn, AsientoEstado.BLOQUEADO);
                }
            } else {
                Optional<ButtonType> r = confirmar(
                        "¿Liberar silla " + asiento.getSalida() + "?",
                        "Quedará DISPONIBLE para este evento.");
                if (r.isPresent() && r.get() == ButtonType.YES) {
                    administracionFacade.habilitarAsientoEnEvento(
                            evento.getIdEvento(), asiento.getIdAsiento());
                    aplicarEstilo(btn, AsientoEstado.DISPONIBLE);
                }
            }
        });

        return btn;
    }

    private void aplicarEstilo(Button btn, AsientoEstado estado) {
        String color = switch (estado) {
            case DISPONIBLE -> "#27ae60";
            case BLOQUEADO  -> "#e67e22";
            case VENDIDO    -> "#e74c3c";
            default         -> "#95a5a6";
        };
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-cursor: hand;");
    }

    private Optional<ButtonType> confirmar(String titulo, String contenido) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, contenido, ButtonType.YES, ButtonType.NO);
        a.setTitle(titulo);
        a.setHeaderText(null);
        return a.showAndWait();
    }

    // --- Navegación ---
    @FXML public void onNavDashboard(ActionEvent e)  { navegar("AdminDashboardView.fxml"); }
    @FXML public void onNavEventos(ActionEvent e)    { navegar("AdminEventosView.fxml"); }
    @FXML public void onNavRecintos(ActionEvent e)   { navegar("AdminRecintosView.fxml"); }
    @FXML public void onNavUsuarios(ActionEvent e)   { navegar("AdminUsuariosView.fxml"); }
    @FXML public void onNavCompras(ActionEvent e)    { navegar("AdminComprasView.fxml"); }
    @FXML public void onNavAsientos(ActionEvent e)   { }
    @FXML public void onNavReportes(ActionEvent e)   { navegar("AdminReportesView.fxml"); }
    @FXML public void onNavIncidencias(ActionEvent e){ navegar("AdminIncidenciasView.fxml"); }
    @FXML public void onCerrarSesion(ActionEvent e) {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) comboEventos.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }

    private void navegar(String fxml) {
        Stage stage = (Stage) comboEventos.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }
}
