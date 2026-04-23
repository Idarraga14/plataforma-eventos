package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;

public class EntradaAsiento extends Entrada {
    private Zona zona;
    private Asiento asiento;

    public EntradaAsiento(Zona zona, Asiento asiento, double precio) {
        super(precio);
        this.zona = zona;
        this.asiento = asiento;
    }

    public Zona getZona() {
        return zona;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    @Override
    public void confirmarVenta() {
        asiento.setEstado(AsientoEstado.VENDIDO);
    }

    @Override
    public void liberarRecursos() {
        asiento.setEstado(AsientoEstado.DISPONIBLE);
    }
}
