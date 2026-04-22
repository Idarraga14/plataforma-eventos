package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter;

public class SimuladorPagoExterno {
    public String realizarCargo(double monto, String tarjeta, String codigoSeguridad) {
        // Lógica simulada: si la tarjeta termina en "0000", rechaza el pago.
        if (tarjeta.endsWith("0000")) {
            return "RECHAZADO_FONDOS_INSUFICIENTES";
        }
        return "APROBADO_TX_" + System.currentTimeMillis();
    }
}
