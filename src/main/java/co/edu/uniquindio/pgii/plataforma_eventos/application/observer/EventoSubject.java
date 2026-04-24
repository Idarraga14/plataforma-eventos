package co.edu.uniquindio.pgii.plataforma_eventos.application.observer;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;

/**
 * Interfaz del sujeto (publicador) en el patrón Observer para notificaciones de eventos.
 *
 * <p>Define el contrato que debe cumplir cualquier sujeto capaz de gestionar suscriptores
 * {@link EventoObserver} y emitir notificaciones de cambio. En esta plataforma el único
 * implementador es {@code PlataformaEventosSingleton}, que actúa simultáneamente como
 * repositorio central y sujeto de notificaciones.</p>
 *
 * <p>[Patrón: Observer] - Actúa como la interfaz <strong>Subject (Observable)</strong>.
 * Permite desacoplar al emisor de los cambios (facades que modifican compras/eventos)
 * de los receptores que reaccionan actualizando la UI (controladores de JavaFX).</p>
 */
public interface EventoSubject {

    /**
     * Registra un observador para recibir notificaciones de cambio de aforo.
     * Si el observador ya está registrado, no se duplica.
     *
     * @param observer controlador o componente interesado en los cambios de aforo
     */
    void suscribir(EventoObserver observer);

    /**
     * Elimina un observador previamente registrado.
     * Invocado cuando el controlador es destruido (cierre de ventana, navegación).
     *
     * @param observer observador a eliminar de la lista de suscriptores
     */
    void desuscribir(EventoObserver observer);

    /**
     * Notifica a todos los observadores suscritos sobre un cambio en el aforo del evento dado.
     *
     * @param evento evento cuyo aforo o estado fue modificado recientemente
     */
    void notificarCambioAforo(Evento evento);
}
