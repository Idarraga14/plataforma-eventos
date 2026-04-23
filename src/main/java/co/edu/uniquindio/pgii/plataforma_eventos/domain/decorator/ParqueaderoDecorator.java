package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class ParqueaderoDecorator extends EntradaDecorator {
    public ParqueaderoDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + 20_000.0;
    }

    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Parqueadero]";
    }
}
