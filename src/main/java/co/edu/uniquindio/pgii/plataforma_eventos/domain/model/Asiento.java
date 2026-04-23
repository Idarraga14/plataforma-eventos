package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.UUID;

public class Asiento {
    private final String idAsiento;
    private final char fila;
    private final int numero;
    private boolean habilitadoFisicamente;

    public Asiento(char fila, int numero) {
        this.idAsiento = UUID.randomUUID().toString();
        this.fila = fila;
        this.numero = numero;
        this.habilitadoFisicamente = true;
    }

    public String getIdAsiento() {
        return idAsiento;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public String getSalida() {
        return String.format("%c%d", fila, numero);
    }

    public boolean isHabilitadoFisicamente() {
        return habilitadoFisicamente;
    }

    public void setHabilitadoFisicamente(boolean habilitadoFisicamente) {
        this.habilitadoFisicamente = habilitadoFisicamente;
    }
}
