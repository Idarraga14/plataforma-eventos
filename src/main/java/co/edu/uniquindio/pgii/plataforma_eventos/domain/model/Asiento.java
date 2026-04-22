package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;

import java.util.UUID;

public class Asiento {
    private String idAsiento;
    private char fila;
    private int numero;
    private AsientoEstado estado;

    public Asiento(char fila, int numero) {
        this.idAsiento = UUID.randomUUID().toString();
        this.fila = fila;
        this.numero = numero;
        this.estado = AsientoEstado.DISPONIBLE;
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

    public AsientoEstado getEstado() {
        return estado;
    }

    public void ocupar() {
        if (this.estado != AsientoEstado.DISPONIBLE) {
            throw new IllegalStateException("El asiento ya está ocupado o bloqueado.");
        }
        this.estado = AsientoEstado.VENDIDO;
    }
}
