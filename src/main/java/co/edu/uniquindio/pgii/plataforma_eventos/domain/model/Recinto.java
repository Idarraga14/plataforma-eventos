package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Modela la plantilla física de un lugar de espectáculos que puede albergar múltiples eventos.
 *
 * <p>El {@code Recinto} actúa como <strong>Aggregate Root</strong> de la jerarquía física
 * {@code Recinto → Zona → Asiento}. Su estructura no cambia entre eventos; los cambios de
 * disponibilidad se registran en los inventarios comerciales de cada {@link Evento}
 * (a través de {@link AsientoEvento}).</p>
 *
 * <p>[Requerimiento: RF-013] - El administrador crea, edita y elimina recintos desde el
 * módulo de administración, incluyendo la definición de sus zonas y la capacidad de cada una.</p>
 * <p>[Requerimiento: RF-012] - Al crear un evento, el administrador asocia un recinto; el
 * constructor del {@code Evento} (via {@link Evento.EventoBuilder#enRecinto(Recinto)}) proyecta
 * automáticamente la plantilla física en el inventario comercial del nuevo evento.</p>
 * <p>[Patrón: Builder] - Es el objeto que el {@link Evento.EventoBuilder} recibe mediante
 * {@code enRecinto(Recinto)} para construir un evento con todos sus asientos inicializados.</p>
 */
public class Recinto {

    /** Identificador único del recinto, generado al crearlo. */
    private String idRecinto;

    /** Nombre comercial del recinto (ej. "Estadio Monumental", "Teatro Ópera"). */
    private String nombre;

    /** Dirección física del recinto. */
    private String direccion;

    /** Ciudad donde se ubica el recinto. */
    private String ciudad;

    /** Lista de zonas que subdividen el recinto. */
    private List<Zona> zonas;

    /**
     * Crea un recinto con su ubicación geográfica.
     *
     * @param nombre    nombre comercial del recinto
     * @param direccion dirección física
     * @param ciudad    ciudad donde se ubica
     */
    public Recinto(String nombre, String direccion, String ciudad) {
        this.idRecinto = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.zonas = new ArrayList<>();
    }

    /**
     * Devuelve la lista de zonas del recinto.
     * El {@link Evento} recorre esta lista durante la inicialización de su inventario.
     *
     * @return lista mutable de {@link Zona}
     */
    public List<Zona> getZonas() {
        return zonas;
    }

    /** @return ciudad donde se ubica el recinto */
    public String getCiudad() {
        return ciudad;
    }

    /** @return dirección física del recinto */
    public String getDireccion() {
        return direccion;
    }

    /** @return nombre comercial del recinto */
    public String getNombre() {
        return nombre;
    }

    /** @return identificador único del recinto */
    public String getIdRecinto() {
        return idRecinto;
    }
}
