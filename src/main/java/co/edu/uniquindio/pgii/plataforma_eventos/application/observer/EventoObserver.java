package co.edu.uniquindio.pgii.plataforma_eventos.application.observer;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;

/**
 * Interfaz del suscriptor en el patrón Observer para notificaciones de cambio de aforo.
 *
 * <p>Los controladores de JavaFX que necesitan actualizarse en tiempo real ante cambios
 * en el inventario de un evento implementan esta interfaz. El sujeto ({@code PlataformaEventosSingleton})
 * invoca {@link #onAforoActualizado(Evento)} en todos los observadores registrados cada vez
 * que una compra modifica el estado de los asientos o el aforo disponible.</p>
 *
 * <p>[Requerimiento: RF-049] - Implementación explícita del patrón de comportamiento
 * <strong>Observer</strong>, exigido como requerimiento no funcional del proyecto.</p>
 * <p>[Patrón: Observer] - Actúa como la interfaz <strong>Observer (Listener)</strong>.
 * El implementador concreto es {@code AdminDashboardController}; el sujeto es
 * {@code PlataformaEventosSingleton} a través de la interfaz {@link EventoSubject}.</p>
 */
public interface EventoObserver {

    /**
     * Notificación recibida cuando el aforo o estado de un evento ha cambiado.
     *
     * <p>En JavaFX este método se ejecuta en el hilo de aplicación mediante
     * {@code Platform.runLater()} para garantizar la seguridad de hilo en la UI.</p>
     *
     * @param evento el evento cuyo aforo o estado fue modificado
     */
    void onAforoActualizado(Evento evento);
}
