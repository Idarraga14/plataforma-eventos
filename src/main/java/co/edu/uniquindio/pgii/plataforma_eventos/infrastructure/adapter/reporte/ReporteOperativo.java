package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO (Data Transfer Object) que encapsula todas las métricas de un reporte operativo
 * para un rango de fechas determinado.
 *
 * <p>Es producido exclusivamente por {@code AdministracionFacadeImpl#generarReporteOperativo}
 * y consumido por los adaptadores de exportación ({@link ExportadorCSVAdminAdapter},
 * {@link ExportadorPDFAdminAdapter}) y por el controlador {@code AdminReportesController}
 * para poblar la vista. Ninguno de estos consumidores conoce la fuente de datos original.</p>
 *
 * <p>[Requerimiento: RF-046] - Contiene las métricas exigidas por el reporte operativo:
 * total de ventas, número de compras, tasa de cancelación, ingresos por servicio adicional
 * y top de eventos por facturación.</p>
 * <p>[Patrón: Adapter] - Actúa como el objeto de transferencia entre la lógica de negocio
 * y los adaptadores de exportación, evitando que éstos accedan directamente al repositorio.</p>
 */
public class ReporteOperativo {

    /** Fecha de inicio del período analizado. */
    private final LocalDate desde;

    /** Fecha de fin del período analizado. */
    private final LocalDate hasta;

    /** Suma de ingresos de compras en estado PAGADA o CONFIRMADA en el período. */
    private final double totalVentas;

    /** Número total de compras registradas en el período (todos los estados). */
    private final int totalCompras;

    /** Número de compras canceladas o reembolsadas en el período. */
    private final int comprasCanceladas;

    /** Porcentaje de compras canceladas o reembolsadas sobre el total del período. */
    private final double tasaCancelacion;

    /** Mapa nombre-extra → ingresos totales generados por ese servicio adicional en el período. */
    private final Map<String, Double> ingresosPorExtra;

    /** Mapa nombre-evento → ingresos totales, ordenado descendentemente por valor. */
    private final Map<String, Double> topEventos;

    public ReporteOperativo(LocalDate desde, LocalDate hasta,
                            double totalVentas, int totalCompras,
                            int comprasCanceladas, double tasaCancelacion,
                            Map<String, Double> ingresosPorExtra,
                            Map<String, Double> topEventos) {
        this.desde = desde;
        this.hasta = hasta;
        this.totalVentas = totalVentas;
        this.totalCompras = totalCompras;
        this.comprasCanceladas = comprasCanceladas;
        this.tasaCancelacion = tasaCancelacion;
        this.ingresosPorExtra = ingresosPorExtra;
        this.topEventos = topEventos;
    }

    public LocalDate getDesde()               { return desde; }
    public LocalDate getHasta()               { return hasta; }
    public double getTotalVentas()            { return totalVentas; }
    public int getTotalCompras()              { return totalCompras; }
    public int getComprasCanceladas()         { return comprasCanceladas; }
    public double getTasaCancelacion()        { return tasaCancelacion; }
    public Map<String, Double> getIngresosPorExtra() { return ingresosPorExtra; }
    public Map<String, Double> getTopEventos()       { return topEventos; }
}
