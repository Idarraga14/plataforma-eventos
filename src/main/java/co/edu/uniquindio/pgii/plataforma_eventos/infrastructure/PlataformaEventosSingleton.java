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

/**
 * Repositorio en memoria y sujeto del patrón Observer para toda la plataforma de eventos.
 *
 * <p>Implementa simultáneamente dos responsabilidades bien delimitadas:</p>
 * <ol>
 *   <li><strong>Repositorio central</strong>: almacena en listas las entidades principales
 *       ({@link Usuario}, {@link Evento}, {@link Recinto}, {@link Compra}, {@link Incidencia})
 *       y expone métodos de búsqueda por ID con semántica de fallo rápido.</li>
 *   <li><strong>Sujeto Observer</strong>: gestiona la lista de {@link EventoObserver} suscritos
 *       y los notifica ante cualquier cambio de aforo o estado de evento.</li>
 * </ol>
 *
 * <p>La instancia única se crea de forma eager (campo estático final) al cargar la clase,
 * garantizando thread-safety en el contexto de JavaFX (hilo único de la UI).</p>
 *
 * <p>[Requerimiento: RF-045] - Inicializa los datos de prueba al arrancar la aplicación
 * delegando en {@link DatosPruebaSeeder#inyectarDatos(PlataformaEventosSingleton)}.</p>
 * <p>[Patrón: Singleton] - Actúa como el <strong>Singleton</strong> del sistema;
 * garantiza que exista exactamente una instancia del repositorio en toda la JVM.</p>
 * <p>[Patrón: Observer] - Actúa como el <strong>Subject Concreto</strong>;
 * mantiene la lista de observers y los notifica a través de {@link #notificarCambio(Evento)}.</p>
 */
public class PlataformaEventosSingleton {

    /**
     * Única instancia del Singleton, creada en tiempo de carga de la clase (eager initialization).
     *
     * <p>[Patrón: Singleton] - La inicialización eager garantiza thread-safety sin sincronización
     * explícita, aprovechando las garantías del class loader de Java.</p>
     */
    private static final PlataformaEventosSingleton instance = new PlataformaEventosSingleton();

    /** Repositorio en memoria de todos los usuarios registrados en la plataforma. */
    private List<Usuario> usuarios = new ArrayList<>();

    /** Repositorio en memoria de todos los eventos creados (publicados y no publicados). */
    private List<Evento> eventos = new ArrayList<>();

    /** Repositorio en memoria de todos los recintos físicos configurados. */
    private List<Recinto> recintos = new ArrayList<>();

    /** Repositorio en memoria de todas las compras realizadas (todos los estados). */
    private List<Compra> compras = new ArrayList<>();

    /** Repositorio en memoria de todas las incidencias registradas por el administrador. */
    private List<Incidencia> incidencias = new ArrayList<>();

    /**
     * Lista de observers suscritos que reciben notificaciones ante cambios de aforo o estado.
     *
     * <p>[Patrón: Observer] - Los controladores de JavaFX (ej. {@code AdminDashboardController})
     * se registran aquí para mantener la UI sincronizada con el estado del repositorio.</p>
     */
    private List<EventoObserver> observadores = new ArrayList<>();

    /**
     * Constructor privado: previene la instanciación externa y lanza la carga de datos.
     *
     * <p>[Patrón: Singleton] - El constructor privado es el mecanismo que impide
     * la creación de instancias adicionales.</p>
     */
    private PlataformaEventosSingleton() {
        // RF-045: carga inicial de datos de prueba al arrancar la aplicación
        inicializarDatosPrueba();
    }

    /**
     * Devuelve la única instancia del repositorio.
     *
     * <p>[Patrón: Singleton] - Punto de acceso global. Todas las facades y strategies
     * obtienen el repositorio a través de este método.</p>
     *
     * @return instancia única de {@code PlataformaEventosSingleton}
     */
    public static PlataformaEventosSingleton getInstance() {
        return instance;
    }

    /** @return lista mutable de usuarios registrados en el sistema */
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /** @return lista mutable de eventos creados en el sistema */
    public List<Evento> getEventos() {
        return eventos;
    }

    /** @return lista mutable de recintos físicos configurados */
    public List<Recinto> getRecintos() {
        return recintos;
    }

    /** @return lista mutable de todas las compras del sistema */
    public List<Compra> getCompras() {
        return compras;
    }

    /** @return lista mutable de todas las incidencias registradas */
    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    /**
     * Delega la población inicial de datos al seeder masivo.
     *
     * <p>[Requerimiento: RF-045] - Garantiza que el sistema arranque con datos de prueba
     * representativos que cubran todos los patrones y estados del dominio.</p>
     */
    private void inicializarDatosPrueba() {
        DatosPruebaSeeder.inyectarDatos(this);
    }

    /**
     * Busca un usuario por su ID único.
     *
     * @param idUsuario identificador del usuario
     * @return el {@link Usuario} encontrado
     * @throws NoSuchElementException si no existe ningún usuario con ese ID
     */
    public Usuario buscarUsuario(String idUsuario) {
        return this.usuarios.stream()
                .filter(usuario -> usuario.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    /**
     * Busca un evento por su ID único.
     *
     * @param idEvento identificador del evento
     * @return el {@link Evento} encontrado
     * @throws NoSuchElementException si no existe ningún evento con ese ID
     */
    public Evento buscarEvento(String idEvento) {
        return this.eventos.stream()
                .filter(evento -> evento.getIdEvento().equals(idEvento))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Evento no encontrado"));
    }

    /**
     * Registra un observer para recibir notificaciones de cambios de aforo.
     * Previene duplicados verificando si ya está en la lista.
     *
     * <p>[Patrón: Observer] - Implementación del método {@code suscribir} del Subject.</p>
     *
     * @param obs observer a registrar
     */
    public void suscribir(EventoObserver obs) {
        if (!observadores.contains(obs)) observadores.add(obs);
    }

    /**
     * Elimina un observer previamente registrado.
     *
     * <p>[Patrón: Observer] - Invocado cuando el controlador suscrito es destruido.</p>
     *
     * @param obs observer a eliminar de la lista de suscriptores
     */
    public void desuscribir(EventoObserver obs) {
        observadores.remove(obs);
    }

    /**
     * Notifica a todos los observers registrados sobre un cambio en el aforo o estado de un evento.
     *
     * <p>[Patrón: Observer] - Invocado por las facades tras cualquier operación que modifique
     * el inventario de asientos o el estado de publicación de un evento.</p>
     *
     * @param evento el evento que fue modificado
     */
    public void notificarCambio(Evento evento) {
        // Notificar a todos los observadores suscritos (tipicamente vistas de JavaFX)
        for (EventoObserver obs : observadores) {
            obs.onAforoActualizado(evento);
        }
    }
}
