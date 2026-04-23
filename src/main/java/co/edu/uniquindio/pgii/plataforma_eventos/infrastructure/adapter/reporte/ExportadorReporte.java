package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Contrato (Patrón Adapter) para generar comprobantes de compra en distintos formatos.
 * Las implementaciones envuelven APIs externas o formatos nativos y las exponen bajo
 * la misma interfaz para la capa de aplicación/UI.
 */
public interface ExportadorReporte {

    byte[] exportar(Compra compra);

    String getExtension();

    String getDescripcionFormato();
}
