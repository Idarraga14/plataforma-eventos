package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.AccesoPreferencialDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.MerchandisingDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.PaqueteVIPDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.ParqueaderoDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.decorator.SeguroCancelacionDecorator;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.AsientoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.IncidenciaEntidadAfectada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Asiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.AsientoEvento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Entrada;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaAsiento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.EntradaZona;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Evento;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Incidencia;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.MedioPago;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Recinto;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Usuario;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Zona;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Genera un conjunto masivo y realista de datos de prueba, cubriendo casos borde
 * del Patrón State (Compra), Decorator (Entrada), Builder (Evento) y la separación
 * física/comercial de AsientoEvento.
 *
 * NOTA SOBRE FECHAS DE COMPRA: el constructor de Compra fija la fecha a LocalDateTime.now(),
 * por lo que todas las compras del seeder quedan con la fecha de hoy. Para probar reportes
 * por período, el admin debe usar el rango "inicio del mes → hoy".
 */
public class DatosPruebaSeeder {

    public static void inyectarDatos(PlataformaEventosSingleton db) {
        System.out.println("[SEEDER] Iniciando carga masiva de datos de prueba...");

        // ==================================================================
        // 1. USUARIOS  (1 Admin + 4 Clientes con roles diferenciados)
        // ==================================================================

        Usuario admin   = new Usuario("Administrador Principal", "admin@eventos.com",  "3000000000", "admin123", true);

        // Conservado de v1 para compatibilidad con el flujo de login de la UI
        Usuario juan    = new Usuario("Juan Pérez",   "juan@gmail.com",   "3111111111", "1234", false);
        juan.getMediosPago().add(new MedioPago("Juan Pérez", "4111111111111111"));

        // El Comprador Compulsivo: 3 tarjetas, protagonista de la mayoría de compras
        Usuario carlos  = new Usuario("Carlos Ramírez",  "carlos@gmail.com",  "3222222222", "1234", false);
        carlos.getMediosPago().add(new MedioPago("Carlos Ramírez",  "4000000000001111"));
        carlos.getMediosPago().add(new MedioPago("Carlos Ramírez",  "5500000000002222"));
        carlos.getMediosPago().add(new MedioPago("Empresas Ramírez","3714000000003333"));

        // El Usuario Fantasma: registrado, cero compras, cero tarjetas
        Usuario andrea  = new Usuario("Andrea Torres",   "andrea@gmail.com",  "3333333333", "1234", false);

        // El Reembolsado: historial de compras con problemas y cancelaciones
        Usuario felipe  = new Usuario("Felipe Ruiz",     "felipe@gmail.com",  "3444444444", "1234", false);
        felipe.getMediosPago().add(new MedioPago("Felipe Ruiz", "4111111111114444"));

        db.getUsuarios().add(admin);
        db.getUsuarios().add(juan);
        db.getUsuarios().add(carlos);
        db.getUsuarios().add(andrea);
        db.getUsuarios().add(felipe);

        // ==================================================================
        // 2. RECINTOS Y ZONAS
        // ==================================================================

        // --- Recinto A: Estadio Monumental — 3 zonas de flujo libre (sin asientos numerados) ---
        Recinto estadio = new Recinto("Estadio Monumental", "Av. Centenario # 100-00", "Bogotá");

        // Capacidades dimensionadas para el volumen del seeder (~5/15, ~4/10, ~3/8 → 33-40%)
        Zona canchGeneral = new Zona("Cancha General",       15,  75_000.0);
        Zona tribunaPref  = new Zona("Tribuna Preferencial", 10, 180_000.0);
        Zona palcoVIP     = new Zona("Palco Platinum VIP",    8, 550_000.0);

        estadio.getZonas().add(canchGeneral);
        estadio.getZonas().add(tribunaPref);
        estadio.getZonas().add(palcoVIP);
        db.getRecintos().add(estadio);

        // --- Recinto B: Teatro Ópera — 2 zonas CON asientos numerados ---
        Recinto teatro = new Recinto("Teatro Ópera", "Calle del Arte # 5-20", "Medellín");

        // Platea Principal: 10 filas (A–J) × 10 columnas = 100 asientos
        Zona platea = new Zona("Platea Principal", 100, 95_000.0);
        for (char fila : new char[]{'A','B','C','D','E','F','G','H','I','J'}) {
            for (int col = 1; col <= 10; col++) {
                platea.getAsientos().add(new Asiento(fila, col));
            }
        }

        // Balcón Premium: 5 filas (A–E) × 6 columnas = 30 asientos
        Zona balcon = new Zona("Balcón Premium", 30, 55_000.0);
        for (char fila : new char[]{'A','B','C','D','E'}) {
            for (int col = 1; col <= 6; col++) {
                balcon.getAsientos().add(new Asiento(fila, col));
            }
        }

        teatro.getZonas().add(platea);
        teatro.getZonas().add(balcon);
        db.getRecintos().add(teatro);

        // ==================================================================
        // 3. EVENTOS (Patrón Builder — 4 eventos con distintos estados)
        // ==================================================================

        // Evento A: Concierto masivo (Estadio, sin asientos numerados)
        Evento conciertoA = new Evento.EventoBuilder()
                .conNombre("Lollapalooza Colombia 2025")
                .conDescripcion("El festival de rock más grande del mundo llega a Colombia. 12 artistas internacionales en 3 escenarios.")
                .deCategoria(EventoCategoria.CONCIERTO)
                .enCiudad("Bogotá")
                .paraLaFecha(LocalDateTime.now().plusDays(60))
                .enRecinto(estadio)
                .build();
        conciertoA.setEstado(EventoEstado.PUBLICADO);

        // Evento B: Teatro con asientos numerados — CASO BORDE de AsientoEvento
        Evento obraB = new Evento.EventoBuilder()
                .conNombre("El Fantasma de la Ópera – Gira Nacional")
                .conDescripcion("La obra más aclamada del mundo vuelve a Colombia. Selección de asiento obligatoria.")
                .deCategoria(EventoCategoria.TEATRO)
                .enCiudad("Medellín")
                .paraLaFecha(LocalDateTime.now().plusDays(7))
                .enRecinto(teatro)
                .build();
        obraB.setEstado(EventoEstado.PUBLICADO);

        // ── CASO BORDE: bloquear asientos en el inventario comercial del Evento B ──
        // Total inventario: 130 AsientoEvento (100 Platea + 30 Balcón).
        // El enum no tiene MANTENIMIENTO; usamos BLOQUEADO con comentarios diferenciados.
        List<Asiento> asPlatea = platea.getAsientos();
        List<Asiento> asBalcon = balcon.getAsientos();

        // ~15% mantenimiento → Platea filas I (idx 80-89) y J (idx 90-99) = 20 asientos
        for (int idx = 80; idx < 100; idx++) {
            obraB.obtenerAsientoEvento(asPlatea.get(idx).getIdAsiento())
                 .setEstado(AsientoEstado.BLOQUEADO); // motivo: mantenimiento físico
        }

        // ~10% bloqueado por el organizador → Balcón filas D (idx 18-23) y E (idx 24-29) = 12 asientos
        for (int idx = 18; idx < 30; idx++) {
            obraB.obtenerAsientoEvento(asBalcon.get(idx).getIdAsiento())
                 .setEstado(AsientoEstado.BLOQUEADO); // motivo: organizador cerró estas filas
        }
        // Completar hasta 13: agregar Balcón C6 (índice 17)
        obraB.obtenerAsientoEvento(asBalcon.get(17).getIdAsiento())
             .setEstado(AsientoEstado.BLOQUEADO);     // motivo: organizador cerró estas filas

        // Evento C: Festival cancelado — los filtros de cliente no deben mostrarlo
        Evento festivalC = new Evento.EventoBuilder()
                .conNombre("Festival Gastronómico del Eje Cafetero")
                .conDescripcion("Celebración culinaria con chefs de talla internacional. Evento suspendido.")
                .deCategoria(EventoCategoria.CONFERENCIA)
                .enCiudad("Armenia")
                .paraLaFecha(LocalDateTime.now().plusDays(30))
                .enRecinto(estadio)
                .build();
        festivalC.setEstado(EventoEstado.CANCELADO);

        // Evento D: Pasado y finalizado — para poblar datos históricos en los reportes del admin
        Evento conferD = new Evento.EventoBuilder()
                .conNombre("Gran Conferencia Tecnológica Nacional 2025")
                .conDescripcion("Reunión de líderes tecnológicos del país. Networking y workshops. [FINALIZADO]")
                .deCategoria(EventoCategoria.CONFERENCIA)
                .enCiudad("Bogotá")
                .paraLaFecha(LocalDateTime.now().plusDays(2))
                .enRecinto(estadio)
                .build();
        conferD.setEstado(EventoEstado.FINALIZADO);

        db.getEventos().add(conciertoA);
        db.getEventos().add(obraB);
        db.getEventos().add(festivalC);
        db.getEventos().add(conferD);

        // ==================================================================
        // 4. COMPRAS (12 compras — Patrón State + Decorator, casos borde)
        // ==================================================================

        // ── Compra 1: PAGADA — carlos, 4 entradas Concierto A ──
        // Decorators: 2×VIP, 1×VIP+Seguro, 1×Normal
        Compra c1 = new Compra(carlos, conciertoA);
        c1.agregarEntrada(new PaqueteVIPDecorator(
                new EntradaZona(canchGeneral, canchGeneral.getPrecioBase())));
        c1.agregarEntrada(new PaqueteVIPDecorator(
                new EntradaZona(canchGeneral, canchGeneral.getPrecioBase())));
        c1.agregarEntrada(new SeguroCancelacionDecorator(new PaqueteVIPDecorator(
                new EntradaZona(tribunaPref, tribunaPref.getPrecioBase()))));
        c1.agregarEntrada(
                new EntradaZona(canchGeneral, canchGeneral.getPrecioBase()));
        c1.calcularTotal();
        c1.pagar(); // CREADA → PAGADA
        db.getCompras().add(c1);

        // ── Compra 2: CREADA (carrito abandonado) — carlos, 2 sillas Platea B1-B2 del Teatro B ──
        // Los AsientoEvento quedan en BLOQUEADO (reservados sin pagar).
        Compra c2 = new Compra(carlos, obraB);
        AsientoEvento aeB1 = obraB.obtenerAsientoEvento(asPlatea.get(10).getIdAsiento()); // B1
        AsientoEvento aeB2 = obraB.obtenerAsientoEvento(asPlatea.get(11).getIdAsiento()); // B2
        aeB1.setEstado(AsientoEstado.BLOQUEADO); // reservado, pago pendiente
        aeB2.setEstado(AsientoEstado.BLOQUEADO);
        c2.agregarEntrada(new EntradaAsiento(platea, aeB1, platea.getPrecioBase()));
        c2.agregarEntrada(new EntradaAsiento(platea, aeB2, platea.getPrecioBase()));
        c2.calcularTotal();
        // NO se llama a pagar() — carrito abandonado, estado permanece CREADA
        db.getCompras().add(c2);

        // ── Compra 3: REEMBOLSADA — felipe, 2 entradas Concierto A ──
        // Zona libre: no hay AsientoEvento que liberar (confirmarVenta()/liberarRecursos() son no-op).
        Compra c3 = new Compra(felipe, conciertoA);
        c3.agregarEntrada(new EntradaZona(canchGeneral, canchGeneral.getPrecioBase()));
        c3.agregarEntrada(new EntradaZona(canchGeneral, canchGeneral.getPrecioBase()));
        c3.calcularTotal();
        c3.pagar();      // CREADA → PAGADA
        c3.reembolsar(); // PAGADA → REEMBOLSADA
        db.getCompras().add(c3);

        // ── Compra 4: CONFIRMADA — carlos, 1 entrada Palco con Parqueadero+Seguro ──
        Compra c4 = new Compra(carlos, conciertoA);
        c4.agregarEntrada(new SeguroCancelacionDecorator(new ParqueaderoDecorator(
                new EntradaZona(palcoVIP, palcoVIP.getPrecioBase()))));
        c4.calcularTotal();
        c4.pagar();    // CREADA → PAGADA
        c4.confirmar(); // PAGADA → CONFIRMADA
        db.getCompras().add(c4);

        // ── Compra 5: PAGADA — juan, 3 entradas Concierto A con nuevos decoradores ──
        // Stress del Adapter de reportes: Merchandising y AccesoPreferencial deben aparecer en métricas.
        Compra c5 = new Compra(juan, conciertoA);
        c5.agregarEntrada(new MerchandisingDecorator(
                new EntradaZona(canchGeneral, canchGeneral.getPrecioBase())));
        c5.agregarEntrada(new AccesoPreferencialDecorator(
                new EntradaZona(tribunaPref, tribunaPref.getPrecioBase())));
        c5.agregarEntrada(new MerchandisingDecorator(new AccesoPreferencialDecorator(
                new EntradaZona(canchGeneral, canchGeneral.getPrecioBase()))));
        c5.calcularTotal();
        c5.pagar();
        db.getCompras().add(c5);

        // ── Compra 6: PAGADA — carlos, 3 asientos numerados Platea C1-C3 del Teatro B ──
        // Decorator VIP aplicado sobre el tercer asiento para probar cadena Decorator + AsientoEvento.
        Compra c6 = new Compra(carlos, obraB);
        AsientoEvento aeC1 = obraB.obtenerAsientoEvento(asPlatea.get(20).getIdAsiento()); // C1
        AsientoEvento aeC2 = obraB.obtenerAsientoEvento(asPlatea.get(21).getIdAsiento()); // C2
        AsientoEvento aeC3 = obraB.obtenerAsientoEvento(asPlatea.get(22).getIdAsiento()); // C3
        aeC1.setEstado(AsientoEstado.BLOQUEADO); // reserva previa al pago
        aeC2.setEstado(AsientoEstado.BLOQUEADO);
        aeC3.setEstado(AsientoEstado.BLOQUEADO);
        EntradaAsiento baseC1 = new EntradaAsiento(platea, aeC1, platea.getPrecioBase());
        EntradaAsiento baseC2 = new EntradaAsiento(platea, aeC2, platea.getPrecioBase());
        EntradaAsiento baseC3 = new EntradaAsiento(platea, aeC3, platea.getPrecioBase());
        Entrada vipC3 = new PaqueteVIPDecorator(baseC3); // decorator sobre asiento numerado
        c6.agregarEntrada(baseC1);
        c6.agregarEntrada(baseC2);
        c6.agregarEntrada(vipC3);
        c6.calcularTotal();
        c6.pagar();
        // Confirmar venta: propaga a través del Decorator hasta EntradaAsiento → asientoEvento.vender()
        baseC1.confirmarVenta(); // aeC1 → VENDIDO
        baseC2.confirmarVenta(); // aeC2 → VENDIDO
        vipC3.confirmarVenta();  // aeC3 → VENDIDO (via EntradaDecorator.confirmarVenta())
        db.getCompras().add(c6);

        // ── Compra 7: CANCELADA — felipe, intento de compra del Festival C (cancelado) ──
        // El cliente desistió antes de pagar. Transición CREADA → cancelar() → CANCELADA.
        Compra c7 = new Compra(felipe, festivalC);
        c7.agregarEntrada(new EntradaZona(canchGeneral, canchGeneral.getPrecioBase()));
        c7.agregarEntrada(new EntradaZona(canchGeneral, canchGeneral.getPrecioBase()));
        c7.calcularTotal();
        c7.cancelar(); // CREADA → CANCELADA (sin pasar por PAGADA)
        db.getCompras().add(c7);

        // ── Compra 8: CONFIRMADA — juan, 4 entradas para la Conferencia D (evento pasado) ──
        // Proporciona datos históricos para que el reporte de métricas tenga algo que mostrar.
        Compra c8 = new Compra(juan, conferD);
        c8.agregarEntrada(new EntradaZona(canchGeneral, canchGeneral.getPrecioBase()));
        c8.agregarEntrada(new EntradaZona(canchGeneral, canchGeneral.getPrecioBase()));
        c8.agregarEntrada(new PaqueteVIPDecorator(
                new EntradaZona(tribunaPref, tribunaPref.getPrecioBase())));
        c8.agregarEntrada(new SeguroCancelacionDecorator(
                new EntradaZona(canchGeneral, canchGeneral.getPrecioBase())));
        c8.calcularTotal();
        c8.pagar();
        c8.confirmar(); // CONFIRMADA — evento ya finalizado
        db.getCompras().add(c8);

        // ── Compra 9: PAGADA — carlos, 3 entradas Conferencia D con Parqueadero ──
        Compra c9 = new Compra(carlos, conferD);
        c9.agregarEntrada(new ParqueaderoDecorator(
                new EntradaZona(tribunaPref, tribunaPref.getPrecioBase())));
        c9.agregarEntrada(new ParqueaderoDecorator(
                new EntradaZona(canchGeneral, canchGeneral.getPrecioBase())));
        c9.agregarEntrada(new EntradaZona(palcoVIP, palcoVIP.getPrecioBase()));
        c9.calcularTotal();
        c9.pagar();
        db.getCompras().add(c9);

        // ── Compra 10: PAGADA — felipe, 2 asientos Balcón A1-A2 del Teatro B ──
        Compra c10 = new Compra(felipe, obraB);
        AsientoEvento aeBalA1 = obraB.obtenerAsientoEvento(asBalcon.get(0).getIdAsiento()); // BA1
        AsientoEvento aeBalA2 = obraB.obtenerAsientoEvento(asBalcon.get(1).getIdAsiento()); // BA2
        aeBalA1.setEstado(AsientoEstado.BLOQUEADO);
        aeBalA2.setEstado(AsientoEstado.BLOQUEADO);
        EntradaAsiento eaBalA1 = new EntradaAsiento(balcon, aeBalA1, balcon.getPrecioBase());
        EntradaAsiento eaBalA2 = new EntradaAsiento(balcon, aeBalA2, balcon.getPrecioBase());
        c10.agregarEntrada(eaBalA1);
        c10.agregarEntrada(eaBalA2);
        c10.calcularTotal();
        c10.pagar();
        eaBalA1.confirmarVenta(); // aeBalA1 → VENDIDO
        eaBalA2.confirmarVenta(); // aeBalA2 → VENDIDO
        db.getCompras().add(c10);

        // ── Compra 11: PAGADA — juan, 2 entradas Concierto A (Tribuna + Merchandising) ──
        Compra c11 = new Compra(juan, conciertoA);
        c11.agregarEntrada(new MerchandisingDecorator(
                new EntradaZona(tribunaPref, tribunaPref.getPrecioBase())));
        c11.agregarEntrada(new EntradaZona(tribunaPref, tribunaPref.getPrecioBase()));
        c11.calcularTotal();
        c11.pagar();
        db.getCompras().add(c11);

        // ── Compra 12: PAGADA — carlos, 2 entradas Palco VIP Concierto A ──
        // Contribuye al top-evento de las métricas del reporte (mayor revenue).
        Compra c12 = new Compra(carlos, conciertoA);
        c12.agregarEntrada(new PaqueteVIPDecorator(
                new EntradaZona(palcoVIP, palcoVIP.getPrecioBase())));
        c12.agregarEntrada(new PaqueteVIPDecorator(new SeguroCancelacionDecorator(
                new EntradaZona(palcoVIP, palcoVIP.getPrecioBase()))));
        c12.calcularTotal();
        c12.pagar();
        db.getCompras().add(c12);

        // ==================================================================
        // 5. INCIDENCIAS (3 — entidades: EVENTO, COMPRA, USUARIO)
        // ==================================================================

        // Incidencia 1: silla rota en el Teatro Ópera (ligada al evento de teatro)
        db.getIncidencias().add(new Incidencia(
                "INFRAESTRUCTURA",
                "Butaca rota en Platea fila D, asiento 5. Requiere reparación urgente antes del próximo viernes. " +
                "Afecta la visualización central del escenario.",
                IncidenciaEntidadAfectada.EVENTO,
                obraB.getIdEvento(),
                admin.getNombreCompleto()
        ));

        // Incidencia 2: posible cargo duplicado en el pago de la compra reembolsada (c3)
        db.getIncidencias().add(new Incidencia(
                "FRAUDE",
                "Cliente reporta cargo no reconocido de $" +
                String.format("%,.0f", c3.getTotal()) +
                " en tarjeta. Posible duplicado en pasarela de pago durante la ventana de reembolso. " +
                "Pendiente verificación con entidad bancaria.",
                IncidenciaEntidadAfectada.COMPRA,
                c3.getIdCompra(),
                admin.getNombreCompleto()
        ));

        // Incidencia 3: múltiples intentos de acceso fallidos para el usuario fantasma
        db.getIncidencias().add(new Incidencia(
                "SEGURIDAD",
                "Se detectaron 7 intentos de inicio de sesión fallidos desde una IP desconocida " +
                "para la cuenta de Andrea Torres. Se recomienda verificación de identidad y bloqueo temporal.",
                IncidenciaEntidadAfectada.USUARIO,
                andrea.getIdUsuario(),
                admin.getNombreCompleto()
        ));

        System.out.printf(
                "[SEEDER] Carga finalizada — %d usuarios, %d recintos, %d eventos, %d compras, %d incidencias.%n",
                db.getUsuarios().size(),
                db.getRecintos().size(),
                db.getEventos().size(),
                db.getCompras().size(),
                db.getIncidencias().size()
        );
    }
}
