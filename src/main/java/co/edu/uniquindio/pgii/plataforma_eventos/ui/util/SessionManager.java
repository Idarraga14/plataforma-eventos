package co.edu.uniquindio.pgii.plataforma_eventos.ui.util;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

import java.util.ArrayList;
import java.util.List;

/**
 * Almacén de estado de sesión compartido entre los controladores JavaFX de la capa UI.
 *
 * <p>Mantiene en memoria el contexto temporal que los controladores necesitan compartir
 * durante un flujo de compra multi-pantalla: usuario autenticado, evento seleccionado,
 * zona elegida, asientos numerados reservados, cantidad de entradas y la orden activa.
 * Expone getters y setters simples; no contiene lógica de negocio.</p>
 *
 * <p>Usa el patrón Singleton con inicialización lazy (no thread-safe), lo cual es
 * correcto en el contexto de JavaFX donde todo corre en el hilo de la aplicación.</p>
 *
 * <p>[Requerimiento: RF-001] - Guarda el {@link Usuario} autenticado tras el login para
 * que todos los controladores posteriores puedan identificar al comprador.</p>
 * <p>[Requerimiento: RF-003] - Transporta la {@link Zona} y los {@link Asiento}s
 * seleccionados desde {@code AsignacionController} hasta {@code CheckoutExtrasController}.</p>
 * <p>[Requerimiento: RF-005] - Almacena la {@link Compra} activa (orden en proceso de pago)
 * hasta que la transacción se confirma o cancela.</p>
 * <p>[Patrón: Singleton] - Instancia única por JVM; gestiona el estado compartido entre
 * vistas de la misma sesión de usuario.</p>
 */
public class SessionManager {
    private static SessionManager instancia;

    /** Usuario que ha iniciado sesión; {@code null} si no hay sesión activa. */
    private Usuario usuarioActual;

    /** Evento sobre el que se está navegando (detalle/compra); {@code null} si no hay ninguno. */
    private Evento eventoSeleccionado;

    /** Zona elegida en la pantalla de asignación; se limpia al arrancar un flujo nuevo. */
    private Zona zonaSeleccionadaTemporal;

    /** Asientos numerados seleccionados en la cuadrícula; lista vacía para zonas de flujo libre. */
    private List<Asiento> asientosSeleccionadosTemporales = new ArrayList<>();

    /** Cantidad de entradas de zona libre; se ignora si hay asientos seleccionados. */
    private int cantidadEntradasZonaTemporal = 1;

    /** Compra creada y pendiente de pago; {@code null} cuando no hay transacción en curso. */
    private Compra ordenActual;

    private SessionManager() {
    }

    /**
     * Devuelve la instancia única (lazy initialization, hilo JavaFX).
     *
     * @return instancia única de {@code SessionManager}
     */
    public static SessionManager getInstance() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    /**
     * Establece el usuario autenticado al iniciar sesión.
     *
     * @param usuario el usuario que acaba de autenticarse
     */
    public void login(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    /** Elimina la referencia al usuario autenticado, cerrando la sesión activa. */
    public void logout() {
        this.usuarioActual = null;
    }

    /** @return el usuario autenticado, o {@code null} si no hay sesión activa */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setEventoSeleccionado(Evento evento) {
        this.eventoSeleccionado = evento;
    }

    public Evento getEventoSeleccionado() {
        return eventoSeleccionado;
    }

    public void limpiarEventoSeleccionado() {
        this.eventoSeleccionado = null;
    }

    public void setZonaSeleccionada(Zona zona) {
        this.zonaSeleccionadaTemporal = zona;
    }

    public Zona getZonaSeleccionada() {
        return zonaSeleccionadaTemporal;
    }

    public void setAsientosSeleccionados(List<Asiento> asientos) {
        this.asientosSeleccionadosTemporales = asientos;
    }

    public List<Asiento> getAsientosSeleccionados() {
        return asientosSeleccionadosTemporales;
    }

    public void setCantidadEntradas(int cantidad) {
        this.cantidadEntradasZonaTemporal = cantidad;
    }

    public int getCantidadEntradas() {
        return cantidadEntradasZonaTemporal;
    }

    public void setOrdenActual(Compra ordenActual) {
        this.ordenActual = ordenActual;
    }

    public Compra getOrdenActual() {
        return ordenActual;
    }

    public void limpiarOrdenActual() {
        this.ordenActual = null;
    }
}
