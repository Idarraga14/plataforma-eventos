PROYECTO FINAL

Plataforma de gestión de eventos y venta de entradas
Curso: Programación 2

DESCRIPCIÓN DEL PROYECTO FINAL - CONTEXTO
Desarrollar una plataforma de gestión de eventos y venta de entradas, donde
usuarios finales pueden explorar eventos (conciertos, teatro, conferencias),
seleccionar zonas y asientos, comprar entradas, agregar servicios adicionales (p.
ej., acceso VIP, seguro de cancelación, merchandising), pagar y recibir
notificaciones sobre cambios de estado del evento y de sus compras.
Existen dos perfiles principales: Usuario y Administrador (Operaciones).

![IU de ejemplo](image.png)

1. Requerimientos del Usuario

La entidad Usuario representa a cada persona que utiliza la plataforma para
buscar eventos, comprar entradas y gestionar sus compras. Cada usuario tiene
un identificador único (idUsuario), nombre completo, correo electrónico, número
de teléfono y un conjunto de métodos de pago simulados. El usuario puede
consultar eventos, seleccionar entradas por zona/asiento, realizar pagos,
descargar comprobantes, solicitar cancelaciones según políticas y descargar
reportes de sus compras.

| Requisito | Especiﬁcación                                                                                             |
| --------- | --------------------------------------------------------------------------------------------------------- |
| RF-001    | Registrarse y/o iniciar sesión.                                                                           |
| RF-002    | Gestionar perﬁl (nombre, correo, teléfono).                                                               |
| RF-003    | Explorar eventos disponibles con ﬁltros (fecha, ciudad, categoría, precio).                               |
| RF-004    | Consultar detalle de un evento (descripción, lugar, fechas, aforo, zonas, precios, reglas).               |
| RF-005    | Seleccionar entradas por zona y/o asientos (según disponibilidad).                                        |
| RF-006    | Crear, modiﬁcar y cancelar una compra antes de conﬁrmarse el pago.                                        |
| RF-007    | Pagar la compra y consultar comprobantes.                                                                 |
| RF-008    | Visualizar estado de la compra (Creada, Pagada, Conﬁrmada, Cancelada, Reembolsada, Incidencia).           |
| RF-009    | Agregar servicios adicionales a la compra (VIP, seguro, merchandising, parqueadero, acceso preferencial). |
| RF-010    | Consultar historial de compras con ﬁltros por fecha, evento y estado.                                     |
| RF-011    | Descargar reportes de sus compras en CSV o PDF.                                                           |

2.  Requerimientos del Administrador

El Administrador representa al personal de operaciones encargado de gestionar el catálogo de eventos,
la conﬁguración de zonas/asientos, el control de disponibilidad, el registro de incidencias, la administración
de usuarios y la visualización de métricas de ventas.

| Requisito | Especiﬁcación                                                                                                                    |
| --------- | -------------------------------------------------------------------------------------------------------------------------------- |
| RF-012    | Gestionar usuarios (crear/actualizar/eliminar/listar).                                                                           |
| RF-013    | Gestionar eventos (crear / actualizar / eliminar / listar / publicar / pausar/ cancelar).                                        |
| RF-014    | Gestionar recintos y zonas (crear/actualizar/eliminar/listar).                                                                   |
| RF-015    | Gestionar asientos y su disponibilidad (habilitar / bloquear / liberar /consultar).                                              |
| RF-016    | Gestionar compras (consultar, reasignar asientos si aplica, cancelar, registrar reembolsos simulados).                           |
| RF-017    | Registrar incidencias y cambios de estado (evento o compra).                                                                     |
| RF-018    | Panel de métricas: ventas por periodo, ocupación por zona, ingresos por servicios adicionales, tasa de cancelación, top eventos. |
| RF-019    | Visualización de métricas con JavaFX Charts (líneas, barras, pie).                                                               |

3.  Requerimientos sobre Entidades

3.1. Usuario

La entidad Usuario representa a las personas que utilizan la plataforma. Cada
usuario tiene un identiﬁcador único (idUsuario), nombre completo, correo electrónico,
número de teléfono y métodos de pago simulados asociados.

| Requisito | Especiﬁcación                                           |
| --------- | ------------------------------------------------------- |
| RF-020    | Registrarse, iniciar sesión y modiﬁcar datos del perﬁl. |
| RF-021    | Gestionar métodos de pago simulados.                    |
| RF-022    | Consultar compras asociadas y sus detalles.             |

3.2. Evento

La entidad Evento representa una actividad programada (concierto, teatro,
conferencia). Incluye identificador único (idEvento), nombre, categoría,
descripción, ciudad, fecha/hora, estado del evento (Borrador, Publicado,
Pausado, Cancelado, Finalizado), políticas (cancelación, reembolso), y un recinto
asociado.

| Requisito | Especiﬁcación                                             |
| --------- | --------------------------------------------------------- |
| RF-023    | Crear, actualizar, eliminar y consultar eventos.          |
| RF-024    | Publicar/pausar/cancelar eventos (cambio de estado).      |
| RF-025    | Consultar disponibilidad del evento por zonas y asientos. |

3.3. Recinto

La entidad Recinto representa el lugar físico donde ocurre el evento. Incluye
identiﬁcador único (idRecinto), nombre, dirección, ciudad y un conjunto de zonas.

| Requisito | Especiﬁcación                                     |
| --------- | ------------------------------------------------- |
| RF-026    | Crear, actualizar, eliminar y consultar recintos. |
| RF-027    | Administrar zonas asociadas al recinto.           |

3.4. Zona

La entidad Zona representa un sector del recinto (p. ej., VIP, Preferencial, General).
Incluye identificador único (idZona), nombre, capacidad, precio base y
configuración de asientos (si aplica).

| Requisito | Especiﬁcación                                  |
| --------- | ---------------------------------------------- |
| RF-028    | Crear, actualizar, eliminar y consultar zonas. |
| RF-029    | Deﬁnir precio base y capacidad por zona.       |
| RF-030    | Consultar ocupación por zona.                  |

3.5. Asiento

La entidad Asiento representa una unidad numerada dentro de una zona (si el
evento maneja sillas numeradas). Incluye identiﬁcador único (idAsiento), ﬁla,
número, estado (Disponible, Reservado, Vendido, Bloqueado).

| Requisito | Especiﬁcación                                                              |
| --------- | -------------------------------------------------------------------------- |
| RF-031    | Crear/actualizar/eliminar/consultar asientos por zona.                     |
| RF-032    | Cambiar estado del asiento (Disponible / Reservado / Vendido / Bloqueado). |
| RF-033    | Consultar mapa de asientos y disponibilidad.                               |

3.6. Compra

La entidad Compra representa la adquisición de entradas por parte de un usuario.
Incluye identiﬁcador único (idCompra), usuario asociado, evento asociado, fecha
de creación, total, estado de compra (Creada, Pagada, Conﬁrmada, Cancelada,
Reembolsada, Incidencia) y un conjunto de ítems de compra (entradas) y servicios
adicionales.

| Requisito | Especiﬁcación                                                                    |
| --------- | -------------------------------------------------------------------------------- |
| RF-034    | Crear compras nuevas (selección de entradas).                                    |
| RF-035    | Modiﬁcar una compra antes de pagar (cambiar entradas/servicios).                 |
| RF-036    | Cancelar una compra según reglas/políticas (antes o después del pago si aplica). |
| RF-037    | Consultar detalle de una compra.                                                 |

3.7. Entrada

La entidad Entrada representa cada ticket adquirido. Incluye identiﬁcador único
(idEntrada), zona, asiento (si aplica), precio ﬁnal calculado, y estado (Activa,
Usada, Anulada).

| Requisito | Especiﬁcación                                          |
| --------- | ------------------------------------------------------ |
| RF-038    | Generar entradas asociadas a una compra pagada.        |
| RF-039    | Consultar entradas por compra y por evento.            |
| RF-040    | Anular entradas por cancelación/reembolso (si aplica). |

3.8. Incidencia (si aplica)

LLa entidad Incidencia registra eventos anómalos o excepciones operativas (p. ej.,
intento de doble compra de asiento, error de pago, cancelación masiva de
evento). Incluye idIncidencia, tipo, descripción, fecha y entidad afectada
(evento/compra/usuario).

| Requisito | Especiﬁcación                                           |
| --------- | ------------------------------------------------------- |
| RF-041    | Registrar incidencias y asociarlas a eventos o compras. |
| RF-042    | Consultar incidencias por rango de fechas y tipo.       |

4.  Requerimientos Tecnicos

| Requisito | Especiﬁcación                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| --------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| RF-043    | Pensamiento computacional.                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| RF-044    | Diagrama de clases con relaciones (Asociación, Composición, Herencia), multiplicidad, roles, jerarquías, atributos y estética. Debe cubrir: Usuario, Evento, Recinto, Zona, Asiento, Compra, Entrada, Tarifa, Pago, Incidencia (si aplica) y clases de soporte (estrategias, decoradores, adaptadores, etc.).                                                                                                                                                                       |
| RF-045    | Implementación técnica: estructura del proyecto, repositorio, datos de prueba inicializados (usuarios, eventos, recintos, zonas, asientos, compras, pagos), aplicación funcional con JavaFX (pantallas para usuario y admin).                                                                                                                                                                                                                                                       |
| RF-046    | Generador de Reportes Operativos: exportar CSV/PDF (Apache POI / PDFBox). Reportes sugeridos: ventas por periodo, ocupación por zona, ingresos por servicios adicionales, tasa de cancelación, top eventos. Menú para elegir tipo de reporte y rango de fechas.                                                                                                                                                                                                                     |
| RF-047    | SOLID: la solución debe evidenciar aplicación de SRP, OCP, LSP, ISP, DIP con ejemplos claros (clases pequeñas, interfaces especíﬁcas, inversión de dependencias en estrategias/adaptadores, etc.).                                                                                                                                                                                                                                                                                  |
| RF-048    | El proyecto debe gestionarse obligatoriamente mediante un repositorio de control de versiones (Git). Cada integrante del equipo deberá trabajar utilizando ramas propias, siguiendo una estrategia de versionamiento deﬁnida, de tal forma que se evidencie su participación activa y continua en el desarrollo del proyecto. El repositorio debe reﬂejar commits frecuentes, descriptivos y asociados a funcionalidades, correcciones o mejoras implementadas por cada estudiante. |

5.  Requerimientos Patrones

Se debe describir el/los requisitos donde se evidencia la necesidad del patrón, el
problema, el propósito y la solución (diagrama breve + código representativo).
Mínimo 3 creacionales, 3 estructurales y 3 de comportamiento.

| Requisito | Especiﬁcación                                                                                                                                                                                                                                                       |
| --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| RF-049    | Describir el (los) requisitos del proyecto donde se vea la necesidad de implementar minino 3 patrones creacionales. Se debe considerar el problema a resolver, el propósito y la solución.<br><br>● Singleton<br>● Otro de su elección<br>● Otro de su elección     |
| RF-050    | Describir el (los) requisitos del proyecto donde se vea la necesidad de implementar minino 3 patrones estructurales. Se debe considerar el problema a resolver, el propósito y la solución.<br><br>● Decorator<br>● De su elección<br>● De su elección              |
| RF-051    | Describir el (los) requisitos del proyecto donde se vea la necesidad de implementar minino 3 patrones de comportamiento. Se debe considerar el problema a resolver, el propósito y la solución.<br><br>● Strategy<br>● Otro de su elección<br>● Otro de su elección |
