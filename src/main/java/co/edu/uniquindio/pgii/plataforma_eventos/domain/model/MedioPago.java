package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import java.util.UUID;

/**
 * Representa un medio de pago registrado por un {@link Usuario} en la plataforma.
 *
 * <p>Almacena únicamente los datos mínimos necesarios para identificar la tarjeta:
 * el titular y los últimos cuatro dígitos del número. El número completo nunca se
 * persiste, simulando las prácticas de tokenización de pasarelas de pago reales.</p>
 *
 * <p>[Requerimiento: RF-005] - Durante el proceso de pago el usuario selecciona uno
 * de sus medios de pago registrados; el {@code SimuladorPagoAdapter} lo usa para
 * procesar la transacción a través del {@code SimuladorPagoExterno}.</p>
 * <p>[Requerimiento: RF-008] - Los medios de pago registrados del usuario son
 * visibles y gestionables desde su vista de perfil.</p>
 * <p>[Patrón: Adapter] - El {@code SimuladorPagoAdapter} recibe un {@code MedioPago}
 * y lo traduce a los parámetros que requiere el servicio externo simulado.</p>
 */
public class MedioPago {

    /** Identificador único del medio de pago. */
    private String idMedioPago;

    /** Nombre del titular de la tarjeta. */
    private String titular;

    /** Últimos cuatro dígitos del número de tarjeta (el número completo no se almacena). */
    private String ultimosCuatroDigitos;

    /**
     * Registra un nuevo medio de pago, guardando únicamente los últimos 4 dígitos.
     *
     * @param titular       nombre completo del titular de la tarjeta
     * @param numeroTarjeta número completo de la tarjeta (sólo se almacenan los 4 últimos)
     */
    public MedioPago(String titular, String numeroTarjeta) {
        this.idMedioPago = UUID.randomUUID().toString();
        this.titular = titular;
        // Solo guardamos los últimos 4 dígitos por "seguridad" simulada
        this.ultimosCuatroDigitos = numeroTarjeta.substring(numeroTarjeta.length() - 4);
    }

    /** @return identificador único del medio de pago */
    public String getIdMedioPago() {
        return idMedioPago;
    }

    /** @return nombre del titular de la tarjeta */
    public String getTitular() {
        return titular;
    }

    /** @return últimos cuatro dígitos del número de tarjeta */
    public String getUltimosCuatro() {
        return ultimosCuatroDigitos;
    }
}
