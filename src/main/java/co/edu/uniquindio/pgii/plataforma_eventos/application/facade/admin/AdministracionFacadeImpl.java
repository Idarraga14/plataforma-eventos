package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.admin;

import co.edu.uniquindio.pgii.plataforma_eventos.application.observer.EventoObserver;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.ParqueaderoDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.PaqueteVIPDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.SeguroCancelacionDecorator;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class AdministracionFacadeImpl implements AdministracionFacade {

    private final PlataformaEventosSingleton plat = PlataformaEventosSingleton.getInstance();

    // === EVENTOS ===

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

    @Override
    public Recinto crearRecinto(String nombre, String direccion, String ciudad) {
        Recinto r = new Recinto(nombre, direccion, ciudad);
        plat.getRecintos().add(r);
        return r;
    }

    @Override
    public void eliminarRecinto(String idRecinto) {
        boolean enUso = plat.getEventos().stream()
                .anyMatch(e -> e.getRecinto().getIdRecinto().equals(idRecinto));
        if (enUso) throw new IllegalStateException("El recinto está asociado a eventos existentes.");
        plat.getRecintos().removeIf(r -> r.getIdRecinto().equals(idRecinto));
    }

    @Override
    public List<Recinto> listarRecintos() {
        return new ArrayList<>(plat.getRecintos());
    }

    @Override
    public Zona agregarZona(String idRecinto, String nombre, int capacidad, double precioBase) {
        Recinto r = buscarRecinto(idRecinto);
        Zona z = new Zona(nombre, capacidad, precioBase);
        r.getZonas().add(z);
        return z;
    }

    @Override
    public void bloquearAsiento(String idAsiento) {
        // Gestión física: marca la silla como fuera de servicio en el recinto
        Asiento a = buscarAsiento(idAsiento);
        a.setHabilitadoFisicamente(false);
    }

    @Override
    public void habilitarAsiento(String idAsiento) {
        // Gestión física: devuelve la silla al servicio en el recinto
        Asiento a = buscarAsiento(idAsiento);
        a.setHabilitadoFisicamente(true);
    }

    @Override
    public void bloquearAsientoEnEvento(String idEvento, String idAsiento) {
        // Gestión comercial: bloquea la silla solo para este evento
        Evento evento = plat.buscarEvento(idEvento);
        AsientoEvento ae = evento.obtenerAsientoEvento(idAsiento);
        ae.bloquear();
    }

    @Override
    public void habilitarAsientoEnEvento(String idEvento, String idAsiento) {
        // Gestión comercial: libera la silla bloqueada solo en este evento
        Evento evento = plat.buscarEvento(idEvento);
        AsientoEvento ae = evento.obtenerAsientoEvento(idAsiento);
        ae.liberar();
    }

    // === COMPRAS ===

    @Override
    public List<Compra> listarCompras() {
        return new ArrayList<>(plat.getCompras());
    }

    @Override
    public void reembolsarCompra(String idCompra) {
        Compra c = buscarCompra(idCompra);
        c.reembolsar();
        c.getEntradas().forEach(Entrada::liberarRecursos);
        plat.notificarCambio(c.getEvento());
    }

    @Override
    public void confirmarCompra(String idCompra) {
        Compra c = buscarCompra(idCompra);
        c.confirmar();
    }

    // === INCIDENCIAS ===

    @Override
    public Incidencia registrarIncidencia(String tipo, String descripcion,
                                          IncidenciaEntidadAfectada entidad,
                                          String idEntidad, String reportadoPor) {
        Incidencia i = new Incidencia(tipo, descripcion, entidad, idEntidad, reportadoPor);
        plat.getIncidencias().add(i);
        return i;
    }

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
        double vip = 0, seguro = 0, parqueadero = 0;
        for (Compra c : plat.getCompras()) {
            if (c.getEstadoEnum() != CompraEstado.PAGADA
                    && c.getEstadoEnum() != CompraEstado.CONFIRMADA) continue;
            for (Entrada e : c.getEntradas()) {
                if (contienePaqueteVIP(e))      vip += PaqueteVIPDecorator.COSTO;
                if (contieneSeguro(e))          seguro += SeguroCancelacionDecorator.COSTO_DEFAULT;
                if (contieneParqueadero(e))     parqueadero += ParqueaderoDecorator.COSTO;
            }
        }
        Map<String, Double> m = new LinkedHashMap<>();
        m.put("VIP", vip);
        m.put("Seguro", seguro);
        m.put("Parqueadero", parqueadero);
        return m;
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

    @Override
    public void registrarObserver(EventoObserver obs) {
        plat.suscribir(obs);
    }

    @Override
    public void desregistrarObserver(EventoObserver obs) {
        plat.desuscribir(obs);
    }

    // === Helpers ===

    private Recinto buscarRecinto(String id) {
        return plat.getRecintos().stream()
                .filter(r -> r.getIdRecinto().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Recinto no encontrado: " + id));
    }

    @Override
    public void reasignarAsiento(String idCompra, String idAsientoAntiguo, String idAsientoNuevo) {
        Compra compra = buscarCompra(idCompra);
        if (compra.getEstadoEnum() != CompraEstado.PAGADA
                && compra.getEstadoEnum() != CompraEstado.CONFIRMADA) {
            throw new IllegalStateException("Solo se puede reasignar en compras PAGADAS o CONFIRMADAS.");
        }

        Evento evento = compra.getEvento();

        // Buscar la EntradaAsiento base que corresponde al asiento antiguo
        EntradaAsiento entradaBase = compra.getEntradas().stream()
                .map(AdministracionFacadeImpl::unwrapEntradaAsiento)
                .filter(ea -> ea != null
                        && ea.getAsientoEvento().getIdAsiento().equals(idAsientoAntiguo))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "No se encontró una entrada para el asiento: " + idAsientoAntiguo));

        AsientoEvento aeAntiguo = evento.obtenerAsientoEvento(idAsientoAntiguo);
        AsientoEvento aeNuevo   = evento.obtenerAsientoEvento(idAsientoNuevo);

        if (aeNuevo.getEstado() != AsientoEstado.DISPONIBLE) {
            throw new IllegalStateException("El asiento destino no está disponible.");
        }

        // Liberar el antiguo y ocupar el nuevo
        aeAntiguo.setEstado(AsientoEstado.DISPONIBLE);
        aeNuevo.setEstado(AsientoEstado.VENDIDO);

        // Actualizar la referencia interna de la entrada
        entradaBase.reasignarA(aeNuevo);

        plat.notificarCambio(evento);
    }

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

    private Compra buscarCompra(String id) {
        return plat.getCompras().stream()
                .filter(c -> c.getIdCompra().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Compra no encontrada: " + id));
    }

    private static EntradaAsiento unwrapEntradaAsiento(Entrada e) {
        Entrada actual = e;
        while (actual instanceof co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.EntradaDecorator d) {
            actual = d.getEntradaEnvuelta();
        }
        return (actual instanceof EntradaAsiento ea) ? ea : null;
    }

    private static Zona zonaDeEntrada(Entrada e) {
        Entrada actual = e;
        while (actual instanceof co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.EntradaDecorator d) {
            actual = d.getEntradaEnvuelta();
        }
        if (actual instanceof EntradaAsiento ea) return ea.getZona();
        if (actual instanceof EntradaZona ez) return ez.getZona();
        return null;
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

    private static boolean contieneDecorator(Entrada e, Class<?> clazz) {
        Entrada actual = e;
        while (actual instanceof co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.EntradaDecorator d) {
            if (clazz.isInstance(d)) return true;
            actual = d.getEntradaEnvuelta();
        }
        return false;
    }
}
