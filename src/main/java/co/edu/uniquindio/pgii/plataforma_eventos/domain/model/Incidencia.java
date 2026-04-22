package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.IncidenciaEntidadAfectada;

import java.time.LocalDateTime;

public class Incidencia {
    private String idIncidencia;
    private String tipo;
    private String descripcion;
    private LocalDateTime fecha;
    private IncidenciaEntidadAfectada entidadAfectada;
}
