package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public class PaqueteVIPDecorator extends EntradaDecorator {
    public PaqueteVIPDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    @Override
    public double getPrecioTotal() {
        // Suponiendo que el pase VIP siempre cuesta 50000 fijos
        return super.getPrecioTotal() + 50000.0;
    }

    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Acceso VIP Backstage]";
    }
}
