package co.edu.uniquindio.pgii.plataforma_eventos.application.strategy;

import co.edu.uniquindio.pgii.plataforma_eventos.application.factory.EntradaFactory;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.PlataformaEventosSingleton;

/**
 * Estrategia de asignación de cupos para zonas de acceso libre (sin numeración de asientos).
 *
 * <p>Verifica la disponibilidad consultando el repositorio global de compras y contando
 * cuántas entradas activas (estado no cancelado ni reembolsado) hay en la zona solicitada.
 * Si hay cupo, genera una {@code EntradaZona} mediante la {@code EntradaFactory}.</p>
 *
 * <p>[Requerimiento: RF-003] - Activa cuando el usuario elige una zona de aforo libre
 * (ej. "Cancha General"); el {@code idAsientoSolicitado} es ignorado en esta implementación.</p>
 * <p>[Patrón: Strategy] - Actúa como <strong>Estrategia Concreta</strong> para zonas libres.
 * Seleccionada por {@code PlataformaFacadeImpl} cuando {@code idAsientos} está vacío.</p>
 * <p>[Patrón: Factory] - Delega la creación de la entrada a {@code EntradaFactory#fabricar(Zona)}
 * para desacoplar la instanciación del tipo concreto de entrada.</p>
 */
public class AsignacionPorZonaStrategy implements AsignacionStrategy {

    /**
     * Valida el aforo disponible en la zona libre y, si hay cupo, retorna una {@code EntradaZona}.
     *
     * <p>Pasos de ejecución:</p>
     */
    @Override
    public Entrada asignarCupo(Evento evento, String idZona, String idAsientoSolicitado) {
        // 1. Buscamos la zona dentro del recinto del evento
        Zona zonaDestino = evento.getRecinto().getZonas().stream()
                .filter(zona -> zona.getIdZona().equals(idZona))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada en el evento"));

        // 2. Calculamos el aforo actual consultando el Singleton.
        //    Las órdenes CREADA/PAGADA/CONFIRMADA reservan cupo; las CANCELADA/REEMBOLSADA no.
        long entradasVendidas = PlataformaEventosSingleton.getInstance().getCompras().stream()
                .filter(compra -> compra.getEvento().getIdEvento().equals(evento.getIdEvento()))
                .filter(compra -> compra.getEstadoEnum() != CompraEstado.CANCELADA
                        && compra.getEstadoEnum() != CompraEstado.REEMBOLSADA)
                .flatMap(compra -> compra.getEntradas().stream())
                .filter(entrada -> entrada instanceof EntradaZona &&
                        ((EntradaZona) entrada).getZona().getIdZona().equals(idZona))
                .count();

        // 3. Regla de negocio: la zona está agotada si las entradas activas igualan o superan su capacidad
        if (entradasVendidas >= zonaDestino.getCapacidad()) {
            throw new IllegalStateException("La zona " + zonaDestino.getNombre() + " está totalmente agotada.");
        }

        // 4. Hay cupo disponible: delegar la creación a la Factory y retornar la entrada base
        return EntradaFactory.fabricar(zonaDestino);
    }
}
