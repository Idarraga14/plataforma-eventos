package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Estado terminal de una {@link Compra} en la que el importe pagado fue devuelto al comprador.
 *
 * <p>Sólo se alcanza desde {@code EstadoPagada} (o {@code EstadoConfirmada} en futuras
 * extensiones). Una vez reembolsada, ninguna operación del ciclo de vida es posible;
 * todas heredan el lanzamiento de {@link IllegalStateException} de {@link CompraState}.</p>
 *
 * <p>[Requerimiento: RF-011] - Alcanzado cuando el administrador o el sistema procesan
 * la devolución del importe tras una cancelación de un evento o solicitud de reembolso
 * del comprador. Las entradas asociadas pasan a estado {@code ANULADA}.</p>
 * <p>[Patrón: State] - Actúa como <strong>Estado Concreto Terminal</strong>; representa
 * la resolución financiera de una compra pagada que no se materializó.</p>
 */
public class EstadoReembolsada extends CompraState {

    /**
     * @param compra compra contexto en estado REEMBOLSADA
     */
    public EstadoReembolsada(Compra compra) {
        super(compra);
    }

    /** @return {@link CompraEstado#REEMBOLSADA} */
    @Override
    public CompraEstado getEstadoEnum() {
        return CompraEstado.REEMBOLSADA;
    }
}
