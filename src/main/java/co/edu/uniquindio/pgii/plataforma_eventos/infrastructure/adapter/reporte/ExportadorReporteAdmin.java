package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

/**
 * Interfaz objetivo (Target) del patrón Adapter para la generación de reportes operativos
 * administrativos en distintos formatos de exportación.
 *
 * <p>Define el contrato paralelo a {@link ExportadorReporte} pero orientado al módulo de
 * administración: recibe un {@link ReporteOperativo} (DTO con métricas agregadas) en lugar
 * de una {@code Compra} individual. Las implementaciones concretas son
 * {@link ExportadorCSVAdminAdapter} y {@link ExportadorPDFAdminAdapter}.</p>
 *
 * <p>[Requerimiento: RF-046] - Permite al administrador exportar el reporte operativo
 * (ventas, cancelaciones, extras, top eventos) en PDF o CSV desde el módulo de reportes.</p>
 * <p>[Patrón: Adapter] - Actúa como la interfaz <strong>Target</strong> para reportes admin.
 * Mantiene la misma estructura que {@link ExportadorReporte} para simetría arquitectónica,
 * pero opera sobre {@link ReporteOperativo} en lugar de {@code Compra}.</p>
 */
public interface ExportadorReporteAdmin {

    /**
     * Genera el reporte operativo en el formato implementado.
     *
     * @param reporte DTO con las métricas del período analizado
     * @return arreglo de bytes con el contenido del archivo generado
     */
    byte[] exportar(ReporteOperativo reporte);

    /**
     * Devuelve la extensión de archivo correspondiente al formato (sin punto).
     *
     * @return extensión de archivo (ej. {@code "pdf"}, {@code "csv"})
     */
    String getExtension();

    /**
     * Devuelve una descripción legible del formato para el {@code FileChooser}.
     *
     * @return descripción del formato (ej. {@code "Documento PDF"}, {@code "Archivo CSV"})
     */
    String getDescripcionFormato();
}
