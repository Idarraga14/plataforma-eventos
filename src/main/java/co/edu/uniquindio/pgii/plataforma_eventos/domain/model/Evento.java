package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;

import java.time.LocalDateTime;
import java.util.List;

public class Evento {
    private String idEvento;
    private String nombre;
    private EventoCategoria categoria;
    private String descripcion;
    private String ciudad;
    private LocalDateTime fecha;
    private EventoEstado estado;
    private List<String> politicas;
    private Recinto recinto;
}
