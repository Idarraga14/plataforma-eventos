package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Subdivide un {@link Recinto} en sectores con precio y capacidad diferenciados.
 *
 * <p>Una zona puede ser de acceso libre (sin asientos numerados, ej. "Cancha General")
 * o numerada (con objetos {@link Asiento} individuales, ej. "Platea"). El tipo de
 * asignación es determinado por la {@code AsignacionStrategy} correspondiente al comparar
 * si {@code asientos} está vacío o contiene elementos.</p>
 *
 * <p>[Requerimiento: RF-003] - Las zonas definen los sectores entre los que el usuario
 * elige durante el flujo de compra; el precio base de la zona es el punto de partida para
 * el cálculo del precio de cada {@code Entrada}.</p>
 * <p>[Requerimiento: RF-013] - El administrador crea y configura zonas dentro de cada
 * recinto desde el módulo de administración de recintos.</p>
 * <p>[Patrón: Strategy] - La lista de asientos es consultada por {@code AsignacionPorAsientoStrategy}
 * para localizar la silla física y obtener el {@link AsientoEvento} correspondiente en el inventario
 * del evento activo.</p>
 */
public class Zona {

    /** Identificador único de la zona, generado al crearla. */
    private String idZona;

    /** Nombre descriptivo de la zona (ej. "Cancha General", "Platea", "Palco VIP"). */
    private String nombre;

    /**
     * Capacidad total de personas en la zona.
     * Para zonas numeradas equivale al número de {@link Asiento} registrados.
     */
    private int capacidad;

    /** Precio base por entrada en esta zona, antes de aplicar decoradores. */
    private double precioBase;

    /**
     * Lista de asientos físicos de la zona.
     * Vacía si la zona es de acceso libre (sin numeración).
     */
    private List<Asiento> asientos;

    /**
     * Crea una nueva zona con los parámetros de configuración dados.
     *
     * @param nombre     nombre descriptivo de la zona
     * @param capacidad  aforo total de la zona
     * @param precioBase precio base por entrada en esta zona
     */
    public Zona(String nombre, int capacidad, double precioBase) {
        this.idZona = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.asientos = new ArrayList<>();
    }

    /** @return identificador único de la zona */
    public String getIdZona() {
        return idZona;
    }

    /** @return nombre descriptivo de la zona */
    public String getNombre() {
        return nombre;
    }

    /** @return aforo total de la zona */
    public int getCapacidad() {
        return capacidad;
    }

    /** @return precio base por entrada antes de aplicar decoradores de servicio */
    public double getPrecioBase() {
        return precioBase;
    }

    /**
     * Devuelve la lista de asientos físicos de la zona.
     * Vacía para zonas de acceso libre.
     *
     * @return lista mutable de {@link Asiento}
     */
    public List<Asiento> getAsientos() {
        return asientos;
    }

    /**
     * Registra un asiento físico dentro de la zona.
     *
     * <p>[Requerimiento: RF-013] - Invocado durante la configuración del recinto para
     * construir la plantilla numerada de la zona.</p>
     *
     * @param asiento asiento físico a registrar
     */
    public void agregarAsiento(Asiento asiento) {
        this.asientos.add(asiento);
    }
}
