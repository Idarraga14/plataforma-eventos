package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class ParqueaderoDecorator extends EntradaDecorator {

    /** Costo fijo del parqueadero. Fuente de verdad para todo el sistema. */
    public static final double COSTO = 20_000.0;

    public ParqueaderoDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + COSTO;
    }

    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Parqueadero]";
    }
}
