package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure;

import co.edu.uniquindio.pgii.plataforma_eventos.application.observer.EventoObserver;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Incidencia;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PlataformaEventosSingleton {
    // La única instancia de la clase
    private static final PlataformaEventosSingleton instance = new PlataformaEventosSingleton();

    // Colecciones en memoria
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Evento> eventos = new ArrayList<>();
    private List<Recinto> recintos = new ArrayList<>();
    private List<Compra> compras = new ArrayList<>();
    private List<Incidencia> incidencias = new ArrayList<>();

    private List<EventoObserver> observadores = new ArrayList<>();

    private PlataformaEventosSingleton() {
        // RF-045: Ejecutamos la carga de datos inicial
        inicializarDatosPrueba();
    }

    public static PlataformaEventosSingleton getInstance() {
        return instance;
    }

    // Métodos getters
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public List<Recinto> getRecintos() {
        return recintos;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    // Carga de datos RF-045 — delega en el seeder masivo
    private void inicializarDatosPrueba() {
        DatosPruebaSeeder.inyectarDatos(this);
    }

    public Usuario buscarUsuario(String idUsuario) {
        return this.usuarios.stream()
                .filter(usuario -> usuario.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    public Evento buscarEvento(String idEvento) {
        return this.eventos.stream()
                .filter(evento -> evento.getIdEvento().equals(idEvento))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado"));
    }

    public void suscribir(EventoObserver obs) {
        if (!observadores.contains(obs)) observadores.add(obs);
    }

    public void desuscribir(EventoObserver obs) {
        observadores.remove(obs);
    }

    public void notificarCambio(Evento evento) {
        // Notificamos a todos los interesados (Vistas de JavaFX)
        for (EventoObserver obs : observadores) {
            obs.onAforoActualizado(evento);
        }
    }
}
