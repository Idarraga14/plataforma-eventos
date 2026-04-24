package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adaptador concreto que serializa un {@link ReporteOperativo} administrativo en formato PDF 1.4.
 *
 * <p>Genera un documento PDF de una página (fuente Helvetica 11pt) sin dependencias externas,
 * con la misma técnica de construcción manual que {@code ExportadorPDFAdapter}. El documento
 * incluye: período del reporte, métricas generales (ventas, compras, tasa de cancelación),
 * sección de ingresos por servicio adicional y ranking de top eventos por facturación.</p>
 *
 * <p>[Requerimiento: RF-046] - Implementa la exportación del reporte operativo en PDF,
 * seleccionado cuando el administrador elige {@link FormatoReporte#PDF} en el módulo de reportes.</p>
 * <p>[Patrón: Adapter] - Actúa como <strong>Adapter Concreto</strong>; implementa
 * {@link ExportadorReporteAdmin} (Target) para el módulo de reportes administrativos.
 * Complementa a {@code ExportadorPDFAdapter} con la misma técnica pero sobre el DTO
 * {@link ReporteOperativo} en lugar de sobre una {@code Compra} individual.</p>
 */
public class ExportadorPDFAdminAdapter implements ExportadorReporteAdmin {

    @Override
    public byte[] exportar(ReporteOperativo r) {
        List<String> lineas = construirLineas(r);
        String contenido = construirStreamPDF(lineas);
        byte[] stream = contenido.getBytes(StandardCharsets.ISO_8859_1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        escribir(out, "%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n");

        offsets.add(out.size());
        escribir(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets.add(out.size());
        escribir(out, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        offsets.add(out.size());
        escribir(out, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                + "/Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>\nendobj\n");

        offsets.add(out.size());
        escribir(out, "4 0 obj\n<< /Length " + stream.length + " >>\nstream\n");
        escribirBytes(out, stream);
        escribir(out, "\nendstream\nendobj\n");

        offsets.add(out.size());
        escribir(out, "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

        int xrefOffset = out.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(offsets.size() + 1).append('\n');
        xref.append("0000000000 65535 f \n");
        for (int off : offsets) xref.append(String.format("%010d 00000 n \n", off));
        xref.append("trailer\n<< /Size ").append(offsets.size() + 1).append(" /Root 1 0 R >>\n");
        xref.append("startxref\n").append(xrefOffset).append("\n%%EOF\n");
        escribir(out, xref.toString());

        return out.toByteArray();
    }

    @Override
    public String getExtension() { return "pdf"; }

    @Override
    public String getDescripcionFormato() { return "Documento PDF"; }

    private List<String> construirLineas(ReporteOperativo r) {
        List<String> l = new ArrayList<>();
        l.add("REPORTE OPERATIVO ADMINISTRATIVO");
        l.add("");
        l.add("Periodo: " + r.getDesde() + " a " + r.getHasta());
        l.add("");
        l.add(String.format("Total Ventas ($):            %,.0f", r.getTotalVentas()));
        l.add("Total Compras:               " + r.getTotalCompras());
        l.add("Compras Canceladas/Reemb.:   " + r.getComprasCanceladas());
        l.add(String.format("Tasa de Cancelacion (%%):     %.1f", r.getTasaCancelacion()));
        l.add("");
        l.add("--- Ingresos por Servicio Adicional ---");
        for (Map.Entry<String, Double> e : r.getIngresosPorExtra().entrySet()) {
            l.add(String.format("  %-22s $%,.0f", e.getKey() + ":", e.getValue()));
        }
        l.add("");
        l.add("--- Top Eventos por Ventas ---");
        int pos = 1;
        for (Map.Entry<String, Double> e : r.getTopEventos().entrySet()) {
            l.add(String.format("  %d. %-20s $%,.0f", pos++, e.getKey(), e.getValue()));
        }
        return l;
    }

    private String construirStreamPDF(List<String> lineas) {
        StringBuilder sb = new StringBuilder();
        sb.append("BT\n/F1 11 Tf\n13 TL\n50 750 Td\n");
        boolean primera = true;
        for (String linea : lineas) {
            if (!primera) sb.append("T*\n");
            sb.append("(").append(escapePdf(linea)).append(") Tj\n");
            primera = false;
        }
        sb.append("ET");
        return sb.toString();
    }

    private String escapePdf(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '\\' || c == '(' || c == ')') sb.append('\\').append(c);
            else if (c > 126) sb.append('?');
            else sb.append(c);
        }
        return sb.toString();
    }

    private void escribir(ByteArrayOutputStream out, String s) {
        escribirBytes(out, s.getBytes(StandardCharsets.ISO_8859_1));
    }

    private void escribirBytes(ByteArrayOutputStream out, byte[] bytes) {
        out.write(bytes, 0, bytes.length);
    }
}
