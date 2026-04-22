package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;

import java.time.LocalDateTime;
import java.util.List;

public class Compra {
    private String idCompra;
    private Usuario usuario;
    private Evento evento;
    private LocalDateTime fecha;
    private double total;
    private CompraEstado estado;
    private List<Entrada> entradas;
    // TODO: servicios adicionales
}
