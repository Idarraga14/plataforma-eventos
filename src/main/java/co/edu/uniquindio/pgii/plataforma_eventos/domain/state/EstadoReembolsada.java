package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

public class EstadoReembolsada extends CompraState {
    public EstadoReembolsada(Compra compra) {
        super(compra);
    }

    @Override
    public CompraEstado getEstadoEnum() {
        return CompraEstado.REEMBOLSADA;
    }
}
