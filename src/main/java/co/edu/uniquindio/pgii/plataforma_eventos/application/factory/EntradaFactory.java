package co.edu.uniquindio.pgii.plataforma_eventos.application.factory;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

public class EntradaFactory {
    private EntradaFactory() {
        throw new UnsupportedOperationException("Clase Factory no instanciable");
    }

    /** Variante A: entrada de aforo libre (sin silla numerada). */
    public static Entrada fabricar(Zona zona) {
        if (zona == null) {
            throw new IllegalArgumentException("La zona es obligatoria para fabricar una entrada.");
        }
        return new EntradaZona(zona, zona.getPrecioBase());
    }

    /** Variante B: entrada numerada — recibe el inventario comercial del evento. */
    public static Entrada fabricar(Zona zona, AsientoEvento asientoEvento) {
        if (zona == null || asientoEvento == null) {
            throw new IllegalArgumentException("Zona y AsientoEvento son obligatorios para una entrada numerada.");
        }
        return new EntradaAsiento(zona, asientoEvento, zona.getPrecioBase());
    }
}
