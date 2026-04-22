package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class ParqueaderoDecorator extends EntradaDecorator {
    public ParqueaderoDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    @Override
    public double getPrecioTotal() {
        // Suponiendo que el parqueadero siempre cuesta 7000 fijos
        return super.getPrecioTotal() + 7000.0;
    }

    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Parqueadero]";
    }
}
