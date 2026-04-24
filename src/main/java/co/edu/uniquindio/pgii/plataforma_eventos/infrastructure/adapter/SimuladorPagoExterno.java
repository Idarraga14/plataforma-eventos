package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter;

/**
 * Simula una pasarela de pago bancaria externa con su propia API incompatible.
 *
 * <p>Representa el <strong>Adaptee</strong> del patrón Adapter: un sistema externo cuyo
 * contrato ({@link #realizarCargo}) difiere de la interfaz que usa el dominio
 * ({@link ProcesadorPago#procesarPago}). En producción esta clase sería reemplazada
 * por el SDK real del proveedor (Stripe, PayU, etc.) sin modificar ninguna clase del dominio.</p>
 *
 * <p><strong>Regla de simulación:</strong> las tarjetas cuyo número termina en {@code "0000"}
 * son rechazadas por fondos insuficientes; cualquier otro número es aprobado.</p>
 *
 * <p>[Requerimiento: RF-005] - Simula la aprobación o rechazo de cobros durante el proceso
 * de pago, devolviendo un código de respuesta con prefijo {@code "APROBADO"} o {@code "RECHAZADO"}.</p>
 * <p>[Patrón: Adapter] - Actúa como el <strong>Adaptee</strong>; su interfaz incompatible
 * es encapsulada y traducida por {@link SimuladorPagoAdapter}.</p>
 */
public class SimuladorPagoExterno {

    /**
     * Intenta realizar un cargo a la tarjeta indicada por el monto solicitado.
     *
     * @param monto            importe a cobrar
     * @param tarjeta          número de tarjeta (las que terminan en {@code "0000"} son rechazadas)
     * @param codigoSeguridad  CVV de la tarjeta (recibido pero no validado en la simulación)
     * @return cadena con prefijo {@code "APROBADO_TX_<timestamp>"} si fue exitoso,
     *         o {@code "RECHAZADO_FONDOS_INSUFICIENTES"} si fue rechazado
     */
    public String realizarCargo(double monto, String tarjeta, String codigoSeguridad) {
        // Lógica simulada: si la tarjeta termina en "0000", rechaza el pago por fondos insuficientes
        if (tarjeta.endsWith("0000")) {
            return "RECHAZADO_FONDOS_INSUFICIENTES";
        }
        return "APROBADO_TX_" + System.currentTimeMillis();
    }
}
