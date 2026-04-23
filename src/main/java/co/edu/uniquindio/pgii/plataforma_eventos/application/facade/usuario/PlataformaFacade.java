package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;

import java.util.List;

public interface PlataformaFacade {

    Usuario login(String correo, String password);

    void actualizarPerfil(Usuario usuario);

    List<Evento> obtenerEventosDisponibles();

    int obtenerCuposDisponibles(String idEvento, String idZona);

    // Proceso principal que usa Strategy, Factory, State y Adapter
    void realizarCompra(String idUsuario, String idEvento, String idZona,
                        String idAsiento, List<String> extras,
                        String numTarjeta, String cvv);
}
