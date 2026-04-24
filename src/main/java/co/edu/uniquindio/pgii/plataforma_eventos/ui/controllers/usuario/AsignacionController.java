package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX de la pantalla de selección de zona/asiento para la compra.
 *
 * <p>Según la categoría del evento ({@code TEATRO}/{@code CONFERENCIA} vs {@code CONCIERTO}),
 * muestra uno de dos modos:</p>
 * <ul>
 *   <li><strong>Modo asientos numerados</strong>: dibuja una cuadrícula {@link GridPane} con un
 *       botón por cada {@link co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento};
 *       el color se obtiene del inventario comercial del evento
 *       ({@link Evento#obtenerAsientoEvento(String)}) — verde = DISPONIBLE, rojo = no disponible.</li>
 *   <li><strong>Modo zona libre</strong>: muestra un {@code ComboBox} numérico para elegir
 *       la cantidad de entradas (1–{@link #MAX_ENTRADAS}).</li>
 * </ul>
 * <p>Al confirmar, deposita zona, asientos y cantidad en {@link SessionManager} y navega
 * al {@code CheckoutExtrasView}.</p>
 *
 * <p>[Requerimiento: RF-003] - Implementa la selección de zona y asiento numerado por el
 * usuario, incluyendo la regla anti-reventa (máximo {@value #MAX_ENTRADAS} entradas).</p>
 * <p>[Requerimiento: RF-018] - Consulta el inventario comercial ({@code AsientoEvento})
 * del evento concreto para colorear correctamente las sillas, sin depender del estado físico
 * del recinto.</p>
 * <p>[Patrón: Strategy] - La estrategia de asignación (por zona o por asiento) se determina
 * implícitamente aquí por la categoría; la lógica formal se ejecuta en la Facade/Strategy
 * al llamar a {@code crearOrdenCompra}.</p>
 */
public class AsignacionController implements Initializable {

    @FXML
    private ComboBox<Zona> comboZonas;
    @FXML
    private VBox panelAsientos;
    @FXML
    private GridPane gridAsientos;
    @FXML
    private Label lblInfo;
    @FXML
    private Button btnContinuar;
    @FXML
    private Button btnVolver;

    @FXML
    private ComboBox<Integer> comboCantidad;
    @FXML
    private HBox panelCantidad;

    private Evento eventoActual;
    private boolean requiereAsientos;

    // Regla de negocio antirreventa
    private static final int MAX_ENTRADAS = 6;

    // Ahora rastreamos una LISTA de asientos
    private final List<Asiento> asientosSeleccionados = new ArrayList<>();
    // Y necesitamos guardar los botones para poder cambiarles el color de vuelta
    private final List<Button> botonesSeleccionados = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.eventoActual = SessionManager.getInstance().getEventoSeleccionado();
        this.requiereAsientos = (eventoActual.getCategoria() == EventoCategoria.TEATRO ||
                eventoActual.getCategoria() == EventoCategoria.CONFERENCIA);

        // Arrancamos con la canasta limpia: si venimos de otra compra previa,
        // la sesión puede traer asientos de un evento distinto.
        SessionManager.getInstance().setAsientosSeleccionados(new ArrayList<>());

        configurarComboBox();

        // Inicializar el selector numérico (1 a 6)
        comboCantidad.getItems().addAll(1, 2, 3, 4, 5, 6);
        comboCantidad.setValue(1);

        // Ocultamos ambos paneles hasta que se elija zona
        panelAsientos.setVisible(false);
        panelAsientos.setManaged(false);
        panelCantidad.setVisible(false);
        panelCantidad.setManaged(false);

        comboZonas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) procesarSeleccionZona(newVal);
                });
    }

    private void configurarComboBox() {
        // Hacemos que el ComboBox muestre el nombre de la Zona, pero guarde el objeto completo
        comboZonas.setConverter(new StringConverter<Zona>() {
            @Override
            public String toString(Zona zona) {
                return zona == null ? "" : zona.getNombre() + " ($" + zona.getPrecioBase() + ")";
            }

            @Override
            public Zona fromString(String string) {
                return null;
            }
        });

        comboZonas.getItems().addAll(eventoActual.getRecinto().getZonas());
    }

    private void procesarSeleccionZona(Zona zona) {
        // Limpiamos la canasta cada vez que cambian de zona
        asientosSeleccionados.clear();
        botonesSeleccionados.clear();

        if (!requiereAsientos) {
            panelAsientos.setVisible(false);
            panelAsientos.setManaged(false);

            panelCantidad.setVisible(true);
            panelCantidad.setManaged(true);

            lblInfo.setText("Zona seleccionada: " + zona.getNombre() + ". Seleccione la cantidad de entradas.");
        } else {
            panelCantidad.setVisible(false);
            panelCantidad.setManaged(false);

            panelAsientos.setVisible(true);
            panelAsientos.setManaged(true);
            dibujarCuadriculaAsientos(zona);
            lblInfo.setText("Sillas seleccionadas: 0 / " + MAX_ENTRADAS);
        }
    }

    private void dibujarCuadriculaAsientos(Zona zona) {
        gridAsientos.getChildren().clear();
        gridAsientos.setHgap(5);
        gridAsientos.setVgap(5);
        gridAsientos.setPadding(new Insets(10));

        int col = 0, fila = 0;

        for (Asiento asiento : zona.getAsientos()) {
            Button btnSilla = new Button(asiento.getSalida());
            btnSilla.setPrefSize(40, 40);

            // Estado consultado desde el inventario comercial del evento, no del recinto físico
            AsientoEvento ae = eventoActual.obtenerAsientoEvento(asiento.getIdAsiento());
            if (ae.getEstado() == AsientoEstado.DISPONIBLE) {
                btnSilla.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
                btnSilla.setOnAction(e -> alternarSilla(btnSilla, asiento));
            } else {
                btnSilla.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnSilla.setDisable(true);
            }

            gridAsientos.add(btnSilla, col, fila);
            col++;
            if (col == 10) {
                col = 0;
                fila++;
            }
        }
    }

    private void alternarSilla(Button btnClickeado, Asiento asiento) {
        // Si la silla YA estaba seleccionada, la deseleccionamos
        if (asientosSeleccionados.contains(asiento)) {
            asientosSeleccionados.remove(asiento);
            botonesSeleccionados.remove(btnClickeado);
            // Regresar al verde
            btnClickeado.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
        }
        // Si no estaba seleccionada, intentamos agregarla
        else {
            if (asientosSeleccionados.size() >= MAX_ENTRADAS) {
                mostrarError("No puedes comprar más de " + MAX_ENTRADAS + " entradas por transacción.");
                return;
            }
            asientosSeleccionados.add(asiento);
            botonesSeleccionados.add(btnClickeado);
            // Cambiar a azul indicando "seleccionada"
            btnClickeado.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        lblInfo.setText("Sillas seleccionadas: " + asientosSeleccionados.size() + " / " + MAX_ENTRADAS);
    }

    @FXML
    public void onContinuarClick() {
        Zona zonaElegida = comboZonas.getValue();

        if (zonaElegida == null) {
            mostrarError("Debe seleccionar una zona.");
            return;
        }

        if (requiereAsientos && asientosSeleccionados.isEmpty()) {
            mostrarError("Debe seleccionar al menos una silla.");
            return;
        }

        // 1. Guardar en sesión
        SessionManager.getInstance().setZonaSeleccionada(zonaElegida);

        if (requiereAsientos) {
            SessionManager.getInstance().setAsientosSeleccionados(new ArrayList<>(asientosSeleccionados));
            // La cantidad de asientos dicta la cantidad de entradas
            SessionManager.getInstance().setCantidadEntradas(asientosSeleccionados.size());
        } else {
            // Para concierto general, sacamos el número del ComboBox y nos aseguramos
            // de limpiar cualquier asiento que hubiera quedado en sesión.
            SessionManager.getInstance().setAsientosSeleccionados(new ArrayList<>());
            SessionManager.getInstance().setCantidadEntradas(comboCantidad.getValue());
        }

        // 2. Navegar
        Stage stage = (Stage) btnContinuar.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("CheckoutExtrasView.fxml", stage);
    }

    @FXML
    public void onVolverClick() {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("DetalleEventoView.fxml", stage);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Selección Incompleta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
