package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

public class EntradaAsiento extends Entrada {
    private Zona zona;
    private Asiento asiento;

    public EntradaAsiento(Zona zona, Asiento asiento, double precio) {
        super(precio);
        this.zona = zona;
        this.asiento = asiento;
    }
}
