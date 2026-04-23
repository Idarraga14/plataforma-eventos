package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;

public class EntradaAsiento extends Entrada {
    private final Zona zona;
    private final AsientoEvento asientoEvento;

    public EntradaAsiento(Zona zona, AsientoEvento asientoEvento, double precio) {
        super(precio);
        this.zona = zona;
        this.asientoEvento = asientoEvento;
    }

    public Zona getZona() {
        return zona;
    }

    public AsientoEvento getAsientoEvento() {
        return asientoEvento;
    }

    @Override
    public void confirmarVenta() {
        asientoEvento.vender();
    }

    @Override
    public void liberarRecursos() {
        asientoEvento.setEstado(AsientoEstado.DISPONIBLE);
    }
}
