package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class SeguroCancelacionDecorator extends EntradaDecorator {

    /** Costo por defecto del seguro. Fuente de verdad para la UI y la fachada. */
    public static final double COSTO_DEFAULT = 15_000.0;

    private final double costoSeguro;

    public SeguroCancelacionDecorator(Entrada entradaEnvuelta) {
        this(entradaEnvuelta, COSTO_DEFAULT);
    }

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
