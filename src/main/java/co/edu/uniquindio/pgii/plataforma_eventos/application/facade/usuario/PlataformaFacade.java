package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.FormatoReporte;

import java.util.List;

public interface PlataformaFacade {

    Usuario login(String correo, String password);

    /** Registra un nuevo usuario. Lanza {@link IllegalArgumentException} si el correo ya existe. */
    Usuario registrarUsuario(String nombre, String correo, String telefono, String password);

    void actualizarPerfil(Usuario usuario);

    List<Evento> obtenerEventosDisponibles();

    int obtenerCuposDisponibles(String idEvento, String idZona);

    /**
     * Crea una orden de compra en estado CREADA: asigna asientos (BLOQUEADO) o
     * reserva cupo por zona, aplica decoradores y calcula el total. No procesa pago.
     *
     * @param idAsientos lista de asientos a reservar; vacía si es modo zona general.
     * @param cantidad   número de entradas para zona general (ignorado si hay asientos).
     */
    Compra crearOrdenCompra(String idUsuario, String idEvento, String idZona,
                            List<String> idAsientos, int cantidad, List<String> extras);

    /**
     * Procesa el pago de una orden CREADA. Si el banco aprueba, transita a PAGADA
     * y marca los asientos como VENDIDO. Si rechaza, la orden pasa a CANCELADA y
     * se liberan los asientos.
     */
    void procesarPagoOrden(String idCompra, String numTarjeta, String cvv);

    /** Cancela una orden CREADA y libera los recursos asociados. */
    void cancelarOrdenCompra(String idCompra);

    Compra buscarCompra(String idCompra);

    List<Compra> obtenerComprasPorUsuario(String idUsuario);

    /**
     * Genera el comprobante de una compra en el formato solicitado.
     * Delegado a {@link co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.ExportadorReporte}.
     */
    byte[] generarComprobante(String idCompra, FormatoReporte formato);
}
