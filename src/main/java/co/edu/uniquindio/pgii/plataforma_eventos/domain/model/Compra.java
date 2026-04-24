package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.CompraEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.state.CompraState;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.state.EstadoCreada;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Agregado raíz que modela una transacción de compra de entradas realizada por un {@link Usuario}.
 *
 * <p>Actúa como el <strong>Contexto</strong> del patrón State: delega las operaciones del ciclo
 * de vida ({@link #pagar()}, {@link #confirmar()}, {@link #cancelar()}, {@link #reembolsar()})
 * al objeto {@code CompraState} activo, que encapsula las reglas de cada estado y maneja
 * las transiciones válidas.</p>
 *
 * <p>[Requerimiento: RF-005] - El objeto se crea al iniciar el proceso de compra y arranca
 * en {@code EstadoCreada}; el pago exitoso lo transiciona a {@code EstadoPagada}.</p>
 * <p>[Requerimiento: RF-006] - La confirmación de la orden transiciona la compra de
 * {@code EstadoPagada} a {@code EstadoConfirmada}, activando definitivamente las entradas.</p>
 * <p>[Requerimiento: RF-009] - La lista de compras del usuario (historial) se obtiene
 * filtrando el repositorio global por {@code usuario.getIdUsuario()}.</p>
 * <p>[Requerimiento: RF-010] - El método {@link #cancelar()} invoca la lógica del estado
 * actual; si es válido, libera los recursos (asientos) y marca la compra como cancelada.</p>
 * <p>[Requerimiento: RF-011] - El método {@link #reembolsar()} sólo está disponible
 * desde {@code EstadoPagada} o {@code EstadoConfirmada}; lanza excepción en otros estados.</p>
 * <p>[Patrón: State] - Actúa como el <strong>Contexto</strong>; el estado concreto actual
 * se almacena en {@code estadoActual} y es reemplazado por el propio estado en cada transición.</p>
 */
public class Compra {

    /** Identificador único de la compra, generado al crearla. */
    private final String idCompra;

    /** Usuario que realizó la compra. */
    private final Usuario usuario;

    /** Evento para el que se compraron las entradas. */
    private final Evento evento;

    /** Fecha y hora en que se inició la compra (instante de creación). */
    private final LocalDateTime fecha;

    /** Suma total de los precios de todas las entradas (incluyendo servicios adicionales). */
    private double total;

    /**
     * Estado actual del ciclo de vida de la compra.
     *
     * <p>[Patrón: State] - Referencia al estado concreto actual; las operaciones del ciclo
     * de vida son delegadas a este objeto, que valida la transición y reemplaza su propio
     * valor en el contexto mediante {@link #setEstado(CompraState)}.</p>
     */
    private CompraState estadoActual;

    /** Lista de entradas asociadas a esta compra (con posibles decoradores aplicados). */
    private final List<Entrada> entradas;

    /**
     * Crea una nueva compra en estado {@code CREADA} para el usuario y evento dados.
     *
     * <p>[Requerimiento: RF-005] - Primer paso del flujo de compra; inicializa el contexto
     * del patrón State con {@code EstadoCreada}.</p>
     *
     * @param usuario usuario comprador
     * @param evento  evento para el que se compran las entradas
     */
    public Compra(Usuario usuario, Evento evento) {
        this.idCompra = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.evento = evento;
        this.fecha = LocalDateTime.now();
        this.total = 0.0;
        this.estadoActual = new EstadoCreada(this);
        this.entradas = new ArrayList<>();
    }

    // --- DELEGACIÓN DEL COMPORTAMIENTO AL PATRÓN STATE ---

    /**
     * Procesa el pago de la compra.
     * Delega en el estado actual; válido únicamente desde {@code EstadoCreada}.
     *
     * <p>[Requerimiento: RF-005] - Invocado desde {@code PlataformaFacadeImpl} tras procesar
     * el cobro con el {@code SimuladorPagoAdapter}.</p>
     */
    public void pagar() {
        this.estadoActual.pagar();
    }

    /**
     * Confirma definitivamente la orden de compra.
     * Delega en el estado actual; válido únicamente desde {@code EstadoPagada}.
     *
     * <p>[Requerimiento: RF-006] - Activa las entradas para acceso al evento.</p>
     */
    public void confirmar() {
        this.estadoActual.confirmar();
    }

    /**
     * Cancela la compra, liberando todos los recursos (asientos) reservados.
     * Delega en el estado actual; válido desde {@code EstadoCreada} y {@code EstadoPagada}.
     *
     * <p>[Requerimiento: RF-010] - Invocado por el usuario o el administrador.</p>
     */
    public void cancelar() {
        this.estadoActual.cancelar();
    }

    /**
     * Inicia el proceso de reembolso de la compra.
     * Delega en el estado actual; válido únicamente desde {@code EstadoPagada} o {@code EstadoConfirmada}.
     *
     * <p>[Requerimiento: RF-011] - Invocado por el administrador desde el módulo de gestión.</p>
     */
    public void reembolsar() {
        this.estadoActual.reembolsar();
    }

    // --- MÉTODOS DE APOYO ---

    /**
     * Reemplaza el estado actual de la compra. Invocado por las clases de estado al transicionar.
     *
     * <p>[Patrón: State] - Mecanismo por el cual el estado concreto se auto-reemplaza en el contexto.</p>
     *
     * @param nuevoEstado nuevo estado concreto
     */
    public void setEstado(CompraState nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    /**
     * Añade una entrada (simple o decorada) a la lista de la compra.
     *
     * @param entrada entrada a agregar (puede ser {@code EntradaZona}, {@code EntradaAsiento}
     *                o cualquier {@code EntradaDecorator})
     */
    public void agregarEntrada(Entrada entrada) {
        this.entradas.add(entrada);
    }

    /**
     * Recalcula el total sumando {@code getPrecioTotal()} de cada entrada.
     * Los decoradores añaden su costo adicional en cada llamada a ese método.
     */
    public void calcularTotal() {
        this.total = this.entradas.stream()
                .mapToDouble(Entrada::getPrecioTotal)
                .sum();
    }

    /**
     * Devuelve el estado de la compra como enumeración observable por la UI de JavaFX.
     *
     * @return valor de {@link CompraEstado} correspondiente al estado concreto actual
     */
    public CompraEstado getEstadoEnum() {
        return this.estadoActual.getEstadoEnum();
    }

    // GETTERS
    public String getIdCompra() {
        return idCompra;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }
}
