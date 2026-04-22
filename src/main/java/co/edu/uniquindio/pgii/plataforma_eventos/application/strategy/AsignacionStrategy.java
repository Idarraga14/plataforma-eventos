package co.edu.uniquindio.pgii.plataforma_eventos.application.strategy;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;

public interface AsignacionStrategy {
    // Retorna la Entrada creada si hay cupo, o lanza excepción si está agotado
    Entrada asignarCupo(Evento evento, String idZona, String idAsientoSolicitado);
}
