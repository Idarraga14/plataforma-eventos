package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.UUID;

public class MedioPago {
    private String idMedioPago;
    private String titular;
    private String ultimosCuatroDigitos;

    public MedioPago(String titular, String numeroTarjeta) {
        this.idMedioPago = UUID.randomUUID().toString();
        this.titular = titular;
        // Solo guardamos los últimos 4 dígitos por "seguridad" simulada
        this.ultimosCuatroDigitos = numeroTarjeta.substring(numeroTarjeta.length() - 4);
    }

    public String getIdMedioPago() {
        return idMedioPago;
    }

    public String getTitular() {
        return titular;
    }

    public String getUltimosCuatro() {
        return ultimosCuatroDigitos;
    }
}
