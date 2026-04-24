package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

/**
 * Decorador que añade el servicio de Pase VIP con acceso a zona backstage a una entrada.
 *
 * <p>[Requerimiento: RF-004] - Servicio adicional premium seleccionable por el usuario
 * en la pantalla de extras durante el checkout. Suma {@link #COSTO} al precio total
 * y añade la etiqueta {@code [+ Acceso VIP Backstage]} a la descripción del comprobante.</p>
 * <p>[Patrón: Decorator] - Actúa como <strong>Decorador Concreto</strong>; extiende
 * {@link EntradaDecorator} y sobreescribe precio y descripción sin conocer ni modificar
 * la entrada base subyacente.</p>
 */
public class PaqueteVIPDecorator extends EntradaDecorator {

    /** Costo fijo del pase VIP ($50.000 COP). Fuente de verdad para todo el sistema. */
    public static final double COSTO = 50_000.0;

    /**
     * Envuelve la entrada dada con el servicio VIP.
     *
     * @param entradaEnvuelta entrada base o decorador previo al que se añade el servicio VIP
     */
    public PaqueteVIPDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    /**
     * Devuelve el precio total sumando el costo del pase VIP al precio acumulado.
     *
     * @return precio total con el pase VIP incluido
     */
    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + COSTO;
    }

    /**
     * Añade la etiqueta del servicio VIP a la descripción acumulada.
     *
     * @return descripción de servicios con {@code [+ Acceso VIP Backstage]} al final
     */
    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Acceso VIP Backstage]";
    }
}
