package co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;

/**
 * Decorador abstracto base que envuelve una {@link Entrada} para añadirle servicios adicionales.
 *
 * <p>Extiende {@link Entrada} y mantiene una referencia a la entrada envuelta
 * ({@code entradaEnvuelta}). Todos los métodos delegan al objeto interior, permitiendo
 * que los decoradores concretos se limiten a sobreescribir sólo los métodos que amplían
 * (precio y descripción), sin repetir la lógica de delegación.</p>
 *
 * <p>Los decoradores pueden anidarse en cualquier combinación y orden, acumulando el precio
 * y la descripción de cada servicio seleccionado por el usuario.</p>
 *
 * <p>[Requerimiento: RF-004] - Los servicios adicionales (VIP, Seguro, Parqueadero, etc.)
 * son añadidos al precio base de la entrada mediante decoradores aplicados en
 * {@code CheckoutExtrasController} según la selección del usuario.</p>
 * <p>[Requerimiento: RF-049] - Implementación explícita del patrón estructural
 * <strong>Decorator</strong>, exigido como requerimiento no funcional del proyecto.</p>
 * <p>[Patrón: Decorator] - Actúa como el <strong>Decorator Abstracto</strong> de la jerarquía.
 * Las subclases concretas ({@code PaqueteVIPDecorator}, {@code SeguroCancelacionDecorator},
 * {@code ParqueaderoDecorator}, {@code MerchandisingDecorator}, {@code AccesoPreferencialDecorator})
 * son los Decoradores Concretos.</p>
 */
public abstract class EntradaDecorator extends Entrada {

    /**
     * Referencia a la entrada que está siendo decorada (puede ser una entrada concreta
     * o a su vez otro decorador, formando cadenas).
     */
    protected Entrada entradaEnvuelta;

    /**
     * Inicializa el decorador con la entrada a envolver.
     * Pasa {@code 0} al constructor base ya que el precio es dictado por la entrada envuelta.
     *
     * @param entradaEnvuelta entrada base o decorador previo a envolver
     */
    public EntradaDecorator(Entrada entradaEnvuelta) {
        // Pasamos 0 al constructor base porque el precio lo dicta la entrada envuelta
        super(0);
        this.entradaEnvuelta = entradaEnvuelta;
    }

    /**
     * Delega el cálculo del precio total a la entrada envuelta.
     * Los decoradores concretos invocan {@code super.getPrecioTotal()} y suman su costo adicional.
     *
     * @return precio total acumulado hasta la entrada base
     */
    @Override
    public double getPrecioTotal() {
        // Delega la responsabilidad a la capa inferior
        return entradaEnvuelta.getPrecioTotal();
    }

    /**
     * Delega la descripción de servicios a la entrada envuelta.
     * Los decoradores concretos invocan {@code super.getDescripcionServicios()} y concatenan
     * el nombre de su servicio adicional.
     *
     * @return descripción acumulada de servicios hasta la entrada base
     */
    @Override
    public String getDescripcionServicios() {
        return entradaEnvuelta.getDescripcionServicios();
    }

    /**
     * Propaga la confirmación de venta a la entrada envuelta.
     * Garantiza que {@link co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento#confirmarVenta()}
     * sea invocado aunque la entrada esté envuelta en múltiples decoradores.
     */
    @Override
    public void confirmarVenta() {
        entradaEnvuelta.confirmarVenta();
    }

    /**
     * Propaga la liberación de recursos a la entrada envuelta.
     * Garantiza que {@link co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento#liberarRecursos()}
     * sea invocado aunque la entrada esté envuelta en múltiples decoradores.
     */
    @Override
    public void liberarRecursos() {
        entradaEnvuelta.liberarRecursos();
    }

    /**
     * Devuelve la entrada inmediatamente envuelta por este decorador.
     * Útil para inspeccionar la cadena de decoradores.
     *
     * @return entrada envuelta (puede ser otro decorador o la entrada base concreta)
     */
    public Entrada getEntradaEnvuelta() {
        return entradaEnvuelta;
    }
}
