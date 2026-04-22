package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

public class EstadoPagada extends CompraState {
    public EstadoPagada(Compra compra) {
        super(compra);
    }

    @Override
    public void confirmar() {
        System.out.println("Enviando entradas al correo electrónico...");
        compra.setEstado(new EstadoConfirmada(compra));
    }

    @Override
    public void reembolsar() {
        System.out.println("Conectando con pasarela para devolver el dinero...");
        compra.setEstado(new EstadoReembolsada(compra));
    }

    @Override
    public CompraEstado getEstadoEnum() {
        return CompraEstado.PAGADA;
    }
}
