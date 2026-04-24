# Requerimientos de Patrones de Diseño

La plataforma implementa una arquitectura limpia (Clean Architecture) soportada por 9 patrones de diseño clásicos (GoF)
distribuidos en creacionales, estructurales y de comportamiento. A continuación se detalla la justificación, propósito y
solución representativa de cada uno.

---

## 🛠️ RF-049: Patrones Creacionales

### 1. Patrón: Singleton

* **Requisito(s) asociado(s):** Transversal (Simulación de persistencia y estado global de la aplicación).
* **Problema:** Se requiere una única fuente de verdad en memoria (`PlataformaEventosSingleton`) que almacene los
  usuarios, eventos, recintos y compras. Si se instancia más de una vez, se perdería el estado transaccional del sistema
  y habría inconsistencia de datos.
* **Propósito:** Garantizar que una clase tenga una única instancia en todo el ciclo de vida de la aplicación y
  proporcionar un punto de acceso global a ella.
* **Solución (Diagrama y Código):** Se define un constructor privado y un atributo estático privado de la misma clase,
  exponiendo el método `getInstance()` para su recuperación.
    * *Diagrama breve:* Cliente `->` llama `PlataformaEventosSingleton.getInstance()` `->` retorna instancia única.

```java
// Código representativo
public class PlataformaEventosSingleton {
    private static PlataformaEventosSingleton instancia;
    // Estructuras de datos simulando la BD...

    private PlataformaEventosSingleton() {
        // Constructor privado: evita instanciación mediante 'new'
        inicializarDatosPrueba();
    }

    public static PlataformaEventosSingleton getInstance() {
        if (instancia == null) {
            instancia = new PlataformaEventosSingleton();
        }
        return instancia;
    }
}
```

### 2. Patrón: Factory (Simple Factory / Factory Method)

* **Requisito(s) asociado(s):** RF-010 (Seleccionar zona o asiento específico).
* **Problema:** El sistema maneja dos tipos base de entradas concretas (`EntradaZona` y `EntradaAsiento`). La lógica de
  la aplicación no debería acoplarse a la instanciación específica mediante sentencias `new`, centralizando la creación
  según el tipo de localidad solicitada.
* **Propósito:** Ocultar la lógica compleja de instanciación al cliente y referirse al objeto recién creado mediante una
  interfaz común (`Entrada`).
* **Solución (Diagrama y Código):** La clase `EntradaFactory` recibe parámetros y decide qué implementación concreta de
  la interfaz abstracta instanciar.

```java
// Código representativo
public class EntradaFactory {
    public static Entrada crearEntrada(Evento evento, Zona zona, Usuario usuario, Asiento asientoAsignado) {
        if (zona.isNumerada() && asientoAsignado != null) {
            return new EntradaAsiento(evento, zona, usuario, asientoAsignado);
        } else {
            return new EntradaZona(evento, zona, usuario);
        }
    }
}
```

### 3. Patrón: Builder

* **Requisito(s) asociado(s):** RF-012 (Administrar Eventos - Crear evento).
* **Problema:** La clase `Evento` requiere una configuración compleja y extensa (ID, nombre, fechas, categoría, recinto,
  inicialización de inventario `AsientoEvento`). Usar un constructor telescópico tradicional (
  `new Evento(a, b, c, d, e...)`) es propenso a errores e ilegible.
* **Propósito:** Separar la construcción de un objeto complejo de su representación, permitiendo crear diferentes
  representaciones con el mismo proceso de construcción.
* **Solución (Diagrama y Código):** Una clase estática interna `EventoBuilder` que encadena métodos para establecer
  atributos paso a paso, finalizando con un método `build()`.

```java
// Código representativo
Evento nuevoEvento = new Evento.EventoBuilder()
                .id("EVT-100")
                .nombre("Rock al Parque")
                .categoria(EventoCategoria.CONCIERTO)
                .recinto(estadioRecinto) // Al inyectar el recinto, el builder genera los AsientoEvento
                .fecha(LocalDateTime.now().plusDays(30))
                .estado(EventoEstado.PUBLICADO)
                .build();
```

---

## 🧱 RF-050: Patrones Estructurales

### 1. Patrón: Decorator

* **Requisito(s) asociado(s):** RF-009 (Agregar extras a la compra en el checkout).
* **Problema:** Los usuarios pueden seleccionar complementos para sus entradas (VIP, Seguro de cancelación,
  Parqueadero). Crear una subclase para cada combinación (ej. `EntradaZonaConSeguroYParqueadero`) generaría una
  explosión de clases inmanejable.
* **Propósito:** Añadir responsabilidades o comportamientos adicionales (como costo extra o descripción) a un objeto
  dinámicamente en tiempo de ejecución, de forma flexible y alternativa a la herencia.
* **Solución (Diagrama y Código):** `EntradaDecorator` implementa la misma interfaz de `Entrada` y envuelve (wraps) el
  objeto original, delegando las llamadas y sumando sus propios costos.
    * *Diagrama:* `Entrada` (Base) `<--` envuelve `<--` `EntradaDecorator` `<--` hereda `<--` `PaqueteVIPDecorator`.

```java
// Código representativo
public class PaqueteVIPDecorator extends EntradaDecorator {

    public PaqueteVIPDecorator(Entrada entradaDecorada) {
        super(entradaDecorada);
    }

    @Override
    public double calcularPrecioTotal() {
        // Delega el costo base y suma el costo del decorador VIP
        return super.calcularPrecioTotal() + 50000.0;
    }

    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion() + " + Paquete VIP (Acceso a Backstage)";
    }
}
```

### 2. Patrón: Adapter

* **Requisito(s) asociado(s):** RF-021 (Simular proceso de pago) y RF-046 (Reportes).
* **Problema:** Existen librerías externas o sistemas de terceros (como el `SimuladorPagoExterno` o generadores de
  PDF/CSV) cuyas interfaces de métodos no coinciden con la lógica de dominio interna de la plataforma.
* **Propósito:** Actuar como intermediario (Wrapper) para que clases con interfaces incompatibles trabajen juntas sin
  modificar el código fuente de las librerías externas.
* **Solución (Diagrama y Código):** `SimuladorPagoAdapter` implementa nuestra interfaz de dominio `ProcesadorPago`, pero
  en su interior delega el llamado a los métodos incompatibles del sistema de pago externo.

```java
// Código representativo
public class SimuladorPagoAdapter implements ProcesadorPago {
    private SimuladorPagoExterno sistemaExternoLegacy;

    public SimuladorPagoAdapter() {
        this.sistemaExternoLegacy = new SimuladorPagoExterno();
    }

    @Override
    public boolean procesarTransaccion(MedioPago tarjeta, double monto) {
        // Se adapta el llamado de nuestro dominio al formato que exige el tercero
        return sistemaExternoLegacy.hacerCobro(
                tarjeta.getNumero(),
                tarjeta.getCvv(),
                monto
        );
    }
}
```

### 3. Patrón: Facade

* **Requisito(s) asociado(s):** Estructura arquitectónica transversal (Desacoplamiento de la Interfaz Gráfica JavaFX).
* **Problema:** Los Controladores de UI (ej. `AsignacionController` o `PagoController`) tendrían que orquestar
  interacciones complejas entre el Singleton, las Factory, los Decorators y los Strategies, violando la regla de
  Responsabilidad Única y acoplando la UI a la lógica de negocio.
* **Propósito:** Proporcionar una interfaz unificada y de alto nivel que defina un punto de entrada simple para un
  subsistema complejo.
* **Solución (Diagrama y Código):** Se exponen fachadas como `PlataformaFacade` y `AdministracionFacade`, de las cuales
  los controladores de UI consumen métodos directos y limpios.

```java
// Código representativo
public class PlataformaFacadeImpl implements PlataformaFacade {
    @Override
    public Compra realizarCompra(Usuario usuario, Evento evento, Zona zona, Asiento asiento, List<String> extras) {
        // La fachada orquesta:
        // 1. Validar estrategia (Strategy)
        // 2. Crear entrada (Factory)
        // 3. Aplicar extras (Decorator)
        // 4. Cambiar estado a Creada (State)
        // 5. Guardar en (Singleton)
        // Ocultando toda esta complejidad a la capa UI de JavaFX.
    }
}
```

---

## ⚙️ RF-051: Patrones de Comportamiento

### 1. Patrón: Strategy

* **Requisito(s) asociado(s):** RF-010 (Asignar y validar disponibilidad de zonas vs asientos).
* **Problema:** La lógica para comprobar la disponibilidad y asignar una entrada cambia radicalmente dependiendo de si
  la zona no es numerada (se valida el aforo total de la zona) o si es numerada (se valida el estado de `AsientoEvento`
  específico). Usar sentencias `if/switch` masivas haría el código in-mantenible.
* **Propósito:** Definir una familia de algoritmos, encapsular cada uno como un objeto y hacerlos intercambiables
  dinámicamente en tiempo de ejecución.
* **Solución (Diagrama y Código):** Se inyecta al contexto la interfaz abstracta `AsignacionStrategy`, ejecutando la
  concreta según el tipo de localidad seleccionada.

```java
// Código representativo
public interface AsignacionStrategy {
    boolean validarDisponibilidad(Evento evento, Zona zona, Asiento asiento);
}

public class AsignacionPorAsientoStrategy implements AsignacionStrategy {
    @Override
    public boolean validarDisponibilidad(Evento evento, Zona zona, Asiento asiento) {
        // Valida en el inventario comercial específico del evento
        AsientoEvento asientoEvento = evento.obtenerAsientoEvento(asiento.getIdAsiento());
        return asientoEvento.getEstado() == AsientoEstado.DISPONIBLE;
    }
}
```

### 2. Patrón: State

* **Requisito(s) asociado(s):** RF-008, RF-018 (Gestionar estado de las compras y reembolsos).
* **Problema:** El objeto `Compra` cambia drásticamente su comportamiento según su etapa (Creada, Pagada, Confirmada,
  Cancelada, Reembolsada). Por ejemplo, no se puede reembolsar una compra "Cancelada" o "Creada", solo una "Confirmada"
  o "Pagada". Llenar la clase `Compra` con banderas `booleanas` rompe el principio OCP (Open-Closed).
* **Propósito:** Permitir que un objeto altere su comportamiento interno cuando su estado cambia, simulando que la clase
  del objeto cambia.
* **Solución (Diagrama y Código):** La entidad `Compra` delega sus acciones (pagar, cancelar) a un objeto que implementa
  la interfaz `CompraState`.

```java
// Código representativo
public class EstadoCreada implements CompraState {
    @Override
    public void procesarPago(Compra compra) {
        // Transición de estado válida
        compra.setEstado(new EstadoPagada());
    }

    @Override
    public void reembolsar(Compra compra) {
        // Lanza excepción: Transición inválida. No hay dinero que devolver aún.
        throw new IllegalStateException("No se puede reembolsar una compra sin pagar.");
    }
}
```

### 3. Patrón: Observer

* **Requisito(s) asociado(s):** Transversal (Notificaciones y sincronización de UI/Datos).
* **Problema:** Cuando ocurren eventos en la capa de negocio (ej. un `Evento` cambia su estado a "FINALIZADO" o una
  `Compra` consume un asiento), diferentes partes del sistema (como los dashboards del Administrador, o módulos de
  auditoría) necesitan enterarse de este cambio inmediatamente sin estar acoplados mediante dependencias directas
  cíclicas.
* **Propósito:** Definir una relación de dependencia de "uno a muchos" para que cuando el estado de un objeto (Sujeto)
  cambie, todos los objetos dependientes (Observadores) sean notificados automáticamente.
* **Solución (Diagrama y Código):** La entidad de dominio extiende o implementa un `Subject` que mantiene una lista de
  suscriptores (`Observer`), y dispara el método `notify` tras una modificación importante.

```java
// Código representativo
public class EventoSubject {
    private List<EventoObserver> observers = new ArrayList<>();

    public void agregarObservador(EventoObserver observer) {
        observers.add(observer);
    }

    public void notificarObservadores(String mensaje) {
        for (EventoObserver observer : observers) {
            observer.onEventoActualizado(mensaje);
        }
    }
}

// Dentro del Facade o Entidad:
// this.notificarObservadores("El inventario del evento ha cambiado");
```
