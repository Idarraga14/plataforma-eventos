package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class PaqueteVIPDecorator extends EntradaDecorator {

    /** Costo fijo del pase VIP. Fuente de verdad para todo el sistema. */
    public static final double COSTO = 50_000.0;

    public PaqueteVIPDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + COSTO;
    }

    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Acceso VIP Backstage]";
    }
}
