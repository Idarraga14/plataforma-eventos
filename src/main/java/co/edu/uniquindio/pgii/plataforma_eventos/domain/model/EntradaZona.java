package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

/**
 * Entrada de acceso libre a una zona del recinto, sin asiento numerado asignado.
 *
 * <p>Representa el ticket más básico: el comprador puede ubicarse en cualquier lugar
 * dentro de la zona adquirida (ej. "Cancha General", "Tribuna Sur"). No mantiene
 * referencia a ningún {@link AsientoEvento}; por tanto, {@code confirmarVenta()}
 * y {@code liberarRecursos()} heredan el comportamiento vacío de {@link Entrada}.</p>
 *
 * <p>[Requerimiento: RF-003] - Tipo de entrada generado por {@code AsignacionPorZonaStrategy}
 * cuando el usuario selecciona una zona de acceso libre durante el flujo de compra.</p>
 * <p>[Requerimiento: RF-004] - El precio de esta entrada equivale al {@code precioBase}
 * de la zona; los decoradores opcionales suman costos adicionales sobre este valor.</p>
 * <p>[Patrón: Decorator] - Actúa como <strong>Componente Concreto</strong> en la jerarquía
 * Decorator. Puede ser envuelto por cualquier {@code EntradaDecorator} (VIP, Seguro, etc.)
 * para enriquecer su precio y descripción sin alterar esta clase.</p>
 * <p>[Patrón: Factory] - Instanciada por {@code EntradaFactory#fabricar(Zona, double)}.</p>
 */
public class EntradaZona extends Entrada {

    /** Zona de acceso libre a la que da acceso esta entrada. */
    private Zona zona;

    /**
     * Crea una entrada de zona libre con el precio indicado.
     *
     * @param zona   zona del recinto a la que da acceso esta entrada
     * @param precio precio base de la entrada (tomado del precio base de la zona)
     */
    public EntradaZona(Zona zona, double precio) {
        super(precio);
        this.zona = zona;
    }

    /**
     * Devuelve la zona a la que da acceso esta entrada.
     *
     * @return zona asociada a la entrada
     */
    public Zona getZona() {
        return zona;
    }
}
