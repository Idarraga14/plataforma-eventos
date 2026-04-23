package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.IncidenciaEntidadAfectada;

import java.time.LocalDateTime;
import java.util.UUID;

public class Incidencia {
    private final String idIncidencia;
    private final String tipo;
    private final String descripcion;
    private final LocalDateTime fecha;
    private final IncidenciaEntidadAfectada entidadAfectada;
    private final String idEntidadAfectada;
    private final String reportadoPor;
    private boolean resuelta;

    public Incidencia(String tipo, String descripcion,
                      IncidenciaEntidadAfectada entidadAfectada,
                      String idEntidadAfectada, String reportadoPor) {
        this.idIncidencia = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
        this.entidadAfectada = entidadAfectada;
        this.idEntidadAfectada = idEntidadAfectada;
        this.reportadoPor = reportadoPor;
        this.resuelta = false;
    }

    public String getIdIncidencia() { return idIncidencia; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public IncidenciaEntidadAfectada getEntidadAfectada() { return entidadAfectada; }
    public String getIdEntidadAfectada() { return idEntidadAfectada; }
    public String getReportadoPor() { return reportadoPor; }
    public boolean isResuelta() { return resuelta; }
    public void marcarResuelta() { this.resuelta = true; }
}
