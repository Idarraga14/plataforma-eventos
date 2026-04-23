package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure;

import co.edu.uniquindio.pgii.plataforma_eventos.application.observer.EventoObserver;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.MedioPago;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

import java.time.LocalDateTime;
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

    // Carga de datos RF-045
    private void inicializarDatosPrueba() {
        System.out.println("[SISTEMA] Inicializando base de datos en memoria...");

        // ==========================================
        // 1. POBLAR USUARIOS
        // ==========================================
        // (id, nombre, correo, telefono, password, esAdmin) -> Ajusta el constructor si es distinto
        Usuario admin = new Usuario("Administrador Principal", "admin@eventos.com", "3000000000", "admin123", true);
        Usuario cliente1 = new Usuario("Juan Pérez", "juan@gmail.com", "3111111111", "1234", false);
        Usuario cliente2 = new Usuario("María Gómez", "maria@gmail.com", "3222222222", "1234", false);

        // Agregamos un medio de pago de prueba para Juan
        cliente1.getMediosPago().add(new MedioPago("Juan Perez", "23232324242"));

        this.usuarios.add(admin);
        this.usuarios.add(cliente1);
        this.usuarios.add(cliente2);

        // ==========================================
        // 2. POBLAR RECINTOS Y ZONAS
        // ==========================================

        // --- Recinto 1: ESTADIO (Eventos masivos, zonas sin asientos específicos) ---
        Recinto estadio = new Recinto("Estadio Centenario", "Cra 18 # 12-00", "Armenia");

        Zona gramilla = new Zona("Gramilla General", 15000, 120000.0);
        Zona vip = new Zona("VIP (Frente a Tarima)", 2000, 350000.0);
        // El estadio no tiene objetos "Asiento", la gente entra hasta llenar la capacidad.

        estadio.getZonas().add(gramilla);
        estadio.getZonas().add(vip);
        this.recintos.add(estadio);

        // --- Recinto 2: TEATRO (Eventos numerados, estrategia de asientos) ---
        Recinto teatro = new Recinto("Teatro Azul", "Plaza Bolívar", "Armenia");

        Zona platea = new Zona("Platea Central", 100, 85000.0);
        Zona balcon = new Zona("Balcón", 50, 45000.0);

        // Generamos sillas para la Platea Central (10 filas x 10 columnas)
        char[] filasPlatea = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
        for (char fila : filasPlatea) {
            for (int i = 1; i <= 10; i++) {
                Asiento silla = new Asiento(fila, i);
                silla.setEstado(AsientoEstado.DISPONIBLE);
                platea.getAsientos().add(silla);
            }
        }

        // Bloqueamos un par de sillas para simular mantenimiento
        platea.getAsientos().get(0).setEstado(AsientoEstado.BLOQUEADO); // A1 bloqueada
        platea.getAsientos().get(1).setEstado(AsientoEstado.BLOQUEADO); // A2 bloqueada

        teatro.getZonas().add(platea);
        teatro.getZonas().add(balcon);
        this.recintos.add(teatro);

        // ==========================================
        // 3. POBLAR EVENTOS (Usando tu Patrón Builder)
        // ==========================================

        // Evento 1: Concierto (Usa Estadio)
        Evento concierto = new Evento.EventoBuilder()
                .conNombre("Rock al Parque - Edición Quindío")
                .conDescripcion("El mejor festival de rock nacional llega a la ciudad. Apertura de puertas a las 4:00 PM.")
                .paraLaFecha(LocalDateTime.now().plusDays(30))
                .enCiudad("Armenia")
                .deCategoria(EventoCategoria.CONCIERTO)
                .enRecinto(estadio)
                .build();
        concierto.setEstado(EventoEstado.PUBLICADO);

        // Evento 2: Teatro (Usa Teatro Azul)
        Evento obraTeatro = new Evento.EventoBuilder()
                .conNombre("El Cascanueces - Ballet Nacional")
                .conDescripcion("Un espectáculo para toda la familia. Requiere selección de asiento.")
                .paraLaFecha(LocalDateTime.now().plusDays(15))
                .enCiudad("Armenia")
                .deCategoria(EventoCategoria.TEATRO)
                .enRecinto(teatro)
                .build();
        obraTeatro.setEstado(EventoEstado.PUBLICADO);

        // Evento 3: Evento Pausado (Para probar los filtros del Facade)
        Evento festival = new Evento.EventoBuilder()
                .conNombre("Festival Gastronómico")
                .conDescripcion("Pronto publicaremos más detalles. Evento no disponible para compra aún.")
                .paraLaFecha(LocalDateTime.now().plusDays(60))
                .enCiudad("Armenia")
                .deCategoria(EventoCategoria.CONFERENCIA)
                .enRecinto(estadio)
                .build();
        festival.setEstado(EventoEstado.PAUSADO);

        this.eventos.add(concierto);
        this.eventos.add(obraTeatro);
        this.eventos.add(festival);

        System.out.println("[SISTEMA] Carga de datos de prueba finalizada. Eventos disponibles: " + this.eventos.size());
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
