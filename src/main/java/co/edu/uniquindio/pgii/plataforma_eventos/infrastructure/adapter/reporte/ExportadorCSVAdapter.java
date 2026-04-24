package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador concreto que serializa un comprobante de {@link Compra} en formato CSV.
 *
 * <p>Genera un archivo de dos secciones: encabezado con los datos generales de la compra
 * (ID, usuario, evento, fecha, estado, total) y un listado de entradas con su descripción
 * de servicios (incluyendo los decoradores aplicados) y precio unitario.
 * Los valores con comas o comillas son escapados correctamente según el estándar RFC 4180.</p>
 *
 * <p>[Requerimiento: RF-009] - Implementa la exportación del comprobante de compra
 * en formato CSV, seleccionado cuando el usuario elige {@link FormatoReporte#CSV}.</p>
 * <p>[Patrón: Adapter] - Actúa como <strong>Adapter Concreto</strong>; implementa
 * {@link ExportadorReporte} (Target) adaptando la serialización a formato texto plano CSV.</p>
 * <p>[Patrón: Decorator] - Invoca {@code entrada.getDescripcionServicios()} y
 * {@code entrada.getPrecioTotal()}, que recorren la cadena de decoradores transparentemente.</p>
 */
public class ExportadorCSVAdapter implements ExportadorReporte {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Serializa la compra y todas sus entradas (con decoradores) en bytes CSV codificados en UTF-8.
     *
     * @param compra la compra para la que se genera el comprobante
     * @return bytes del archivo CSV en codificación UTF-8
     */
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
