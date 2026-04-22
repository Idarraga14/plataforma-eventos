package co.edu.uniquindio.pgii.plataforma_eventos.application.factory;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

// Patrón de Static Factory Methods apoyado en sobrecarga
public class EntradaFactory {
    // 1. Constructor privado: Una fábrica es una utilidad, no debe instanciarse.
    private EntradaFactory() {
        throw new UnsupportedOperationException("Clase Factory no instanciable");
    }

    // 2. Variante A: Fabricar entrada General (Firma solo exige Zona)
    public static Entrada fabricar(Zona zona) {
        if (zona == null) {
            throw new IllegalArgumentException("La zona es obligatoria para fabricar una entrada.");
        }
        // Retorna la subclase específica
        return new EntradaZona(zona, zona.getPrecioBase());
    }

    // 3. Variante B: Fabricar entrada Numerada (Firma exige Zona y Asiento)
    public static Entrada fabricar(Zona zona, Asiento asiento) {
        if (zona == null || asiento == null) {
            throw new IllegalArgumentException("Zona y Asiento son obligatorios para una entrada numerada.");
        }
        // Retorna la subclase específica
        return new EntradaAsiento(zona, asiento, zona.getPrecioBase());
    }
}
