package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

public class EstadoCreada extends CompraState {

    public EstadoCreada(Compra compra) {
        super(compra);
    }

    @Override
    public void pagar() {
        // Lógica de transición permitida
        System.out.println("Procesando pago... Pago exitoso.");
        compra.setEstado(new EstadoPagada(compra)); // Transición de estado
    }

    @Override
    public void cancelar() {
        System.out.println("Compra cancelada por el usuario antes de pagar.");
        compra.setEstado(new EstadoCancelada(compra));
    }


    @Override
    public CompraEstado getEstadoEnum() {
        return CompraEstado.CREADA;
    }
}
