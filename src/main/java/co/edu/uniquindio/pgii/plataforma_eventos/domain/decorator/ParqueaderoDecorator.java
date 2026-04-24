package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

/**
 * Decorador que añade el servicio de parqueadero reservado a una entrada.
 *
 * <p>[Requerimiento: RF-004] - Servicio adicional seleccionable en la pantalla de extras
 * durante el checkout. Suma {@link #COSTO} al precio total y añade la etiqueta
 * {@code [+ Parqueadero]} a la descripción del comprobante.</p>
 * <p>[Patrón: Decorator] - Actúa como <strong>Decorador Concreto</strong>; extiende
 * {@link EntradaDecorator} sin modificar la entrada base subyacente.</p>
 */
public class ParqueaderoDecorator extends EntradaDecorator {

    /** Costo fijo del parqueadero reservado ($20.000 COP). Fuente de verdad para todo el sistema. */
    public static final double COSTO = 20_000.0;

    /**
     * Envuelve la entrada dada con el servicio de parqueadero.
     *
     * @param entradaEnvuelta entrada base o decorador previo
     */
    public ParqueaderoDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    /**
     * Devuelve el precio total sumando el costo del parqueadero al precio acumulado.
     *
     * @return precio total con el parqueadero incluido
     */
    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + COSTO;
    }

    /**
     * Añade la etiqueta del parqueadero a la descripción acumulada.
     *
     * @return descripción de servicios con {@code [+ Parqueadero]} al final
     */
    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Parqueadero]";
    }
}
