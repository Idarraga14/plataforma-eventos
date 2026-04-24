package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

/**
 * Enumeración de los formatos de exportación disponibles para comprobantes y reportes.
 *
 * <p>Usada por la UI para indicar a la capa de aplicación qué implementación de
 * {@link ExportadorReporte} o {@link ExportadorReporteAdmin} debe seleccionarse.
 * El switch dentro de la facade ({@code PlataformaFacadeImpl#generarComprobante} y
 * {@code AdminReportesController}) resuelve el adaptador concreto según este valor.</p>
 *
 * <p>[Requerimiento: RF-009] - El usuario elige el formato al exportar su comprobante
 * desde la vista de historial de compras.</p>
 * <p>[Requerimiento: RF-046] - El administrador elige el formato al exportar el reporte
 * operativo desde el módulo de reportes.</p>
 * <p>[Patrón: Adapter] - Actúa como selector del Adapter concreto a instanciar;
 * desacopla a la UI de los tipos concretos de exportador.</p>
 */
public enum FormatoReporte {

    /** Exportación como documento PDF 1.4 (generado sin dependencias externas). */
    PDF,

    /** Exportación como archivo CSV (valores separados por coma, codificación UTF-8). */
    CSV
}
