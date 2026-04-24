package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Estado terminal positivo de una {@link Compra} validada y confirmada definitivamente.
 *
 * <p>Una vez confirmada, la compra no admite más transiciones de ciclo de vida desde
 * esta implementación. Todas las operaciones heredan el lanzamiento de
 * {@link IllegalStateException} de {@link CompraState}. Las entradas asociadas son
 * válidas para acceso al evento.</p>
 *
 * <p>[Requerimiento: RF-006] - Alcanzado tras la invocación de {@code confirmar()} desde
 * {@code EstadoPagada}; representa el cierre exitoso del proceso de compra.</p>
 * <p>[Requerimiento: RF-009] - Las compras en este estado aparecen en el historial del
 * usuario con el indicador visual de compra completada.</p>
 * <p>[Patrón: State] - Actúa como <strong>Estado Concreto Terminal</strong>; no implementa
 * ninguna transición salida porque {@code CONFIRMADA} es un estado final del negocio.</p>
 */
public class EstadoConfirmada extends CompraState {

    /**
     * @param compra compra contexto en estado CONFIRMADA
     */
    public EstadoConfirmada(Compra compra) {
        super(compra);
    }

    /** @return {@link CompraEstado#CONFIRMADA} */
    @Override
    public CompraEstado getEstadoEnum() {
        return CompraEstado.CONFIRMADA;
    }
}
