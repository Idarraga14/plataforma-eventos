package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

/**
 * Decorador que añade un Seguro de Cancelación a una entrada, con costo configurable.
 *
 * <p>El seguro garantiza al comprador el reembolso total si cancela su asistencia dentro
 * del plazo acordado. Ofrece un constructor con el costo por defecto ({@link #COSTO_DEFAULT})
 * y otro con costo personalizado para escenarios de precios diferenciados.</p>
 *
 * <p>[Requerimiento: RF-004] - Servicio adicional seleccionable en la pantalla de extras
 * durante el checkout. Suma {@link #COSTO_DEFAULT} (o el costo configurado) al precio total
 * y añade la etiqueta {@code [+ Seguro de Cancelación]} al comprobante.</p>
 * <p>[Patrón: Decorator] - Actúa como <strong>Decorador Concreto</strong>; extiende
 * {@link EntradaDecorator} sin modificar la entrada base subyacente.</p>
 */
public class SeguroCancelacionDecorator extends EntradaDecorator {

    /** Costo estándar del seguro de cancelación ($15.000 COP). Fuente de verdad para la UI y la fachada. */
    public static final double COSTO_DEFAULT = 15_000.0;

    /** Costo efectivo del seguro (puede diferir del default si se usa el constructor personalizado). */
    private final double costoSeguro;

    /**
     * Envuelve la entrada con el seguro de cancelación al costo estándar ({@link #COSTO_DEFAULT}).
     *
     * @param entradaEnvuelta entrada base o decorador previo
     */
    public SeguroCancelacionDecorator(Entrada entradaEnvuelta) {
        this(entradaEnvuelta, COSTO_DEFAULT);
    }

    /**
     * Envuelve la entrada con el seguro de cancelación a un costo personalizado.
     *
     * @param entradaEnvuelta entrada base o decorador previo
     * @param costoSeguro     costo personalizado del seguro
     */
    public SeguroCancelacionDecorator(Entrada entradaEnvuelta, double costoSeguro) {
        super(entradaEnvuelta);
        this.costoSeguro = costoSeguro;
    }

    /**
     * Devuelve el precio total sumando el costo del seguro al precio acumulado.
     *
     * @return precio total con el seguro incluido
     */
    @Override
    public double getPrecioTotal() {
        // Toma el precio de lo que hay debajo y le suma el seguro
        return super.getPrecioTotal() + this.costoSeguro;
    }

    /**
     * Añade la etiqueta del seguro a la descripción acumulada.
     *
     * @return descripción de servicios con {@code [+ Seguro de Cancelación]} al final
     */
    @Override
    public String getDescripcionServicios() {
        return super.getDescripcionServicios() + " [+ Seguro de Cancelación]";
    }
}
