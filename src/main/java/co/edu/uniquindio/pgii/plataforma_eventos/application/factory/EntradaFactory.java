package co.edu.uniquindio.pgii.plataforma_eventos.application.factory;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

/**
 * Fábrica estática que centraliza la creación de instancias de {@link Entrada}.
 *
 * <p>Ofrece dos variantes de fabricación según el tipo de asignación requerida:
 * una para zonas de acceso libre ({@link EntradaZona}) y otra para asientos
 * numerados ({@link EntradaAsiento}). Al centralizar la lógica de instanciación
 * se desacopla a los clientes (Strategies) de los tipos concretos de entrada.</p>
 *
 * <p>[Requerimiento: RF-003] - Responsable de emitir las entradas que acreditan el
 * acceso a un evento, ya sea para zona de aforo libre o para asiento numerado.</p>
 * <p>[Requerimiento: RF-049] - Implementación explícita del patrón creacional
 * <strong>Factory Method</strong> (variante Factory de Clase Utilitaria), exigido
 * como requerimiento no funcional del proyecto.</p>
 * <p>[Patrón: Factory] - Actúa como <strong>Creator</strong> centralizado.
 * Los clientes directos son {@link AsignacionPorZonaStrategy} y
 * {@link AsignacionPorAsientoStrategy}; el resultado puede ser inmediatamente
 * envuelto en decoradores por {@code PlataformaFacadeImpl#aplicarExtras}.</p>
 */
public class EntradaFactory {

    /**
     * Constructor privado: la clase no debe instanciarse; sólo expone métodos estáticos.
     */
    private EntradaFactory() {
        throw new UnsupportedOperationException("Clase Factory no instanciable");
    }

    /**
     * Variante A — Crea una entrada de acceso libre para una zona sin numeración.
     *
     * <p>Usada por {@link AsignacionPorZonaStrategy} para zonas de aforo general.</p>
     *
     * @param zona zona de acceso libre; su {@code precioBase} se aplica directamente
     * @return nueva instancia de {@link EntradaZona} en estado {@code ACTIVA}
     * @throws IllegalArgumentException si {@code zona} es nula
     */
    public static Entrada fabricar(Zona zona) {
        if (zona == null) {
            throw new IllegalArgumentException("La zona es obligatoria para fabricar una entrada.");
        }
        return new EntradaZona(zona, zona.getPrecioBase());
    }

    /**
     * Variante B — Crea una entrada numerada vinculada al inventario comercial del evento.
     *
     * <p>Usada por {@link AsignacionPorAsientoStrategy} cuando el usuario elige un asiento
     * específico. Recibe el {@link AsientoEvento} ya marcado como {@code BLOQUEADO}.</p>
     *
     * @param zona          zona del recinto donde se ubica el asiento; su {@code precioBase} se aplica
     * @param asientoEvento inventario comercial del asiento reservado para este evento
     * @return nueva instancia de {@link EntradaAsiento} en estado {@code ACTIVA}
     * @throws IllegalArgumentException si alguno de los parámetros es nulo
     */
    public static Entrada fabricar(Zona zona, AsientoEvento asientoEvento) {
        if (zona == null || asientoEvento == null) {
            throw new IllegalArgumentException("Zona y AsientoEvento son obligatorios para una entrada numerada.");
        }
        return new EntradaAsiento(zona, asientoEvento, zona.getPrecioBase());
    }
}
