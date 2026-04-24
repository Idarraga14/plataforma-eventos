package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacade;
import co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin.AdministracionFacadeImpl;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ExportadorCSVAdminAdapter;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ExportadorPDFAdminAdapter;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ExportadorReporteAdmin;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ReporteOperativo;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.SessionManager;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX del módulo de reportes operativos administrativos.
 *
 * <p>Permite al administrador definir un rango de fechas, consultar las métricas del período
 * (ventas totales, número de compras, cancelaciones, tasa de cancelación, ingresos por cada
 * servicio adicional, top de eventos por facturación) y exportar el reporte en PDF o CSV.</p>
 *
 * <p>La consulta delega a {@link AdministracionFacade#generarReporteOperativo}, que construye
 * un {@link ReporteOperativo} DTO. La exportación instancia directamente el adaptador
 * concreto ({@code ExportadorPDFAdminAdapter} o {@code ExportadorCSVAdminAdapter}) y
 * abre un {@code FileChooser} para guardar el archivo.</p>
 *
 * <p>[Requerimiento: RF-046] - Implementa la generación y exportación del reporte operativo
 * del administrador, con métricas de ventas, servicios adicionales y top eventos.</p>
 * <p>[Patrón: Adapter] - Al exportar, crea el {@code ExportadorReporteAdmin} concreto
 * (PDF o CSV) e invoca {@code exportar(ReporteOperativo)}, siguiendo el Patrón Adapter
 * (Target → Adapter → serialización sin biblioteca externa).</p>
 * <p>[Patrón: Facade] - La generación del reporte se delega a {@link AdministracionFacade},
 * que agrega métricas del repositorio central sin exponer la lógica de cálculo a la UI.</p>
 */
public class AdminReportesController implements Initializable {

    private final AdministracionFacade administracionFacade = new AdministracionFacadeImpl();

    @FXML
    private DatePicker dtpDesde;
    @FXML
    private DatePicker dtpHasta;
    @FXML
    private Button btnConsultar;
    @FXML
    private Label lblMensaje;

    @FXML
    private Label lblTotalVentas;
    @FXML
    private Label lblTotalCompras;
    @FXML
    private Label lblCanceladas;
    @FXML
    private Label lblTasaCancelacion;

    @FXML
    private Label lblExtraVIP;
    @FXML
    private Label lblExtraSeguro;
    @FXML
    private Label lblExtraParq;
    @FXML
    private Label lblExtraMerch;
    @FXML
    private Label lblExtraAcceso;

    @FXML
    private Label lblTopEventos;

    @FXML
    private Button btnExportarCSV;
    private ReporteOperativo reporteActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dtpDesde.setValue(LocalDate.now().withDayOfMonth(1));
        dtpHasta.setValue(LocalDate.now());
    }

    @FXML
    public void onConsultarClick() {
        LocalDate desde = dtpDesde.getValue();
        LocalDate hasta = dtpHasta.getValue();
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            mostrarMensaje("La fecha de inicio debe ser anterior o igual a la fecha fin.", true);
            return;
        }
        try {
            reporteActual = administracionFacade.generarReporteOperativo(desde, hasta);
            poblarVista(reporteActual);
            mostrarMensaje("Métricas calculadas correctamente.", false);
        } catch (RuntimeException ex) {
            mostrarMensaje("Error al generar reporte: " + ex.getMessage(), true);
        }
    }

    @FXML
    public void onLimpiarClick() {
        dtpDesde.setValue(LocalDate.now().withDayOfMonth(1));
        dtpHasta.setValue(LocalDate.now());
        reporteActual = null;
        resetLabels();
        lblMensaje.setVisible(false);
        lblMensaje.setManaged(false);
    }

    @FXML
    public void onExportarCSVClick() {
        exportar(new ExportadorCSVAdminAdapter());
    }

    @FXML
    public void onExportarPDFClick() {
        exportar(new ExportadorPDFAdminAdapter());
    }

    private void exportar(ExportadorReporteAdmin exportador) {
        if (reporteActual == null) {
            mostrarMensaje("Primero consulta las métricas antes de exportar.", true);
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar Reporte Operativo");
        chooser.setInitialFileName("reporte_operativo_" + reporteActual.getDesde()
                + "_" + reporteActual.getHasta() + "." + exportador.getExtension());
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                exportador.getDescripcionFormato(), "*." + exportador.getExtension()));
        Stage stage = (Stage) btnExportarCSV.getScene().getWindow();
        File archivo = chooser.showSaveDialog(stage);
        if (archivo == null) return;
        try {
            byte[] contenido = exportador.exportar(reporteActual);
            Files.write(archivo.toPath(), contenido);
            mostrarInfo("Reporte exportado en:\n" + archivo.getAbsolutePath());
        } catch (IOException ex) {
            mostrarError("Error al guardar el archivo: " + ex.getMessage());
        }
    }

    private void poblarVista(ReporteOperativo r) {
        lblTotalVentas.setText(String.format("$ %,.0f", r.getTotalVentas()));
        lblTotalCompras.setText(String.valueOf(r.getTotalCompras()));
        lblCanceladas.setText(String.valueOf(r.getComprasCanceladas()));
        lblTasaCancelacion.setText(String.format("%.1f %%", r.getTasaCancelacion()));

        Map<String, Double> extras = r.getIngresosPorExtra();
        lblExtraVIP.setText(formatMoney(extras.getOrDefault("VIP", 0.0)));
        lblExtraSeguro.setText(formatMoney(extras.getOrDefault("Seguro", 0.0)));
        lblExtraParq.setText(formatMoney(extras.getOrDefault("Parqueadero", 0.0)));
        lblExtraMerch.setText(formatMoney(extras.getOrDefault("Merchandising", 0.0)));
        lblExtraAcceso.setText(formatMoney(extras.getOrDefault("Acceso Preferencial", 0.0)));

        if (r.getTopEventos().isEmpty()) {
            lblTopEventos.setText("Sin ventas en el período.");
        } else {
            StringBuilder sb = new StringBuilder();
            int pos = 1;
            for (Map.Entry<String, Double> entry : r.getTopEventos().entrySet()) {
                sb.append(pos++).append(". ").append(entry.getKey())
                        .append("  →  ").append(formatMoney(entry.getValue())).append('\n');
            }
            lblTopEventos.setText(sb.toString().trim());
        }
    }

    private void resetLabels() {
        lblTotalVentas.setText("—");
        lblTotalCompras.setText("—");
        lblCanceladas.setText("—");
        lblTasaCancelacion.setText("—");
        lblExtraVIP.setText("—");
        lblExtraSeguro.setText("—");
        lblExtraParq.setText("—");
        lblExtraMerch.setText("—");
        lblExtraAcceso.setText("—");
        lblTopEventos.setText("—");
    }

    private String formatMoney(double v) {
        return String.format("$ %,.0f", v);
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
    }

    @FXML
    public void onNavIncidencias() {
        navegar("AdminIncidenciasView.fxml");
    }

    @FXML
    public void onCerrarSesion() {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) btnConsultar.getScene().getWindow();
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", stage);
    }

    private void navegar(String fxml) {
        Stage stage = (Stage) btnConsultar.getScene().getWindow();
        ViewNavigator.cargarVistaAdmin(fxml, stage);
    }

    private void mostrarMensaje(String msg, boolean esError) {
        lblMensaje.setText(msg);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
        lblMensaje.setVisible(true);
        lblMensaje.setManaged(true);
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
