package co.edu.uniquindio.pgii.plataforma_eventos.application.observer;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;

// El suscriptor (el controlador de JavaFX implementará esto)
public interface EventoObserver {
    void onAforoActualizado(Evento evento);
}
