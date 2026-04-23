package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO que encapsula todas las métricas de un reporte operativo con rango de fechas.
 * Lo consumen los adaptadores de exportación sin conocer la fuente de datos.
 */
public class ReporteOperativo {

    private final LocalDate desde;
    private final LocalDate hasta;
    private final double totalVentas;
    private final int totalCompras;
    private final int comprasCanceladas;
    private final double tasaCancelacion;
    private final Map<String, Double> ingresosPorExtra;   // nombre extra → total $
    private final Map<String, Double> topEventos;          // nombre evento → total $ (ordenado desc)

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
