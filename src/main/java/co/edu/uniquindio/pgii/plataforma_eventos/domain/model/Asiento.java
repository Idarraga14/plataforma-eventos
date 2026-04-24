package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.UUID;

/**
 * Representa la plantilla física de una silla dentro de una {@link Zona} de un {@link Recinto}.
 *
 * <p>Esta clase modela el <strong>activo fijo</strong> del recinto: su localización (fila y número)
 * y si la silla está físicamente operativa. No contiene ningún dato de disponibilidad comercial
 * ni de venta; esa responsabilidad recae en {@link AsientoEvento}, que proyecta este activo
 * sobre un evento concreto.</p>
 *
 * <p>[Requerimiento: RF-013] - El administrador gestiona los recintos y sus asientos físicos
 * desde el módulo de administración. Cada {@code Asiento} forma parte de la plantilla
 * inmutable del {@code Recinto} que persiste entre eventos.</p>
 * <p>[Requerimiento: RF-018] - El campo {@code habilitadoFisicamente} permite al administrador
 * marcar una silla como fuera de servicio (ej. rota, en mantenimiento). Los eventos creados
 * a partir de ese momento inicializarán esa silla como {@code BLOQUEADO} en su inventario.</p>
 */
public class Asiento {

    /** Identificador único inmutable generado al crear el asiento físico. */
    private final String idAsiento;

    /** Fila del asiento dentro de la zona (ej. 'A', 'B', 'C'). */
    private final char fila;

    /** Número de columna del asiento dentro de su fila. */
    private final int numero;

    /**
     * Indica si el asiento está físicamente operativo.
     * Si es {@code false}, los nuevos {@link AsientoEvento} arrancan en {@code BLOQUEADO}.
     */
    private boolean habilitadoFisicamente;

    /**
     * Crea un asiento físico en la fila y número indicados, habilitado por defecto.
     *
     * @param fila   letra de fila dentro de la zona
     * @param numero número de columna dentro de la fila
     */
    public Asiento(char fila, int numero) {
        this.idAsiento = UUID.randomUUID().toString();
        this.fila = fila;
        this.numero = numero;
        this.habilitadoFisicamente = true;
    }

    /** @return identificador único del asiento físico */
    public String getIdAsiento() {
        return idAsiento;
    }

    /** @return letra de fila del asiento (ej. 'A') */
    public char getFila() {
        return fila;
    }

    /** @return número de columna del asiento */
    public int getNumero() {
        return numero;
    }

    /**
     * Devuelve la etiqueta compacta del asiento (ej. "A3") usada en UI y comprobantes.
     *
     * @return cadena con el formato {@code filaNumero}
     */
    public String getSalida() {
        return String.format("%c%d", fila, numero);
    }

    /** @return {@code true} si el asiento está físicamente operativo */
    public boolean isHabilitadoFisicamente() {
        return habilitadoFisicamente;
    }

    /**
     * Habilita o inhabilita el asiento físicamente.
     *
     * <p>[Requerimiento: RF-018] - Usado por el administrador para marcar sillas rotas
     * o en mantenimiento; afecta el estado inicial de los inventarios en nuevos eventos.</p>
     *
     * @param habilitadoFisicamente {@code true} para operativo, {@code false} para fuera de servicio
     */
    public void setHabilitadoFisicamente(boolean habilitadoFisicamente) {
        this.habilitadoFisicamente = habilitadoFisicamente;
    }
}
