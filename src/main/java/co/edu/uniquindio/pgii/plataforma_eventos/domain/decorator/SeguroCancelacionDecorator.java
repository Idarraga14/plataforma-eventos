package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class SeguroCancelacionDecorator extends EntradaDecorator {

    private final double costoSeguro;

    public SeguroCancelacionDecorator(Entrada entradaEnvuelta, double costoSeguro) {
        super(entradaEnvuelta);
        this.costoSeguro = costoSeguro;
    }

    @Override
    public double getPrecioTotal() {
        // Toma el precio de lo que hay debajo y le suma el seguro
        return super.getPrecioTotal() + this.costoSeguro;
    }

    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Seguro de Cancelación]";
    }
}
