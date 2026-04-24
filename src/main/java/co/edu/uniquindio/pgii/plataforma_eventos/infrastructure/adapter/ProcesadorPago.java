package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Interfaz objetivo (Target) del patrón Adapter para la pasarela de pago.
 *
 * <p>Define el contrato que la capa de aplicación usa para procesar cobros,
 * sin conocer ni depender del servicio de pago externo concreto. El único
 * implementador actualmente es {@link SimuladorPagoAdapter}, que traduce esta
 * interfaz a las llamadas de {@link SimuladorPagoExterno}.</p>
 *
 * <p>[Requerimiento: RF-005] - La facade {@code PlataformaFacadeImpl} depende
 * exclusivamente de esta interfaz para procesar pagos durante el flujo de compra,
 * permitiendo sustituir el simulador por una pasarela real sin cambiar el dominio.</p>
 * <p>[Patrón: Adapter] - Actúa como la interfaz <strong>Target</strong> del patrón.
 * {@code SimuladorPagoAdapter} es el Adapter; {@code SimuladorPagoExterno} es el Adaptee.</p>
 */
public interface ProcesadorPago {

    /**
     * Intenta cobrar el total de una compra a la tarjeta indicada.
     *
     * @param compra        compra cuyo {@code total} se intentará cobrar
     * @param numeroTarjeta número completo de la tarjeta de pago
     * @param cvv           código de seguridad de la tarjeta
     * @return {@code true} si el pago fue aprobado; {@code false} si fue rechazado
     * @throws IllegalArgumentException si los datos de la tarjeta tienen formato inválido
     */
    boolean procesarPago(Compra compra, String numeroTarjeta, String cvv);
}
