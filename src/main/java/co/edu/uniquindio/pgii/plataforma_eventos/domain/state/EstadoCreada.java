package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Estado inicial de una {@link Compra} recién creada, antes del procesamiento del pago.
 *
 * <p>Desde este estado la compra puede avanzar a {@code EstadoPagada} (si el pago es exitoso)
 * o ser abandonada pasando a {@code EstadoCancelada} (si el usuario desiste antes de pagar).
 * Las operaciones {@code confirmar()} y {@code reembolsar()} no son válidas desde aquí y
 * heredan el lanzamiento de excepción de {@link CompraState}.</p>
 *
 * <p>[Requerimiento: RF-005] - Corresponde al primer paso del flujo de compra; la compra
 * permanece en este estado mientras el usuario selecciona entradas y configura servicios extra.</p>
 * <p>[Requerimiento: RF-010] - {@link #cancelar()} permite al usuario desistir antes de
 * completar el pago, sin cargo alguno.</p>
 * <p>[Patrón: State] - Actúa como <strong>Estado Concreto</strong>; implementa las
 * transiciones válidas ({@code CREADA → PAGADA} y {@code CREADA → CANCELADA}).</p>
 */
public class EstadoCreada extends CompraState {

    /**
     * Crea el estado inicial de una compra.
     *
     * @param compra compra contexto que este estado gestiona
     */
    public EstadoCreada(Compra compra) {
        super(compra);
    }

    /**
     * Procesa el pago y transiciona la compra al estado {@code PAGADA}.
     *
     * <p>// 1. El pago es aprobado por el {@code SimuladorPagoAdapter} antes de llegar aquí.<br>
     * // 2. Se registra la transición y se reemplaza el estado en el contexto.</p>
     */
    @Override
    public void pagar() {
        // 1. Simular confirmación de pago exitoso
        System.out.println("Procesando pago... Pago exitoso.");
        // 2. Transición de estado: CREADA → PAGADA
        compra.setEstado(new EstadoPagada(compra));
    }

    /**
     * Cancela la compra sin cargo, transicionando al estado {@code CANCELADA}.
     *
     * <p>[Requerimiento: RF-010] - Permite al usuario desistir antes del pago.</p>
     */
    @Override
    public void cancelar() {
        System.out.println("Compra cancelada por el usuario antes de pagar.");
        compra.setEstado(new EstadoCancelada(compra));
    }

    /** @return {@link CompraEstado#CREADA} */
    @Override
    public CompraEstado getEstadoEnum() {
        return CompraEstado.CREADA;
    }
}
