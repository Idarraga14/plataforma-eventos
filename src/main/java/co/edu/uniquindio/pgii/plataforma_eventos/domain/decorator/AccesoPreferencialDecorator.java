package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class AccesoPreferencialDecorator extends EntradaDecorator {

    /** Costo fijo del acceso preferencial (entrada prioritaria sin fila). Fuente de verdad para todo el sistema. */
    public static final double COSTO = 25_000.0;

    public AccesoPreferencialDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + COSTO;
    }

    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Acceso Preferencial Sin Fila]";
    }
}
