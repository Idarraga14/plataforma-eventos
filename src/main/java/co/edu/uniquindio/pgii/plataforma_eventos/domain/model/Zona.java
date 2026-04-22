package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Zona {
    private String idZona;
    private String nombre;
    private int capacidad;
    private double precioBase;
    private List<Asiento> asientos;

    public Zona(String nombre, int capacidad, double precioBase) {
        this.idZona = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.asientos = new ArrayList<>();
    }

    public String getIdZona() {
        return idZona;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public List<Asiento> getAsientos() {
        return asientos;
    }

    public void agregarAsiento(Asiento asiento) {
        this.asientos.add(asiento);
    }
}
