package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.state.CompraState;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.state.EstadoCreada;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Compra {
    private String idCompra;
    private Usuario usuario;
    private Evento evento;
    private LocalDateTime fecha;
    private double total;
    private CompraState estadoActual;
    private List<Entrada> entradas;

    public Compra(Usuario usuario, Evento evento) {
        this.idCompra = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.evento = evento;
        this.fecha = LocalDateTime.now();
        this.total = 0.0;
        this.estadoActual = new EstadoCreada(this);
        this.entradas = new ArrayList<>();
    }

    // --- DELEGACIÓN DEL COMPORTAMIENTO AL PATRÓN STATE ---
    public void pagar() {
        this.estadoActual.pagar();
    }

    public void confirmar() {
        this.estadoActual.confirmar();
    }

    public void cancelar() {
        this.estadoActual.cancelar();
    }

    public void reembolsar() {
        this.estadoActual.reembolsar();
    }

    // --- MÉTODOS DE APOYO ---
    public void setEstado(CompraState nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    public void agregarEntrada(Entrada entrada) {
        this.entradas.add(entrada);
    }

    public void calcularTotal() {
        this.total = this.entradas.stream()
                .mapToDouble(Entrada::getPrecioTotal)
                .sum();
    }

    // Para la UI de JavaFX
    public CompraEstado getEstadoEnum() {
        return this.estadoActual.getEstadoEnum();
    }

    // GETTERS
    public String getIdCompra() {
        return idCompra;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }
}
