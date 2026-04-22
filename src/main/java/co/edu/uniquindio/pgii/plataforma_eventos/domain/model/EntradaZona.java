package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

public class EntradaZona extends Entrada {
    private Zona zona;

    public EntradaZona(Zona zona, double precio) {
        super(precio);
        this.zona = zona;
    }

    public Zona getZona() {
        return zona;
    }
}
