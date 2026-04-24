package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;

/**
 * Representa el inventario comercial de un {@link Asiento} físico para un {@link Evento} concreto.
 *
 * <p>Separa el concepto de <em>plantilla física</em> ({@link Asiento}, que pertenece al
 * {@link Recinto}) del concepto de <em>disponibilidad comercial</em> (este objeto), que
 * pertenece exclusivamente a una función/evento. Así, vender la silla "A1" en el
 * "Evento X" no altera su disponibilidad para el "Evento Y" que use el mismo recinto.</p>
 *
 * <p>[Requerimiento: RF-003] - Al seleccionar asientos numerados, la Strategy
 * {@code AsignacionPorAsientoStrategy} consulta el estado de este objeto para verificar
 * que esté {@code DISPONIBLE} antes de reservarlo.</p>
 * <p>[Requerimiento: RF-018] - El administrador invoca {@link #bloquear()} o {@link #liberar()}
 * para gestionar la disponibilidad de un asiento en una función específica sin afectar
 * a ningún otro evento del mismo recinto.</p>
 * <p>[Patrón: State] - Actúa como objeto de contexto cuyo atributo {@code estado}
 * ({@link AsientoEstado}) modela el ciclo de vida de un asiento en la función;
 * las transiciones válidas son encapsuladas por los métodos {@link #bloquear()},
 * {@link #liberar()} y {@link #vender()}.</p>
 */
public class AsientoEvento {

    /** Referencia inmutable a la plantilla física del recinto. */
    private final Asiento asientoFisico;

    /** Estado comercial actual del asiento para este evento. */
    private AsientoEstado estado;

    /**
     * Crea el inventario comercial para un asiento físico dado.
     * Si el asiento físico está habilitado, arranca en {@code DISPONIBLE}; si no, en {@code BLOQUEADO}.
     *
     * @param asientoFisico plantilla física del recinto
     */
    public AsientoEvento(Asiento asientoFisico) {
        this.asientoFisico = asientoFisico;
        this.estado = asientoFisico.isHabilitadoFisicamente()
                ? AsientoEstado.DISPONIBLE
                : AsientoEstado.BLOQUEADO;
    }

    /**
     * Delega al asiento físico para obtener su identificador único.
     *
     * @return ID del asiento físico asociado
     */
    public String getIdAsiento() {
        return asientoFisico.getIdAsiento();
    }

    /** @return referencia al {@link Asiento} físico del recinto */
    public Asiento getAsientoFisico() {
        return asientoFisico;
    }

    /** @return estado comercial actual del asiento en esta función */
    public AsientoEstado getEstado() {
        return estado;
    }

    /**
     * Establece directamente el estado comercial (uso interno y administrativo).
     *
     * @param estado nuevo estado a asignar
     */
    public void setEstado(AsientoEstado estado) {
        this.estado = estado;
    }

    /**
     * Transiciona el asiento a {@code BLOQUEADO}, impidiendo su venta en esta función.
     *
     * <p>[Requerimiento: RF-018] - Operación de gestión por evento que el administrador
     * invoca para reservar un asiento (mantenimiento, palco organizador, etc.).</p>
     *
     * @throws IllegalStateException si el asiento ya está {@code VENDIDO}
     */
    public void bloquear() {
        if (estado == AsientoEstado.VENDIDO) {
            throw new IllegalStateException("No se puede bloquear un asiento vendido.");
        }
        this.estado = AsientoEstado.BLOQUEADO;
    }

    /**
     * Transiciona el asiento de {@code BLOQUEADO} a {@code DISPONIBLE}.
     *
     * <p>[Requerimiento: RF-018] - Permite al administrador reabrir la venta de un
     * asiento previamente bloqueado en esta función.</p>
     *
     * @throws IllegalStateException si el asiento no está en estado {@code BLOQUEADO}
     */
    public void liberar() {
        if (estado != AsientoEstado.BLOQUEADO) {
            throw new IllegalStateException("Solo se pueden liberar asientos bloqueados.");
        }
        this.estado = AsientoEstado.DISPONIBLE;
    }

    /**
     * Transiciona el asiento de {@code BLOQUEADO} a {@code VENDIDO}, cerrando definitivamente su venta.
     *
     * <p>El flujo correcto es: la Strategy reserva el asiento poniéndolo en {@code BLOQUEADO},
     * y al confirmar el pago, {@link EntradaAsiento#confirmarVenta()} invoca este método.</p>
     *
     * @throws IllegalStateException si el asiento no está en estado {@code BLOQUEADO}
     */
    public void vender() {
        if (estado != AsientoEstado.BLOQUEADO) {
            throw new IllegalStateException("Solo se pueden vender asientos bloqueados (reservados).");
        }
        this.estado = AsientoEstado.VENDIDO;
    }
}
