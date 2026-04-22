package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EntradaEstado;

public class Entrada {
    private String idEntrada;
    private Zona zona;
    private Asiento asiento; // TODO: si aplica
    private double precio;
    private EntradaEstado estado;
}
