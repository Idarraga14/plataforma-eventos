package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

public interface ProcesadorPago {
    /**
     * Intenta cobrar el total de una compra a una tarjeta específica.
     * @return true si el pago fue exitoso, false si fue rechazado.
     * @throws IllegalArgumentException si los datos de la tarjeta son inválidos.
     */
    boolean procesarPago(Compra compra, String numeroTarjeta, String cvv);
}
