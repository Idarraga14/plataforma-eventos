package co.edu.uniquindio.pgii.plataforma_eventos.domain.state;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

/**
 * Interfaz abstracta del patrón State para el ciclo de vida de una {@link Compra}.
 *
 * <p>Define las operaciones que pueden ejecutarse sobre una compra ({@code pagar},
 * {@code confirmar}, {@code cancelar}, {@code reembolsar}) con implementaciones por defecto
 * que lanzan {@link IllegalStateException}. Cada subclase concreta sobrescribe únicamente
 * las operaciones permitidas desde su estado, ignorando las demás (que heredan el lanzamiento
 * de excepción).</p>
 *
 * <p>[Requerimiento: RF-049] - Implementación explícita del patrón de comportamiento
 * <strong>State</strong>, exigido como requerimiento no funcional del proyecto.</p>
 * <p>[Patrón: State] - Actúa como la <strong>Interfaz de Estado (State)</strong> de la
 * jerarquía; {@link Compra} es el Contexto y las cinco subclases concretas
 * ({@code EstadoCreada}, {@code EstadoPagada}, {@code EstadoConfirmada},
 * {@code EstadoCancelada}, {@code EstadoReembolsada}) son los Estados Concretos.</p>
 */
public abstract class CompraState {

    /** Referencia a la compra (Contexto) que este estado está gestionando. */
    protected Compra compra;

    /**
     * Vincula el estado concreto con su compra contexto.
     *
     * @param compra la compra cuyo ciclo de vida gestiona este estado
     */
    public CompraState(Compra compra) {
        this.compra = compra;
    }

    /**
     * Procesa el pago de la compra.
     * Por defecto lanza excepción; sólo {@code EstadoCreada} lo sobrescribe.
     *
     * @throws IllegalStateException si la operación no está permitida en el estado actual
     */
    public void pagar() {
        throw new IllegalStateException("No se puede pagar una compra en estado: " + getEstadoEnum());
    }

    /**
     * Confirma definitivamente la orden.
     * Por defecto lanza excepción; sólo {@code EstadoPagada} lo sobrescribe.
     *
     * @throws IllegalStateException si la operación no está permitida en el estado actual
     */
    public void confirmar() {
        throw new IllegalStateException("No se puede confirmar una compra en estado: " + getEstadoEnum());
    }

    /**
     * Cancela la compra, liberando los recursos reservados.
     * Por defecto lanza excepción; {@code EstadoCreada} y {@code EstadoPagada} lo sobrescriben.
     *
     * @throws IllegalStateException si la operación no está permitida en el estado actual
     */
    public void cancelar() {
        throw new IllegalStateException("No se puede cancelar una compra en estado: " + getEstadoEnum());
    }

    /**
     * Inicia el proceso de reembolso al comprador.
     * Por defecto lanza excepción; sólo {@code EstadoPagada} lo sobrescribe.
     *
     * @throws IllegalStateException si la operación no está permitida en el estado actual
     */
    public void reembolsar() {
        throw new IllegalStateException("No se puede reembolsar una compra en estado: " + getEstadoEnum());
    }

    /**
     * Devuelve el valor de la enumeración {@link CompraEstado} que representa este estado.
     * Usado por la UI de JavaFX para mostrar el estado de la compra sin exponer el objeto de estado.
     *
     * @return constante de {@link CompraEstado} correspondiente a este estado concreto
     */
    public abstract CompraEstado getEstadoEnum();
}
