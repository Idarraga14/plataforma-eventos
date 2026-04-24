package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EntradaEstado;

import java.util.UUID;

/**
 * Clase base abstracta de todos los tipos de entrada emitidos por la plataforma.
 *
 * <p>Define la interfaz mínima que comparten las entradas concretas ({@link EntradaZona},
 * {@link EntradaAsiento}) y sobre la cual los decoradores del paquete {@code domain.decorator}
 * añaden servicios adicionales (VIP, Seguro, Parqueadero, etc.) sin modificar esta clase.</p>
 *
 * <p>[Requerimiento: RF-004] - Toda entrada emitida tiene un precio base calculado a partir
 * del precio de su {@link Zona}, al que los decoradores añaden el costo de cada servicio extra.</p>
 * <p>[Requerimiento: RF-007] - Al crear la entrada en la {@code EntradaFactory} se emite
 * automáticamente en estado {@link EntradaEstado#ACTIVA}, validando el acceso al evento.</p>
 * <p>[Patrón: Decorator] - Actúa como el <strong>Componente Base</strong> de la jerarquía
 * Decorator. Las subclases concretas son {@code EntradaZona} y {@code EntradaAsiento};
 * los Decoradores Concretos extienden {@code EntradaDecorator} que a su vez extiende esta clase.</p>
 * <p>[Patrón: Factory] - Es el tipo de retorno de {@code EntradaFactory#fabricar(...)},
 * permitiendo que el cliente reciba una {@code Entrada} sin conocer la subclase concreta.</p>
 */
public abstract class Entrada {

    /** Identificador único de la entrada, generado en el momento de su emisión. */
    private String idEntrada;

    /** Precio base de la entrada sin adicionales. Puede ser sobreescrito por decoradores. */
    private double precioBase;

    /** Estado de validez de la entrada dentro de su ciclo de vida. */
    private EntradaEstado estado;

    /**
     * Inicializa la entrada con su precio base y la emite en estado {@code ACTIVA}.
     *
     * @param precioBase precio base de la entrada (precio de zona sin servicios extra)
     */
    public Entrada(double precioBase) {
        this.idEntrada = UUID.randomUUID().toString();
        this.precioBase = precioBase;
        this.estado = EntradaEstado.ACTIVA;
    }

    /**
     * Devuelve el precio total de la entrada.
     * Los decoradores sobreescriben este método para sumar el costo de sus servicios.
     *
     * <p>[Patrón: Decorator] - Método clave del Componente Base; los Decoradores Concretos
     * lo sobreescriben con {@code super.getPrecioTotal() + costoServicio}.</p>
     *
     * @return precio total de la entrada (base + servicios adicionales si aplica)
     */
    public double getPrecioTotal() {
        return this.precioBase;
    }

    /**
     * Devuelve la descripción textual de los servicios incluidos en la entrada.
     * Los decoradores concatenan su nombre al resultado de la entrada envuelta.
     *
     * <p>[Patrón: Decorator] - Método complementario clave para comprobantes y UI.</p>
     *
     * @return descripción de servicios incluidos
     */
    public String getDescripcionServicios() {
        return "Entrada estándar";
    }

    /** @return identificador único de la entrada */
    public String getIdEntrada() {
        return idEntrada;
    }

    /** @return estado actual de la entrada ({@code ACTIVA}, {@code USADA} o {@code ANULADA}) */
    public EntradaEstado getEstado() {
        return estado;
    }

    /**
     * Cambia el estado de validez de la entrada.
     *
     * @param estado nuevo estado de la entrada
     */
    public void setEstado(EntradaEstado estado) {
        this.estado = estado;
    }

    /**
     * Marca como vendidos los recursos físicos asociados (asientos numerados).
     * La implementación base no hace nada; {@link EntradaAsiento} la sobreescribe.
     *
     * <p>[Patrón: Decorator] - {@code EntradaDecorator} delega este llamado a la entrada
     * envuelta, garantizando que la confirmación de venta se propague a través de toda la cadena.</p>
     */
    public void confirmarVenta() {
    }

    /**
     * Libera los recursos físicos asociados (asientos), dejándolos {@code DISPONIBLE}.
     * La implementación base no hace nada; {@link EntradaAsiento} la sobreescribe.
     *
     * <p>[Requerimiento: RF-010] - Invocado cuando la compra es cancelada para liberar los
     * asientos y permitir que otros usuarios los adquieran.</p>
     */
    public void liberarRecursos() {
    }
}
