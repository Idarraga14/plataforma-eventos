package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.application.observer.EventoObserver;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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

public class AdminDashboardController implements Initializable, EventoObserver {

    private AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML private Label    lblEventosActivos;
    @FXML private Label    lblVentasMes;
    @FXML private Label    lblTotalUsuarios;
    @FXML private Label    lblIncidenciasAbiertas;

    @FXML private LineChart<String, Number> lineChartVentas;
    @FXML private PieChart                  pieChartOcupacion;
    @FXML private BarChart<String, Number>  barChartServicios;

    @FXML private Label  lblUltimaActualizacion;
    @FXML private Button btnRefrescar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        administracionFacade.registrarObserver(this);
        cargarDashboard();
    }

    public void setAdministracionFacade(AdministracionFacade administracionFacade) {
        this.administracionFacade = administracionFacade;
    }

    @Override
    public void onAforoActualizado(Evento evento) {
        Platform.runLater(this::cargarDashboard);
    }

    @FXML
    public void onRefrescarClick(ActionEvent event) {
        cargarDashboard();
    }

    // --- Navegación ---
    @FXML public void onNavDashboard(ActionEvent e) { }
    @FXML public void onNavEventos(ActionEvent e) { navegar("AdminEventosView.fxml"); }
    @FXML public void onNavRecintos(ActionEvent e) { navegar("AdminRecintosView.fxml"); }
    @FXML public void onNavUsuarios(ActionEvent e) { navegar("AdminUsuariosView.fxml"); }
    @FXML public void onNavCompras(ActionEvent e) { navegar("AdminComprasView.fxml"); }
    @FXML public void onNavAsientos(ActionEvent e) { navegar("AdminGestorAsientosView.fxml"); }
    @FXML public void onNavReportes(ActionEvent e) { navegar("AdminReportesView.fxml"); }
    @FXML public void onNavIncidencias(ActionEvent e) { navegar("AdminIncidenciasView.fxml"); }
    @FXML public void onCerrarSesion(ActionEvent e) {
        co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager.getInstance().logout();
        navegarUsuario("LoginView.fxml");
    }

    private void navegar(String fxml) {
        Stage stage = (Stage) lblEventosActivos.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }

    private void navegarUsuario(String fxml) {
        Stage stage = (Stage) lblEventosActivos.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario(fxml, stage);
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
