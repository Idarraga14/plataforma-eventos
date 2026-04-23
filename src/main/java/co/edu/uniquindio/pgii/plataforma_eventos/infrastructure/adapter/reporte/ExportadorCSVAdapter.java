package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

public class ExportadorCSVAdapter implements ExportadorReporte {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public byte[] exportar(Compra compra) {
        StringBuilder sb = new StringBuilder();
        sb.append("Campo,Valor\n");
        sb.append("Id Compra,").append(csv(compra.getIdCompra())).append('\n');
        sb.append("Usuario,").append(csv(compra.getUsuario().getNombreCompleto())).append('\n');
        sb.append("Correo,").append(csv(compra.getUsuario().getCorreo())).append('\n');
        sb.append("Evento,").append(csv(compra.getEvento().getNombre())).append('\n');
        sb.append("Fecha,").append(compra.getFecha().format(FMT)).append('\n');
        sb.append("Estado,").append(compra.getEstadoEnum().name()).append('\n');
        sb.append("Total,").append(String.format("%.2f", compra.getTotal())).append('\n');
        sb.append('\n');
        sb.append("# Entrada,Descripción,Precio\n");
        int i = 1;
        for (Entrada e : compra.getEntradas()) {
            sb.append(i++).append(',')
              .append(csv(e.getDescripcionServicios())).append(',')
              .append(String.format("%.2f", e.getPrecioTotal()))
              .append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getExtension() {
        return "csv";
    }

    @Override
    public String getDescripcionFormato() {
        return "Archivo CSV";
    }

    private String csv(String v) {
        if (v == null) return "";
        boolean needsQuote = v.contains(",") || v.contains("\"") || v.contains("\n");
        String safe = v.replace("\"", "\"\"");
        return needsQuote ? "\"" + safe + "\"" : safe;
    }
}
