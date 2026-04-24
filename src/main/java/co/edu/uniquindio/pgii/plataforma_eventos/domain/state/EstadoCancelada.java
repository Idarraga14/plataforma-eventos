package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Estado terminal de una {@link Compra} que fue anulada antes de su confirmación.
 *
 * <p>Una vez cancelada, ninguna operación del ciclo de vida es posible; todas heredan
 * el lanzamiento de {@link IllegalStateException} de {@link CompraState}. Los recursos
 * (asientos) fueron liberados durante la transición a este estado.</p>
 *
 * <p>[Requerimiento: RF-010] - Alcanzado desde {@code EstadoCreada} (antes del pago)
 * o desde {@code EstadoPagada} (si la pasarela devuelve error o el admin anula la orden).
 * Las entradas asociadas pasan al estado {@code ANULADA}.</p>
 * <p>[Patrón: State] - Actúa como <strong>Estado Concreto Terminal</strong>; representa
 * el cierre negativo del proceso de compra sin posibilidad de reactivación.</p>
 */
public class EstadoCancelada extends CompraState {

    /**
     * @param compra compra contexto en estado CANCELADA
     */
    public EstadoCancelada(Compra compra) {
        super(compra);
    }

    /** @return {@link CompraEstado#CANCELADA} */
    @Override
    public CompraEstado getEstadoEnum() {
        return CompraEstado.CANCELADA;
    }
}
