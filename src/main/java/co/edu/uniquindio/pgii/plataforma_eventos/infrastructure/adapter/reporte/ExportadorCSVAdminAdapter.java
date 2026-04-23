package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ExportadorCSVAdminAdapter implements ExportadorReporteAdmin {

    @Override
    public byte[] exportar(ReporteOperativo r) {
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE OPERATIVO ADMINISTRATIVO\n\n");
        sb.append("Período Desde,").append(r.getDesde()).append('\n');
        sb.append("Período Hasta,").append(r.getHasta()).append('\n');
        sb.append("Total Ventas ($),").append(String.format("%.2f", r.getTotalVentas())).append('\n');
        sb.append("Total Compras,").append(r.getTotalCompras()).append('\n');
        sb.append("Compras Canceladas/Reembolsadas,").append(r.getComprasCanceladas()).append('\n');
        sb.append("Tasa de Cancelación (%),").append(String.format("%.1f", r.getTasaCancelacion())).append('\n');
        sb.append('\n');

        sb.append("INGRESOS POR SERVICIO ADICIONAL (EXTRAS)\n");
        sb.append("Servicio,Ingresos ($)\n");
        for (Map.Entry<String, Double> e : r.getIngresosPorExtra().entrySet()) {
            sb.append(csv(e.getKey())).append(',')
              .append(String.format("%.2f", e.getValue())).append('\n');
        }
        sb.append('\n');

        sb.append("TOP EVENTOS POR VENTAS\n");
        sb.append("Evento,Ventas ($)\n");
        for (Map.Entry<String, Double> e : r.getTopEventos().entrySet()) {
            sb.append(csv(e.getKey())).append(',')
              .append(String.format("%.2f", e.getValue())).append('\n');
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getExtension() { return "csv"; }

    @Override
    public String getDescripcionFormato() { return "Archivo CSV"; }

    private String csv(String v) {
        if (v == null) return "";
        boolean needsQuote = v.contains(",") || v.contains("\"") || v.contains("\n");
        return needsQuote ? "\"" + v.replace("\"", "\"\"") + "\"" : v;
    }
}
