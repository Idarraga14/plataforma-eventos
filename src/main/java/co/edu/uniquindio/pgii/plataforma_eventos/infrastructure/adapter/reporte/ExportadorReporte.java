package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Interfaz objetivo (Target) del patrón Adapter para la generación de comprobantes de compra.
 *
 * <p>Define el contrato unificado que la capa de aplicación y la UI usan para exportar
 * comprobantes en distintos formatos, sin conocer los detalles de generación de PDF o CSV.
 * Cada implementación ({@link ExportadorCSVAdapter}, {@link ExportadorPDFAdapter}) encapsula
 * la lógica de serialización de una {@link co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra}
 * en el formato correspondiente.</p>
 *
 * <p>[Requerimiento: RF-009] - Permite al usuario exportar el comprobante de su compra
 * en PDF o CSV desde la vista de historial de compras.</p>
 * <p>[Patrón: Adapter] - Actúa como la interfaz <strong>Target</strong> para comprobantes
 * de compra individual. La interfaz paralela para reportes operativos es
 * {@link ExportadorReporteAdmin}.</p>
 */
public interface ExportadorReporte {

    /**
     * Genera el comprobante de la compra indicada en el formato implementado.
     *
     * @param compra la compra para la cual se genera el comprobante
     * @return arreglo de bytes con el contenido del archivo generado
     */
    byte[] exportar(Compra compra);

    /**
     * Devuelve la extensión de archivo correspondiente al formato (sin punto).
     * Usada para construir el nombre del archivo al guardar.
     *
     * @return extensión de archivo (ej. {@code "pdf"}, {@code "csv"})
     */
    String getExtension();

    /**
     * Devuelve una descripción legible del formato para mostrarse en el {@code FileChooser}.
     *
     * @return descripción del formato (ej. {@code "Documento PDF"}, {@code "Archivo CSV"})
     */
    String getDescripcionFormato();
}
