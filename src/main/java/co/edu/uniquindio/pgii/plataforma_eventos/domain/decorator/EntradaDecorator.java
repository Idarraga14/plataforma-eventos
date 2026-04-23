package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

public abstract class EntradaDecorator extends Entrada {

    // El objeto que estamos decorando (entrada base)
    protected Entrada entradaEnvuelta;

    public EntradaDecorator(Entrada entradaEnvuelta) {
        // Pasamos 0 al constructor base porque el precio lo dicta la entrada envuelta
        super(0);
        this.entradaEnvuelta = entradaEnvuelta;
    }

    @Override
    public double getPrecioTotal() {
        return entradaEnvuelta.getPrecioTotal();
    }

    @Override
    public String getDescripcionServicios() {
        return entradaEnvuelta.getDescripcionServicios();
    }
}
