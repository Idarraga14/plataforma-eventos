package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Adaptador que traduce la interfaz interna {@link ProcesadorPago} a las llamadas
 * del servicio de pago externo {@link SimuladorPagoExterno}.
 *
 * <p>Encapsula toda la lógica de traducción: extrae el monto del dominio ({@code compra.getTotal()}),
 * invoca al Adaptee con la firma que éste espera ({@code realizarCargo(double, String, String)})
 * y convierte la respuesta textual en el booleano que el dominio comprende.</p>
 *
 * <p>[Requerimiento: RF-005] - Permite a {@code PlataformaFacadeImpl} procesar pagos sin
 * conocer la API del proveedor externo; basta con verificar el valor devuelto por
 * {@link #procesarPago} para decidir la transición de estado de la compra.</p>
 * <p>[Patrón: Adapter] - Actúa como el <strong>Adapter (Wrapper)</strong>:
 * implementa {@link ProcesadorPago} (Target) y delega en {@link SimuladorPagoExterno} (Adaptee).</p>
 */
public class SimuladorPagoAdapter implements ProcesadorPago {

    /** Referencia al servicio externo simulado cuya API es incompatible con el dominio. */
    private final SimuladorPagoExterno pagoExterno;

    /** Inicializa el adapter creando la instancia del servicio externo. */
    public SimuladorPagoAdapter() {
        this.pagoExterno = new SimuladorPagoExterno();
    }

    /**
     * Traduce una solicitud de pago del dominio a la llamada del servicio externo.
     *
     * <p>Pasos de traducción:</p>
     *
     * @param compra        compra cuyo total se cobrará
     * @param numeroTarjeta número completo de la tarjeta de pago
     * @param cvv           código de seguridad de la tarjeta
     * @return {@code true} si la respuesta del banco comienza con {@code "APROBADO"}
     */
    @Override
    public boolean procesarPago(Compra compra, String numeroTarjeta, String cvv) {
        // 1. Traducir datos del dominio al formato que exige el servicio externo
        double monto = compra.getTotal();

        // 2. Invocar al Adaptee (servicio externo incompatible)
        String respuestaBanco = pagoExterno.realizarCargo(monto, numeroTarjeta, cvv);

        // 3. Traducir la respuesta textual del banco al booleano que entiende el dominio
        return respuestaBanco.startsWith("APROBADO");
    }
}
