package co.edu.uniquindio.pgii.plataforma_eventos.application.observer;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;

// El publicador (el singleton implementará esto)
public interface EventoSubject {
    void suscribir(EventoObserver observer);
    void desuscribir(EventoObserver observer);
    void notificarCambioAforo(Evento evento);
}
