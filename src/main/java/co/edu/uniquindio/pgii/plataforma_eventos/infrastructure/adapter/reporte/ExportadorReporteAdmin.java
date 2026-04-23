package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte;

public interface ExportadorReporteAdmin {

    byte[] exportar(ReporteOperativo reporte);

    String getExtension();

    String getDescripcionFormato();
}
