package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.observer.EventoObserver;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.AccesoPreferencialDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.MerchandisingDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.ParqueaderoDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.PaqueteVIPDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.SeguroCancelacionDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ReporteOperativo;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.IncidenciaEntidadAfectada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Incidencia;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.PlataformaEventosSingleton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Implementación concreta de {@link AdministracionFacade} para los casos de uso del administrador.
 *
 * <p>Coordina todos los subsistemas de gestión administrativa: eventos (usando el Builder),
 * usuarios, recintos/zonas/asientos (tanto gestión física como por evento), compras (State),
 * incidencias, métricas del dashboard (Observer) y reportes operativos.</p>
 *
 * <p>[Patrón: Facade] - Actúa como la <strong>Implementación Concreta de la Fachada Admin</strong>.
 * Los controladores de la UI admin nunca dependen de esta clase directamente.</p>
 * <p>[Patrón: Builder] - {@link #crearEvento} construye el agregado {@code Evento} mediante
 * {@code Evento.EventoBuilder}, garantizando la validación de precondiciones.</p>
 * <p>[Patrón: State] - {@link #reembolsarCompra} y {@link #confirmarCompra} delegan en
 * el estado concreto actual de la {@code Compra}.</p>
 * <p>[Patrón: Observer] - {@link #registrarObserver}/{@link #desregistrarObserver} delegan en
 * el sujeto {@code PlataformaEventosSingleton} para gestionar las suscripciones del Dashboard.</p>
 * <p>[Patrón: Decorator] - Los helpers privados {@code contiene*(Entrada)} inspeccionan la cadena
 * de decoradores para calcular ingresos por servicio adicional en métricas y reportes.</p>
 */
public class AdministracionFacadeImpl implements AdministracionFacade {

    /** Repositorio singleton en memoria con todos los datos de la plataforma. */
    private final PlataformaEventosSingleton plat = PlataformaEventosSingleton.getInstance();

    // === EVENTOS ===

    /**
     * Construye un nuevo evento usando el {@link co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento.EventoBuilder}
     * y lo añade al repositorio.
     *
     * <p>[Requerimiento: RF-012] / [Patrón: Builder] - El Builder valida precondiciones antes de
     * crear la instancia e inicializa automáticamente el inventario de asientos del recinto.</p>
     */
    @Override
    public Evento crearEvento(String nombre, EventoCategoria categoria, String descripcion,
                              String ciudad, LocalDateTime fecha, String idRecinto) {
        Recinto recinto = buscarRecinto(idRecinto);
        Evento evento = new Evento.EventoBuilder()
                .conNombre(nombre)
                .deCategoria(categoria)
                .conDescripcion(descripcion == null ? "" : descripcion)
                .enCiudad(ciudad)
                .paraLaFecha(fecha)
                .enRecinto(recinto)
                .build();
        plat.getEventos().add(evento);
        return evento;
    }

    @Override
    public void actualizarEstadoEvento(String idEvento, EventoEstado nuevoEstado) {
        Evento evento = plat.buscarEvento(idEvento);
        evento.setEstado(nuevoEstado);
        plat.notificarCambio(evento);
    }

    @Override
    public void eliminarEvento(String idEvento) {
        boolean tieneCompras = plat.getCompras().stream()
                .anyMatch(c -> c.getEvento().getIdEvento().equals(idEvento)
                        && c.getEstadoEnum() != CompraEstado.CANCELADA
                        && c.getEstadoEnum() != CompraEstado.REEMBOLSADA);
        if (tieneCompras) {
            throw new IllegalStateException("No se puede eliminar un evento con compras activas.");
        }
        plat.getEventos().removeIf(e -> e.getIdEvento().equals(idEvento));
    }

    @Override
    public List<Evento> listarEventos() {
        return new ArrayList<>(plat.getEventos());
    }

    // === USUARIOS ===

    @Override
    public Usuario crearUsuario(String nombre, String correo, String telefono,
                                String password, boolean admin) {
        boolean existe = plat.getUsuarios().stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(correo));
        if (existe) throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        Usuario nuevo = new Usuario(nombre, correo, telefono, password, admin);
        plat.getUsuarios().add(nuevo);
        return nuevo;
    }

    @Override
    public void actualizarUsuario(String idUsuario, String nombre, String correo, String telefono) {
        Usuario u = plat.buscarUsuario(idUsuario);
        boolean correoEnUso = plat.getUsuarios().stream()
                .anyMatch(o -> !o.getIdUsuario().equals(idUsuario)
                        && o.getCorreo().equalsIgnoreCase(correo));
        if (correoEnUso) throw new IllegalArgumentException("Correo ya registrado por otro usuario.");
        u.setNombreCompleto(nombre);
        u.setCorreo(correo);
        u.setNumeroTelefono(telefono);
    }

    @Override
    public void eliminarUsuario(String idUsuario) {
        Usuario u = plat.buscarUsuario(idUsuario);
        if (u.getEsAdmin()) throw new IllegalStateException("No se puede eliminar un administrador.");
        plat.getUsuarios().remove(u);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(plat.getUsuarios());
    }

    // === RECINTOS / ZONAS / ASIENTOS ===

    /**
     * Crea un nuevo recinto físico y lo persiste en el repositorio.
     *
     * <p>[Requerimiento: RF-013] - Las zonas y asientos se añaden posteriormente
     * con {@link #agregarZona} y los métodos de configuración de asientos.</p>
     */
    @Override
    public Recinto crearRecinto(String nombre, String direccion, String ciudad) {
        Recinto r = new Recinto(nombre, direccion, ciudad);
        plat.getRecintos().add(r);
        return r;
    }

    /**
     * Elimina un recinto si no está referenciado por ningún evento existente.
     *
     * <p>[Requerimiento: RF-013] - Protege la integridad referencial del sistema.</p>
     */
    @Override
    public void eliminarRecinto(String idRecinto) {
        boolean enUso = plat.getEventos().stream()
                .anyMatch(e -> e.getRecinto().getIdRecinto().equals(idRecinto));
        if (enUso) throw new IllegalStateException("El recinto está asociado a eventos existentes.");
        plat.getRecintos().removeIf(r -> r.getIdRecinto().equals(idRecinto));
    }

    /** @return copia de la lista de recintos registrados en el sistema */
    @Override
    public List<Recinto> listarRecintos() {
        return new ArrayList<>(plat.getRecintos());
    }

    /**
     * Crea una nueva zona y la añade al recinto indicado.
     *
     * <p>[Requerimiento: RF-013] - Define un sector con su nombre, capacidad y precio base.</p>
     */
    @Override
    public Zona agregarZona(String idRecinto, String nombre, int capacidad, double precioBase) {
        Recinto r = buscarRecinto(idRecinto);
        Zona z = new Zona(nombre, capacidad, precioBase);
        r.getZonas().add(z);
        return z;
    }

    /**
     * Gestión física: marca el asiento como fuera de servicio en la plantilla del recinto.
     * Los eventos creados a partir de este momento inicializarán ese asiento como {@code BLOQUEADO}.
     *
     * <p>[Requerimiento: RF-018] - Para mantenimiento permanente o rotura del asiento.</p>
     */
    @Override
    public void bloquearAsiento(String idAsiento) {
        Asiento a = buscarAsiento(idAsiento);
        a.setHabilitadoFisicamente(false);
    }

    /**
     * Gestión física: devuelve el asiento al servicio en la plantilla del recinto.
     *
     * <p>[Requerimiento: RF-018] - Revierte el bloqueo físico del asiento.</p>
     */
    @Override
    public void habilitarAsiento(String idAsiento) {
        Asiento a = buscarAsiento(idAsiento);
        a.setHabilitadoFisicamente(true);
    }

    /**
     * Gestión comercial: bloquea el asiento sólo en el inventario del evento indicado.
     * No afecta la plantilla física del recinto ni a ningún otro evento.
     *
     * <p>[Requerimiento: RF-018] - Para reservas de organizador o mantenimiento por función.</p>
     */
    @Override
    public void bloquearAsientoEnEvento(String idEvento, String idAsiento) {
        // Gestión por evento: afecta únicamente el inventario comercial de este evento
        Evento evento = plat.buscarEvento(idEvento);
        AsientoEvento ae = evento.obtenerAsientoEvento(idAsiento);
        ae.bloquear();
    }

    /**
     * Gestión comercial: libera el asiento bloqueado sólo en el inventario del evento indicado.
     *
     * <p>[Requerimiento: RF-018] - Revierte el bloqueo por evento sin afectar otras funciones.</p>
     */
    @Override
    public void habilitarAsientoEnEvento(String idEvento, String idAsiento) {
        // Gestión por evento: afecta únicamente el inventario comercial de este evento
        Evento evento = plat.buscarEvento(idEvento);
        AsientoEvento ae = evento.obtenerAsientoEvento(idAsiento);
        ae.liberar();
    }

    // === COMPRAS ===

    /** @return copia de todas las compras registradas en el sistema */
    @Override
    public List<Compra> listarCompras() {
        return new ArrayList<>(plat.getCompras());
    }

    /**
     * Inicia el reembolso de una compra pagada o confirmada.
     *
     * <p>[Requerimiento: RF-011] / [Patrón: State] - Delega en el estado actual de la compra;
     * libera los asientos bloqueados y notifica a los observers del cambio de aforo.</p>
     */
    @Override
    public void reembolsarCompra(String idCompra) {
        Compra c = buscarCompra(idCompra);
        // 1. Transición de estado via State: PAGADA/CONFIRMADA → REEMBOLSADA
        c.reembolsar();
        // 2. Liberar los asientos (VENDIDO → DISPONIBLE en el inventario del evento)
        c.getEntradas().forEach(Entrada::liberarRecursos);
        // 3. Notificar al Dashboard del cambio de aforo
        plat.notificarCambio(c.getEvento());
    }

    /**
     * Confirma definitivamente una compra en estado PAGADA.
     *
     * <p>[Requerimiento: RF-006] / [Patrón: State] - Transición PAGADA → CONFIRMADA via State.</p>
     */
    @Override
    public void confirmarCompra(String idCompra) {
        Compra c = buscarCompra(idCompra);
        c.confirmar();
    }

    // === INCIDENCIAS ===

    /**
     * Registra una nueva incidencia y la persiste en el repositorio.
     *
     * <p>[Requerimiento: RF-019] - Vincula el problema con la entidad afectada;
     * el contador del Dashboard ({@link #contarIncidenciasAbiertas}) se actualiza automáticamente.</p>
     */
    @Override
    public Incidencia registrarIncidencia(String tipo, String descripcion,
                                          IncidenciaEntidadAfectada entidad,
                                          String idEntidad, String reportadoPor) {
        Incidencia i = new Incidencia(tipo, descripcion, entidad, idEntidad, reportadoPor);
        plat.getIncidencias().add(i);
        return i;
    }

    /** @return copia de todas las incidencias registradas en el sistema */
    @Override
    public List<Incidencia> listarIncidencias() {
        return new ArrayList<>(plat.getIncidencias());
    }

    // === MÉTRICAS ===

    @Override
    public long contarEventosPublicados() {
        return plat.getEventos().stream()
                .filter(e -> e.getEstado() == EventoEstado.PUBLICADO)
                .count();
    }

    @Override
    public double totalVentasPeriodo(LocalDate desde, LocalDate hasta) {
        return plat.getCompras().stream()
                .filter(c -> c.getEstadoEnum() == CompraEstado.PAGADA
                        || c.getEstadoEnum() == CompraEstado.CONFIRMADA)
                .filter(c -> {
                    LocalDate f = c.getFecha().toLocalDate();
                    return (desde == null || !f.isBefore(desde))
                            && (hasta == null || !f.isAfter(hasta));
                })
                .mapToDouble(Compra::getTotal)
                .sum();
    }

    @Override
    public Map<String, Double> ingresosPorMes() {
        Map<String, Double> out = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        plat.getCompras().stream()
                .filter(c -> c.getEstadoEnum() == CompraEstado.PAGADA
                        || c.getEstadoEnum() == CompraEstado.CONFIRMADA)
                .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                .forEach(c -> {
                    String mes = c.getFecha().format(fmt);
                    out.merge(mes, c.getTotal(), Double::sum);
                });
        return out;
    }

    @Override
    public Map<String, Double> ocupacionPorZona(String idEvento) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        Evento evento = plat.buscarEvento(idEvento);
        for (Zona zona : evento.getRecinto().getZonas()) {
            long ocupadas = plat.getCompras().stream()
                    .filter(c -> c.getEvento().getIdEvento().equals(idEvento))
                    .filter(c -> c.getEstadoEnum() != CompraEstado.CANCELADA
                            && c.getEstadoEnum() != CompraEstado.REEMBOLSADA)
                    .flatMap(c -> c.getEntradas().stream())
                    .map(AdministracionFacadeImpl::zonaDeEntrada)
                    .filter(z -> z != null && z.getIdZona().equals(zona.getIdZona()))
                    .count();
            double porcentaje = zona.getCapacidad() == 0 ? 0.0 : (100.0 * ocupadas / zona.getCapacidad());
            resultado.put(zona.getNombre(), porcentaje);
        }
        return resultado;
    }

    @Override
    public Map<String, Double> ingresosPorServicioAdicional() {
        double vip = 0, seguro = 0, parqueadero = 0, merchandising = 0, acceso = 0;
        for (Compra c : plat.getCompras()) {
            if (c.getEstadoEnum() != CompraEstado.PAGADA
                    && c.getEstadoEnum() != CompraEstado.CONFIRMADA) continue;
            for (Entrada e : c.getEntradas()) {
                if (contienePaqueteVIP(e))      vip         += PaqueteVIPDecorator.COSTO;
                if (contieneSeguro(e))          seguro      += SeguroCancelacionDecorator.COSTO_DEFAULT;
                if (contieneParqueadero(e))     parqueadero += ParqueaderoDecorator.COSTO;
                if (contieneMerchandising(e))   merchandising += MerchandisingDecorator.COSTO;
                if (contieneAccesoPreferencial(e)) acceso   += AccesoPreferencialDecorator.COSTO;
            }
        }
        Map<String, Double> m = new LinkedHashMap<>();
        m.put("VIP", vip);
        m.put("Seguro", seguro);
        m.put("Parqueadero", parqueadero);
        m.put("Merchandising", merchandising);
        m.put("Acceso Preferencial", acceso);
        return m;
    }

    @Override
    public ReporteOperativo generarReporteOperativo(LocalDate desde, LocalDate hasta) {
        List<Compra> enPeriodo = plat.getCompras().stream()
                .filter(c -> {
                    LocalDate f = c.getFecha().toLocalDate();
                    return (desde == null || !f.isBefore(desde))
                            && (hasta == null || !f.isAfter(hasta));
                })
                .toList();

        int totalCompras = enPeriodo.size();

        int canceladas = (int) enPeriodo.stream()
                .filter(c -> c.getEstadoEnum() == CompraEstado.CANCELADA
                        || c.getEstadoEnum() == CompraEstado.REEMBOLSADA)
                .count();

        double totalVentas = enPeriodo.stream()
                .filter(c -> c.getEstadoEnum() == CompraEstado.PAGADA
                        || c.getEstadoEnum() == CompraEstado.CONFIRMADA)
                .mapToDouble(Compra::getTotal)
                .sum();

        double tasaCancelacion = totalCompras == 0 ? 0.0 : (100.0 * canceladas / totalCompras);

        // Ingresos por extra (solo compras PAGADA/CONFIRMADA en el período)
        Map<String, Double> extras = new LinkedHashMap<>();
        extras.put("VIP", 0.0);
        extras.put("Seguro", 0.0);
        extras.put("Parqueadero", 0.0);
        extras.put("Merchandising", 0.0);
        extras.put("Acceso Preferencial", 0.0);
        for (Compra c : enPeriodo) {
            if (c.getEstadoEnum() != CompraEstado.PAGADA
                    && c.getEstadoEnum() != CompraEstado.CONFIRMADA) continue;
            for (Entrada e : c.getEntradas()) {
                if (contienePaqueteVIP(e))         extras.merge("VIP", PaqueteVIPDecorator.COSTO, Double::sum);
                if (contieneSeguro(e))             extras.merge("Seguro", SeguroCancelacionDecorator.COSTO_DEFAULT, Double::sum);
                if (contieneParqueadero(e))        extras.merge("Parqueadero", ParqueaderoDecorator.COSTO, Double::sum);
                if (contieneMerchandising(e))      extras.merge("Merchandising", MerchandisingDecorator.COSTO, Double::sum);
                if (contieneAccesoPreferencial(e)) extras.merge("Acceso Preferencial", AccesoPreferencialDecorator.COSTO, Double::sum);
            }
        }

        // Top eventos por ventas (desc)
        Map<String, Double> topEventos = enPeriodo.stream()
                .filter(c -> c.getEstadoEnum() == CompraEstado.PAGADA
                        || c.getEstadoEnum() == CompraEstado.CONFIRMADA)
                .collect(Collectors.groupingBy(
                        c -> c.getEvento().getNombre(),
                        Collectors.summingDouble(Compra::getTotal)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));

        return new ReporteOperativo(desde, hasta, totalVentas, totalCompras, canceladas,
                tasaCancelacion, extras, topEventos);
    }

    @Override
    public long contarUsuarios() {
        return plat.getUsuarios().size();
    }

    @Override
    public long contarIncidenciasAbiertas() {
        return plat.getIncidencias().stream().filter(i -> !i.isResuelta()).count();
    }

    // === OBSERVER ===

    /**
     * Delega la suscripción al sujeto {@code PlataformaEventosSingleton}.
     *
     * <p>[Patrón: Observer] - Registra el controlador del Dashboard como observer activo.</p>
     */
    @Override
    public void registrarObserver(EventoObserver obs) {
        plat.suscribir(obs);
    }

    /**
     * Delega la desuscripción al sujeto {@code PlataformaEventosSingleton}.
     *
     * <p>[Patrón: Observer] - Elimina la referencia al controlador cuando la ventana se cierra.</p>
     */
    @Override
    public void desregistrarObserver(EventoObserver obs) {
        plat.desuscribir(obs);
    }

    // === Helpers privados ===

    /** Busca un recinto por ID; lanza {@link NoSuchElementException} si no existe. */
    private Recinto buscarRecinto(String id) {
        return plat.getRecintos().stream()
                .filter(r -> r.getIdRecinto().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Recinto no encontrado: " + id));
    }

    /**
     * Reasigna a un comprador de un asiento a otro en el mismo evento.
     *
     * <p>[Requerimiento: RF-016] - Flujo completo:</p>
     */
    @Override
    public void reasignarAsiento(String idCompra, String idAsientoAntiguo, String idAsientoNuevo) {
        Compra compra = buscarCompra(idCompra);
        // 1. Validar que la compra esté en un estado que permita reasignación
        if (compra.getEstadoEnum() != CompraEstado.PAGADA
                && compra.getEstadoEnum() != CompraEstado.CONFIRMADA) {
            throw new IllegalStateException("Solo se puede reasignar en compras PAGADAS o CONFIRMADAS.");
        }

        Evento evento = compra.getEvento();

        // 2. Localizar la EntradaAsiento base que referencia el asiento antiguo (desenrollando decoradores)
        EntradaAsiento entradaBase = compra.getEntradas().stream()
                .map(AdministracionFacadeImpl::unwrapEntradaAsiento)
                .filter(ea -> ea != null
                        && ea.getAsientoEvento().getIdAsiento().equals(idAsientoAntiguo))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "No se encontró una entrada para el asiento: " + idAsientoAntiguo));

        AsientoEvento aeAntiguo = evento.obtenerAsientoEvento(idAsientoAntiguo);
        AsientoEvento aeNuevo   = evento.obtenerAsientoEvento(idAsientoNuevo);

        // 3. Verificar que el asiento destino esté disponible en el inventario del evento
        if (aeNuevo.getEstado() != AsientoEstado.DISPONIBLE) {
            throw new IllegalStateException("El asiento destino no está disponible.");
        }

        // 4. Liberar el asiento antiguo y ocupar el nuevo en el inventario
        aeAntiguo.setEstado(AsientoEstado.DISPONIBLE);
        aeNuevo.setEstado(AsientoEstado.VENDIDO);

        // 5. Actualizar la referencia interna de la entrada al nuevo asiento
        entradaBase.reasignarA(aeNuevo);

        // 6. Notificar a los observers del cambio de aforo
        plat.notificarCambio(evento);
    }

    /**
     * Busca un asiento físico en todos los recintos recorriendo la jerarquía Recinto → Zona → Asiento.
     * Lanza {@link NoSuchElementException} si no existe ningún asiento con ese ID.
     */
    private Asiento buscarAsiento(String idAsiento) {
        for (Recinto r : plat.getRecintos()) {
            for (Zona z : r.getZonas()) {
                for (Asiento a : z.getAsientos()) {
                    if (a.getIdAsiento().equals(idAsiento)) return a;
                }
            }
        }
        throw new NoSuchElementException("Asiento no encontrado: " + idAsiento);
    }

    /** Busca una compra por ID en el repositorio global. */
    private Compra buscarCompra(String id) {
        return plat.getCompras().stream()
                .filter(c -> c.getIdCompra().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Compra no encontrada: " + id));
    }

    /**
     * Desenvuelve la cadena de decoradores de una entrada hasta encontrar la {@code EntradaAsiento} base.
     * Devuelve {@code null} si la entrada base no es de tipo {@code EntradaAsiento}.
     *
     * <p>[Patrón: Decorator] - Necesario para reasignación y métricas por asiento.</p>
     */
    private static EntradaAsiento unwrapEntradaAsiento(Entrada e) {
        Entrada actual = e;
        while (actual instanceof co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.EntradaDecorator d) {
            actual = d.getEntradaEnvuelta();
        }
        return (actual instanceof EntradaAsiento ea) ? ea : null;
    }

    /**
     * Desenvuelve la cadena de decoradores y devuelve la zona de la entrada base (cualquier tipo).
     *
     * <p>[Patrón: Decorator] - Usado en el cálculo de ocupación por zona del Dashboard.</p>
     */
    private static Zona zonaDeEntrada(Entrada e) {
        Entrada actual = e;
        while (actual instanceof co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.EntradaDecorator d) {
            actual = d.getEntradaEnvuelta();
        }
        if (actual instanceof EntradaAsiento ea) return ea.getZona();
        if (actual instanceof EntradaZona ez) return ez.getZona();
        return null;
    }

    /**
     * Recorre la cadena de decoradores buscando un tipo específico de decorador.
     *
     * <p>[Patrón: Decorator] - Clave para calcular ingresos por servicio adicional en métricas y reportes.</p>
     *
     * @param e     entrada (posiblemente decorada)
     * @param clazz clase del decorador a buscar
     * @return {@code true} si algún nivel de la cadena es una instancia de {@code clazz}
     */
    private static boolean contieneDecorator(Entrada e, Class<?> clazz) {
        Entrada actual = e;
        while (actual instanceof co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.EntradaDecorator d) {
            if (clazz.isInstance(d)) return true;
            actual = d.getEntradaEnvuelta();
        }
        return false;
    }

    private static boolean contienePaqueteVIP(Entrada e) {
        return contieneDecorator(e, PaqueteVIPDecorator.class);
    }

    private static boolean contieneSeguro(Entrada e) {
        return contieneDecorator(e, SeguroCancelacionDecorator.class);
    }

    private static boolean contieneParqueadero(Entrada e) {
        return contieneDecorator(e, ParqueaderoDecorator.class);
    }

    private static boolean contieneMerchandising(Entrada e) {
        return contieneDecorator(e, MerchandisingDecorator.class);
    }

    private static boolean contieneAccesoPreferencial(Entrada e) {
        return contieneDecorator(e, AccesoPreferencialDecorator.class);
    }
}
