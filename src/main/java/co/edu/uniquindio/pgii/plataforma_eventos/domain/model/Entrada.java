package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EntradaEstado;

import java.util.UUID;

public abstract class Entrada {
    private String idEntrada;
    private double precioBase;
    private EntradaEstado estado;

    public Entrada(double precioBase) {
        this.idEntrada = UUID.randomUUID().toString();
        this.precioBase = precioBase;
        this.estado = EntradaEstado.ACTIVA;
    }

    public double getPrecioTotal() {
        return this.precioBase;
    }

    public String getDescripcionServicios() {
        return "Entrada estándar";
    }

    public String getIdEntrada() {
        return idEntrada;
    }

    public EntradaEstado getEstado() {
        return estado;
    }

    public void setEstado(EntradaEstado estado) {
        this.estado = estado;
    }

    /** Marca como vendidos los recursos físicos asociados (asientos). Default: nada. */
    public void confirmarVenta() {
    }

    /** Libera los recursos físicos asociados (asientos) dejándolos DISPONIBLES. Default: nada. */
    public void liberarRecursos() {
    }
}
