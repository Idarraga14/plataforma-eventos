package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Estado de una {@link Compra} cuyo pago ha sido aprobado, pendiente de confirmación definitiva.
 *
 * <p>Desde este estado la compra puede avanzar a {@code EstadoConfirmada} (confirmación final)
 * o retroceder a {@code EstadoReembolsada} (devolución del importe). Las operaciones
 * {@code pagar()} y {@code cancelar()} no son válidas desde aquí.</p>
 *
 * <p>[Requerimiento: RF-005] - Alcanzado tras el procesamiento exitoso del pago por el
 * {@code SimuladorPagoAdapter}; las entradas están reservadas pero aún no confirmadas.</p>
 * <p>[Requerimiento: RF-006] - {@link #confirmar()} completa el flujo de compra, activando
 * las entradas para acceso al evento y notificando al comprador.</p>
 * <p>[Requerimiento: RF-011] - {@link #reembolsar()} inicia la devolución del importe
 * al comprador a través de la pasarela de pago.</p>
 * <p>[Patrón: State] - Actúa como <strong>Estado Concreto</strong>; implementa las
 * transiciones ({@code PAGADA → CONFIRMADA} y {@code PAGADA → REEMBOLSADA}).</p>
 */
public class EstadoPagada extends CompraState {

    /**
     * @param compra compra contexto en estado PAGADA
     */
    public EstadoPagada(Compra compra) {
        super(compra);
    }

    /**
     * Confirma la orden de compra y notifica al comprador, transicionando a {@code CONFIRMADA}.
     *
     * <p>// 1. Dispara el envío de entradas al correo del comprador (simulado).<br>
     * // 2. Reemplaza el estado en el contexto: PAGADA → CONFIRMADA.</p>
     *
     * <p>[Requerimiento: RF-006] - Punto de activación definitiva de las entradas.</p>
     */
    @Override
    public void confirmar() {
        // 1. Notificar al comprador (simulado con log de consola)
        System.out.println("Enviando entradas al correo electrónico...");
        // 2. Transición de estado: PAGADA → CONFIRMADA
        compra.setEstado(new EstadoConfirmada(compra));
    }

    /**
     * Inicia el reembolso del importe pagado, transicionando a {@code REEMBOLSADA}.
     *
     * <p>// 1. Conecta con la pasarela de pago para iniciar la devolución (simulado).<br>
     * // 2. Reemplaza el estado en el contexto: PAGADA → REEMBOLSADA.</p>
     *
     * <p>[Requerimiento: RF-011] - Sólo es posible reembolsar desde PAGADA o CONFIRMADA.</p>
     */
    @Override
    public void reembolsar() {
        // 1. Notificar a la pasarela de pago (simulado con log de consola)
        System.out.println("Conectando con pasarela para devolver el dinero...");
        // 2. Transición de estado: PAGADA → REEMBOLSADA
        compra.setEstado(new EstadoReembolsada(compra));
    }

    /** @return {@link CompraEstado#PAGADA} */
    @Override
    public CompraEstado getEstadoEnum() {
        return CompraEstado.PAGADA;
    }
}
