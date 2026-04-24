package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.application.observer.EventoObserver;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX del panel de control administrativo (Dashboard).
 *
 * <p>Muestra métricas globales en tiempo real: eventos activos, ventas del mes, total de
 * usuarios e incidencias abiertas; complementadas por tres gráficos (líneas de ingresos por
 * mes, pastel de ocupación por zona del primer evento publicado, barras de ingresos por
 * servicio adicional). Se auto-suscribe al {@code PlataformaEventosSingleton} como
 * {@link EventoObserver} para recibir notificaciones de cambios de aforo y refrescar
 * el dashboard automáticamente desde el hilo de JavaFX ({@link Platform#runLater}).</p>
 *
 * <p>[Requerimiento: RF-012] - Brinda al administrador una vista consolidada del estado
 * operativo del sistema con métricas y gráficos de resumen.</p>
 * <p>[Requerimiento: RF-016] - Muestra el total de ventas del período actual y los
 * ingresos por servicio adicional requeridos por el reporte operativo.</p>
 * <p>[Patrón: Observer] - Implementa {@link EventoObserver} (rol: <strong>Concrete Observer</strong>);
 * {@code onAforoActualizado} se invoca por el Sujeto al modificar inventario de asientos,
 * y usa {@code Platform.runLater} para garantizar la actualización en el hilo de UI.</p>
 * <p>[Patrón: Facade] - Delega todas las consultas de métricas y gráficos a
 * {@link AdministracionFacade} sin acceder directamente al repositorio.</p>
 */
public class AdminDashboardController implements Initializable, EventoObserver {

    private final AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML
    private Label lblEventosActivos;
    @FXML
    private Label lblVentasMes;
    @FXML
    private Label lblTotalUsuarios;
    @FXML
    private Label lblIncidenciasAbiertas;

    @FXML
    private LineChart<String, Number> lineChartVentas;
    @FXML
    private PieChart pieChartOcupacion;
    @FXML
    private BarChart<String, Number> barChartServicios;

    @FXML
    private Label lblUltimaActualizacion;
    @FXML
    private Button btnRefrescar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        administracionFacade.registrarObserver(this);
        cargarDashboard();
    }

    @Override
    public void onAforoActualizado(Evento evento) {
        Platform.runLater(this::cargarDashboard);
    }

    @FXML
    public void onRefrescarClick() {
        cargarDashboard();
    }

    // --- Navegación ---
    @FXML
    public void onNavDashboard() {
    }

    @FXML
    public void onNavEventos() {
        navegar("AdminEventosView.fxml");
    }

    @FXML
    public void onNavRecintos() {
        navegar("AdminRecintosView.fxml");
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
        co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager.getInstance().logout();
        navegarUsuario();
    }

    private void navegar(String fxml) {
        Stage stage = (Stage) lblEventosActivos.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }

    private void navegarUsuario() {
        Stage stage = (Stage) lblEventosActivos.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }

    // --- Carga de métricas ---

    private void cargarDashboard() {
        lblEventosActivos.setText(String.valueOf(administracionFacade.contarEventosPublicados()));
        lblTotalUsuarios.setText(String.valueOf(administracionFacade.contarUsuarios()));
        lblIncidenciasAbiertas.setText(String.valueOf(administracionFacade.contarIncidenciasAbiertas()));

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        double ventasMes = administracionFacade.totalVentasPeriodo(inicioMes, hoy);
        lblVentasMes.setText(String.format("$ %,.0f", ventasMes));

        // LineChart: ingresos por mes
        lineChartVentas.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ingresos");
        for (Map.Entry<String, Double> e : administracionFacade.ingresosPorMes().entrySet()) {
            serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        lineChartVentas.getData().add(serie);

        // PieChart: ocupación del primer evento publicado (si existe).
        // Construimos la lista completa y luego setData para forzar repintado correcto.
        javafx.collections.ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        administracionFacade.listarEventos().stream()
                .filter(ev -> ev.getEstado() == co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado.PUBLICADO)
                .findFirst()
                .ifPresent(ev -> {
                    Map<String, Double> ocu = administracionFacade.ocupacionPorZona(ev.getIdEvento());
                    for (Map.Entry<String, Double> en : ocu.entrySet()) {
                        pieData.add(new PieChart.Data(
                                en.getKey() + String.format(" (%.1f%%)", en.getValue()),
                                Math.max(en.getValue(), 0.01)));
                    }
                });
        pieChartOcupacion.setData(pieData);

        // BarChart: servicios
        barChartServicios.getData().clear();
        XYChart.Series<String, Number> serieServ = new XYChart.Series<>();
        serieServ.setName("Ingresos por servicio");
        for (Map.Entry<String, Double> e : administracionFacade.ingresosPorServicioAdicional().entrySet()) {
            serieServ.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        barChartServicios.getData().add(serieServ);

        String ahora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        lblUltimaActualizacion.setText("Última actualización: " + ahora);
    }
}
