package co.edu.uniquindio.pgii.plataforma_eventos.application.facade.usuario;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter.reporte.FormatoReporte;

import java.util.List;

/**
 * Fachada de la capa de aplicación para las operaciones del usuario final de la plataforma.
 *
 * <p>Simplifica el acceso a los casos de uso del comprador (login, registro, exploración de
 * eventos, compra de entradas, historial y exportación de comprobantes) ocultando la
 * complejidad del dominio, las strategies de asignación, el patrón State y los adaptadores
 * de pago y exportación. Los controladores de JavaFX del módulo usuario dependen
 * exclusivamente de esta interfaz.</p>
 *
 * <p>[Requerimiento: RF-001] - {@link #login} y {@link #registrarUsuario} implementan
 * el acceso y registro de usuarios.</p>
 * <p>[Requerimiento: RF-002] - {@link #obtenerEventosDisponibles} lista el catálogo filtrado
 * por estado {@code PUBLICADO}.</p>
 * <p>[Requerimiento: RF-003] / [RF-004] - {@link #crearOrdenCompra} orquesta la Strategy
 * de asignación y aplica decoradores de servicios extras.</p>
 * <p>[Requerimiento: RF-005] - {@link #procesarPagoOrden} integra el Adapter de pago
 * ({@code SimuladorPagoAdapter}) y el patrón State de {@code Compra}.</p>
 * <p>[Requerimiento: RF-008] - {@link #actualizarPerfil} permite editar los datos del usuario.</p>
 * <p>[Requerimiento: RF-009] - {@link #obtenerComprasPorUsuario} y {@link #generarComprobante}
 * cubren el historial y la exportación PDF/CSV.</p>
 * <p>[Patrón: Facade] - Actúa como la <strong>Fachada</strong> del subsistema de usuario;
 * {@code PlataformaFacadeImpl} es el implementador concreto.</p>
 */
public interface PlataformaFacade {

    /**
     * Autentica al usuario con sus credenciales.
     *
     * <p>[Requerimiento: RF-001] - Punto de entrada al sistema; si las credenciales son
     * incorrectas lanza {@link IllegalArgumentException}.</p>
     *
     * @param correo   correo electrónico del usuario
     * @param password contraseña del usuario
     * @return entidad {@link Usuario} autenticada
     * @throws IllegalArgumentException si el correo o la contraseña no coinciden
     */
    Usuario login(String correo, String password);

    /**
     * Registra un nuevo usuario en la plataforma.
     *
     * <p>[Requerimiento: RF-001] - Valida formato de correo, longitud de contraseña y
     * unicidad del correo antes de persistir.</p>
     *
     * @param nombre   nombre completo del nuevo usuario
     * @param correo   correo electrónico (debe ser único)
     * @param telefono número de teléfono de contacto
     * @param password contraseña (mínimo 4 caracteres)
     * @return nuevo {@link Usuario} registrado
     * @throws IllegalArgumentException si el correo ya existe o los datos son inválidos
     */
    Usuario registrarUsuario(String nombre, String correo, String telefono, String password);

    /**
     * Actualiza los datos de perfil de un usuario existente.
     *
     * <p>[Requerimiento: RF-008] - Permite cambiar nombre, correo y teléfono; valida
     * que el nuevo correo no esté en uso por otro usuario.</p>
     *
     * @param usuario objeto usuario con los datos actualizados
     * @throws IllegalArgumentException si el correo ya está registrado por otro usuario
     */
    void actualizarPerfil(Usuario usuario);

    /**
     * Devuelve el catálogo de eventos disponibles para compra.
     *
     * <p>[Requerimiento: RF-002] - Filtra únicamente los eventos en estado {@code PUBLICADO}.</p>
     *
     * @return lista de eventos publicados, ordenados por fecha de creación
     */
    List<Evento> obtenerEventosDisponibles();

    /**
     * Calcula los cupos disponibles en una zona de un evento.
     *
     * <p>[Requerimiento: RF-003] - Usado por la UI para mostrar la disponibilidad
     * antes de que el usuario inicie la selección de asientos.</p>
     *
     * @param idEvento identificador del evento
     * @param idZona   identificador de la zona dentro del recinto del evento
     * @return número de cupos disponibles (asientos {@code DISPONIBLE} o aforo libre restante)
     */
    int obtenerCuposDisponibles(String idEvento, String idZona);

    /**
     * Crea una orden de compra en estado {@code CREADA}: reserva asientos (poniéndolos en
     * {@code BLOQUEADO}) o consume cupo de zona libre, aplica los decoradores de servicios
     * extras seleccionados y calcula el total. No procesa ningún pago.
     *
     * <p>[Requerimiento: RF-003] / [RF-004] - Orquesta la Strategy de asignación y aplica
     * los decoradores de la cadena Decorator según {@code extras}.</p>
     *
     * @param idUsuario  ID del usuario comprador
     * @param idEvento   ID del evento para el que se compran entradas
     * @param idZona     ID de la zona dentro del recinto del evento
     * @param idAsientos lista de IDs de asientos específicos; vacía si es modo zona general
     * @param cantidad   número de entradas para zona de aforo libre (ignorado si hay asientos)
     * @param extras     lista de códigos de servicios adicionales (ej. "VIP", "PARQUEADERO")
     * @return nueva {@link Compra} en estado {@code CREADA} con las entradas añadidas
     * @throws IllegalStateException si no hay cupo disponible o algún asiento no está disponible
     */
    Compra crearOrdenCompra(String idUsuario, String idEvento, String idZona,
                            List<String> idAsientos, int cantidad, List<String> extras);

    /**
     * Procesa el pago de una orden en estado {@code CREADA}.
     * Si el banco aprueba: transita a {@code PAGADA} y confirma la venta de asientos ({@code VENDIDO}).
     * Si rechaza: transita a {@code CANCELADA} y libera los asientos reservados.
     *
     * <p>[Requerimiento: RF-005] - Integra el Adapter {@code SimuladorPagoAdapter} y el State de {@code Compra}.</p>
     *
     * @param idCompra   ID de la compra a pagar
     * @param numTarjeta número de la tarjeta de crédito/débito
     * @param cvv        código de seguridad de la tarjeta
     * @throws RuntimeException si el pago es rechazado por el banco simulado
     */
    void procesarPagoOrden(String idCompra, String numTarjeta, String cvv);

    /**
     * Cancela una orden en estado {@code CREADA} y libera todos los recursos asociados.
     *
     * <p>[Requerimiento: RF-010] - Permite al usuario desistir de una compra antes del pago.</p>
     *
     * @param idCompra ID de la compra a cancelar
     * @throws IllegalStateException si la compra no está en estado {@code CREADA}
     */
    void cancelarOrdenCompra(String idCompra);

    /**
     * Busca una compra por su identificador único.
     *
     * @param idCompra ID de la compra
     * @return la {@link Compra} encontrada
     * @throws java.util.NoSuchElementException si no existe ninguna compra con ese ID
     */
    Compra buscarCompra(String idCompra);

    /**
     * Devuelve el historial de compras de un usuario, ordenado por fecha descendente.
     *
     * <p>[Requerimiento: RF-009] - Usado por {@code HistorialComprasController} para
     * poblar la tabla del historial del usuario.</p>
     *
     * @param idUsuario ID del usuario cuyo historial se consulta
     * @return lista de compras del usuario ordenadas de más reciente a más antigua
     */
    List<Compra> obtenerComprasPorUsuario(String idUsuario);

    /**
     * Genera el comprobante de una compra en el formato solicitado (PDF o CSV).
     * Delega en el {@code ExportadorReporte} correspondiente vía el Adapter Pattern.
     *
     * <p>[Requerimiento: RF-009] - Exportación del comprobante de compra.</p>
     *
     * @param idCompra ID de la compra para la que se genera el comprobante
     * @param formato  formato de exportación ({@code PDF} o {@code CSV})
     * @return arreglo de bytes con el contenido del archivo generado
     */
    byte[] generarComprobante(String idCompra, FormatoReporte formato);
}
