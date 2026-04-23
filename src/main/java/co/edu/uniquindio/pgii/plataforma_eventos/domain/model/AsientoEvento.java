package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;

/**
 * Inventario comercial de una silla para un evento concreto.
 * El Recinto guarda la plantilla física (Asiento); el Evento guarda esta proyección.
 */
public class AsientoEvento {

    private final Asiento asientoFisico;
    private AsientoEstado estado;

    public AsientoEvento(Asiento asientoFisico) {
        this.asientoFisico = asientoFisico;
        this.estado = asientoFisico.isHabilitadoFisicamente()
                ? AsientoEstado.DISPONIBLE
                : AsientoEstado.BLOQUEADO;
    }

    public String getIdAsiento() {
        return asientoFisico.getIdAsiento();
    }

    public Asiento getAsientoFisico() {
        return asientoFisico;
    }

    public AsientoEstado getEstado() {
        return estado;
    }

    public void setEstado(AsientoEstado estado) {
        this.estado = estado;
    }

    public void bloquear() {
        if (estado == AsientoEstado.VENDIDO) {
            throw new IllegalStateException("No se puede bloquear un asiento vendido.");
        }
        this.estado = AsientoEstado.BLOQUEADO;
    }

    public void liberar() {
        if (estado != AsientoEstado.BLOQUEADO) {
            throw new IllegalStateException("Solo se pueden liberar asientos bloqueados.");
        }
        this.estado = AsientoEstado.DISPONIBLE;
    }

    public void vender() {
        if (estado != AsientoEstado.BLOQUEADO) {
            throw new IllegalStateException("Solo se pueden vender asientos bloqueados (reservados).");
        }
        this.estado = AsientoEstado.VENDIDO;
    }
}
