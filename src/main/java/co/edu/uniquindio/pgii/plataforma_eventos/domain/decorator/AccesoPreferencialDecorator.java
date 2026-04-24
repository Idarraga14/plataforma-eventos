package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

/**
 * Decorador que añade el servicio de acceso preferencial (ingreso prioritario sin fila) a una entrada.
 *
 * <p>[Requerimiento: RF-004] - Servicio adicional seleccionable en la pantalla de extras
 * durante el checkout. Suma {@link #COSTO} al precio total y añade la etiqueta
 * {@code [+ Acceso Preferencial Sin Fila]} a la descripción del comprobante.</p>
 * <p>[Patrón: Decorator] - Actúa como <strong>Decorador Concreto</strong>; extiende
 * {@link EntradaDecorator} sin modificar la entrada base subyacente.</p>
 */
public class AccesoPreferencialDecorator extends EntradaDecorator {

    /** Costo fijo del acceso preferencial ($25.000 COP). Fuente de verdad para todo el sistema. */
    public static final double COSTO = 25_000.0;

    /**
     * Envuelve la entrada dada con el servicio de acceso preferencial.
     *
     * @param entradaEnvuelta entrada base o decorador previo
     */
    public AccesoPreferencialDecorator(Entrada entradaEnvuelta) {
        super(entradaEnvuelta);
    }

    /**
     * Devuelve el precio total sumando el costo del acceso preferencial al precio acumulado.
     *
     * @return precio total con el acceso preferencial incluido
     */
    @Override
    public double getPrecioTotal() {
        return super.getPrecioTotal() + COSTO;
    }

    /**
     * Añade la etiqueta del acceso preferencial a la descripción acumulada.
     *
     * @return descripción de servicios con {@code [+ Acceso Preferencial Sin Fila]} al final
     */
    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Acceso Preferencial Sin Fila]";
    }
}
