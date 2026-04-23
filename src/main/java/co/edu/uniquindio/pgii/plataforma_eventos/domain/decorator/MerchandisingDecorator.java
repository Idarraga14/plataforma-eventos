package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class MerchandisingDecorator extends EntradaDecorator {

    /** Costo fijo del kit de merchandising oficial. Fuente de verdad para todo el sistema. */
    public static final double COSTO = 35_000.0;

    public MerchandisingDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + COSTO;
    }

    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Kit de Merchandising Oficial]";
    }
}
