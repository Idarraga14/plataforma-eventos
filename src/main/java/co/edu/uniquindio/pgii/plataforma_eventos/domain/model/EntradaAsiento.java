package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;

/**
 * Entrada de acceso a un asiento numerado específico dentro de una zona del recinto.
 *
 * <p>Mantiene una referencia al {@link AsientoEvento} del inventario comercial del evento,
 * permitiendo rastrear la disponibilidad de ese asiento en esa función concreta. Al confirmar
 * la venta, transiciona el asiento a {@code VENDIDO}; al cancelar la compra, lo libera a
 * {@code DISPONIBLE}.</p>
 *
 * <p>[Requerimiento: RF-003] - Tipo de entrada generado por {@code AsignacionPorAsientoStrategy}
 * cuando el usuario selecciona un asiento numerado específico durante el flujo de compra.</p>
 * <p>[Requerimiento: RF-018] - El método {@link #reasignarA(AsientoEvento)} permite al
 * administrador cambiar el asiento asignado a un comprador sin alterar el resto de la compra.</p>
 * <p>[Requerimiento: RF-010] - {@link #liberarRecursos()} es invocado al cancelar la compra,
 * devolviendo el asiento al estado {@code DISPONIBLE} para otros compradores.</p>
 * <p>[Patrón: Decorator] - Actúa como <strong>Componente Concreto</strong> en la jerarquía
 * Decorator. Sus métodos {@code confirmarVenta()} y {@code liberarRecursos()} son propagados
 * correctamente por {@code EntradaDecorator} hacia este objeto.</p>
 * <p>[Patrón: Factory] - Instanciada por {@code EntradaFactory#fabricar(Zona, AsientoEvento, double)}.</p>
 */
public class EntradaAsiento extends Entrada {

    /** Zona del recinto a la que pertenece el asiento. */
    private final Zona zona;

    /** Referencia al inventario comercial del asiento en este evento específico. */
    private AsientoEvento asientoEvento;

    /**
     * Crea una entrada numerada vinculando zona, asiento e inventario comercial.
     *
     * @param zona          zona del recinto donde se ubica el asiento
     * @param asientoEvento inventario comercial del asiento para el evento activo
     * @param precio        precio base de la entrada (tomado del precio base de la zona)
     */
    public EntradaAsiento(Zona zona, AsientoEvento asientoEvento, double precio) {
        super(precio);
        this.zona = zona;
        this.asientoEvento = asientoEvento;
    }

    /** @return zona del recinto a la que pertenece el asiento */
    public Zona getZona() {
        return zona;
    }

    /** @return inventario comercial del asiento en el evento activo */
    public AsientoEvento getAsientoEvento() {
        return asientoEvento;
    }

    /**
     * Reasigna la entrada a un asiento diferente dentro del mismo evento.
     *
     * <p>[Requerimiento: RF-018] - Operación administrativa que permite mover a un comprador
     * de un asiento a otro sin cancelar ni recrear la compra.</p>
     *
     * @param nuevoAsientoEvento nuevo asiento del inventario comercial del evento
     */
    public void reasignarA(AsientoEvento nuevoAsientoEvento) {
        this.asientoEvento = nuevoAsientoEvento;
    }

    /**
     * Transiciona el asiento del inventario a estado {@code VENDIDO} al confirmar el pago.
     *
     * <p>Requiere que el asiento esté en estado {@code BLOQUEADO} (reservado durante la compra).
     * Este método es propagado por la cadena de decoradores si la entrada está envuelta.</p>
     */
    @Override
    public void confirmarVenta() {
        asientoEvento.vender();
    }

    /**
     * Devuelve el asiento al estado {@code DISPONIBLE} al cancelar la compra.
     *
     * <p>[Requerimiento: RF-010] - Garantiza que el asiento quede libre para futuros compradores
     * cuando se anula la reserva.</p>
     */
    @Override
    public void liberarRecursos() {
        asientoEvento.setEstado(AsientoEstado.DISPONIBLE);
    }
}
