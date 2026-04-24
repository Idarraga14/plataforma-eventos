package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

/**
 * Decorador que añade un kit de merchandising oficial del evento a una entrada.
 *
 * <p>[Requerimiento: RF-004] - Servicio adicional seleccionable en la pantalla de extras
 * durante el checkout. Suma {@link #COSTO} al precio total y añade la etiqueta
 * {@code [+ Kit de Merchandising Oficial]} a la descripción del comprobante.</p>
 * <p>[Patrón: Decorator] - Actúa como <strong>Decorador Concreto</strong>; extiende
 * {@link EntradaDecorator} sin modificar la entrada base subyacente.</p>
 */
public class MerchandisingDecorator extends EntradaDecorator {

    /** Costo fijo del kit de merchandising oficial ($35.000 COP). Fuente de verdad para todo el sistema. */
    public static final double COSTO = 35_000.0;

    /**
     * Envuelve la entrada dada con el kit de merchandising.
     *
     * @param entradaEnvuelta entrada base o decorador previo
     */
    public MerchandisingDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    /**
     * Devuelve el precio total sumando el costo del kit de merchandising al precio acumulado.
     *
     * @return precio total con el kit de merchandising incluido
     */
    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + COSTO;
    }

    /**
     * Añade la etiqueta del kit de merchandising a la descripción acumulada.
     *
     * @return descripción de servicios con {@code [+ Kit de Merchandising Oficial]} al final
     */
    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Kit de Merchandising Oficial]";
    }
}
