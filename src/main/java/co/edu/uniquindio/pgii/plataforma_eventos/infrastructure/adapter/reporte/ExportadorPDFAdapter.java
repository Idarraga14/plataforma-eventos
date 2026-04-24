package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador concreto que serializa un comprobante de {@link Compra} en formato PDF 1.4.
 *
 * <p>Genera un documento PDF válido de una sola página (fuente Helvetica, tamaño 12pt)
 * <strong>sin ninguna dependencia externa</strong>, construyendo manualmente la estructura
 * de objetos PDF (Catalog, Pages, Page, ContentStream, Font) y su tabla xref. Es la única
 * implementación de PDF del sistema y cumple el estándar ISO 32000 en su variante mínima.</p>
 *
 * <p>El documento incluye: ID de compra, fecha, estado, datos del comprador, nombre del evento
 * con ciudad, listado de entradas con descripción completa de servicios (cadena de decoradores)
 * y el total de la compra.</p>
 *
 * <p>[Requerimiento: RF-009] - Implementa la exportación del comprobante en formato PDF,
 * seleccionado cuando el usuario elige {@link FormatoReporte#PDF}.</p>
 * <p>[Patrón: Adapter] - Actúa como <strong>Adapter Concreto</strong>; implementa
 * {@link ExportadorReporte} (Target) adaptando la serialización al formato binario PDF.</p>
 * <p>[Patrón: Decorator] - Invoca {@code entrada.getDescripcionServicios()} que recorre
 * la cadena de decoradores para incluir todos los servicios adicionales en el comprobante.</p>
 */
public class ExportadorPDFAdapter implements ExportadorReporte {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public byte[] exportar(Compra compra) {
        List<String> lineas = construirLineas(compra);
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
        for (int off : offsets) {
            xref.append(String.format("%010d 00000 n \n", off));
        }
        xref.append("trailer\n<< /Size ").append(offsets.size() + 1).append(" /Root 1 0 R >>\n");
        xref.append("startxref\n").append(xrefOffset).append("\n%%EOF\n");
        escribir(out, xref.toString());

        return out.toByteArray();
    }

    @Override
    public String getExtension() {
        return "pdf";
    }

    @Override
    public String getDescripcionFormato() {
        return "Documento PDF";
    }

    // --- Helpers ---

    private List<String> construirLineas(Compra c) {
        List<String> l = new ArrayList<>();
        l.add("COMPROBANTE DE COMPRA");
        l.add("");
        l.add("Id Compra: " + c.getIdCompra());
        l.add("Fecha:     " + c.getFecha().format(FMT));
        l.add("Estado:    " + c.getEstadoEnum().name());
        l.add("");
        l.add("Usuario:   " + c.getUsuario().getNombreCompleto());
        l.add("Correo:    " + c.getUsuario().getCorreo());
        l.add("");
        l.add("Evento:    " + c.getEvento().getNombre());
        l.add("Ciudad:    " + c.getEvento().getCiudad());
        l.add("");
        l.add("Entradas:");
        int i = 1;
        for (Entrada e : c.getEntradas()) {
            l.add(String.format("  %d. %s  -  $%,.0f",
                    i++, e.getDescripcionServicios(), e.getPrecioTotal()));
        }
        l.add("");
        l.add(String.format("TOTAL: $%,.0f", c.getTotal()));
        return l;
    }

    private String construirStreamPDF(List<String> lineas) {
        StringBuilder sb = new StringBuilder();
        sb.append("BT\n/F1 12 Tf\n14 TL\n60 740 Td\n");
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
