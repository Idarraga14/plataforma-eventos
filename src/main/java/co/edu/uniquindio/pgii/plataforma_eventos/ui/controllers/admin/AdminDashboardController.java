package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.observer.EventoObserver;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controlador del panel de administración.
 * Implementa EventoObserver para recibir notificaciones de cambios de aforo en tiempo real.
 * Registro: administracionFacade.registrarObserver(this) en initialize().
 */
public class AdminDashboardController implements Initializable, EventoObserver {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // Admin actual: SessionManager.getInstance().getUsuarioActual()
    private AdministracionFacade administracionFacade;

    // --- COMPONENTES FXML ---
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
        // TODO: Registrar este controlador como observer del sistema de eventos
        //       administracionFacade.registrarObserver(this);

        // TODO: Cargar métricas iniciales llamando a cargarDashboard()
        cargarDashboard();
    }

    /** Inyecta la fachada de administración. */
    public void setAdministracionFacade(AdministracionFacade administracionFacade) {
        this.administracionFacade = administracionFacade;
    }

    // --- OBSERVER ---

    @Override
    public void onAforoActualizado(Evento evento) {
        // TODO: Refrescar pieChartOcupacion y las tarjetas de resumen
        //       cuando el aforo de un evento cambie en tiempo real.
        cargarDashboard();
    }

    // --- ACCIONES ---

    @FXML
    public void onRefrescarClick(ActionEvent event) {
        cargarDashboard();
    }

    // --- MÉTODOS PRIVADOS ---

    private void cargarDashboard() {
        // TODO: Consultar métricas desde administracionFacade y poblar:
        //   - lblEventosActivos con el conteo de eventos en estado PUBLICADO
        //   - lblVentasMes con el total de ventas del mes en curso
        //   - lblTotalUsuarios con el total de usuarios registrados
        //   - lblIncidenciasAbiertas con incidencias sin resolver

        // TODO: Poblar lineChartVentas con datos de ventas agrupados por período
        // TODO: Poblar pieChartOcupacion con % de ocupación por zona
        // TODO: Poblar barChartServicios con ingresos por tipo de servicio adicional

        String ahora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        lblUltimaActualizacion.setText("Última actualización: " + ahora);
    }
}
