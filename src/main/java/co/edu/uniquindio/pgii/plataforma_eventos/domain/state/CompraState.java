package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

public abstract class CompraState {

    // Referencia a la compra que estamos gestionando
    protected Compra compra;

    public CompraState(Compra compra) {
        this.compra = compra;
    }

    // Todos los métodos lanzan excepción por defecto.
    // Solo los estados válidos sobrescribirán estos métodos.
    public void pagar() {
        throw new IllegalStateException("No se puede pagar una compra en estado: " + getEstadoEnum());
    }

    public void confirmar() {
        throw new IllegalStateException("No se puede confirmar una compra en estado: " + getEstadoEnum());
    }

    public void cancelar() {
        throw new IllegalStateException("No se puede cancelar una compra en estado: " + getEstadoEnum());
    }

    public void reembolsar() {
        throw new IllegalStateException("No se puede reembolsar una compra en estado: " + getEstadoEnum());
    }

    public abstract CompraEstado getEstadoEnum();
}
