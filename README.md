Nombres integrantes del grupo: Juan Manuel Idarraga Diaz

Descripcion breve del proyecto: Desarrollar una aplicación de escritorio en Java con JavaFX que simule
una plataforma de tipo tickets para concierto, donde se tengan usuarios, admins y gestiones de la plataforma cómo
comprar entradas y crear eventos.

Instrucciones claras de compilación: 
* VersionJava: Java21
* Datos default de admin declarados en dataseeder
  Administrador - Usuario: admin@eventos.com Contraseña: admin123
  Usuario - Usuario: juan@gmail.com Contraseña: 1234
* Launcher ubicado en: co.edu.uniquindio.pgii.plataforma_eventos.ui.Launcher

Patrones de diseño implementados:

Singleton: 
Requisito asociado: Es transversal a todo el sistema, en memoria usuario, evento, 
recinto compra y asiento (RF03, 013, 015) deben existir como única instancia. 
Problema: Se requiere una única fuente de verdad en memoria que almacene los 
usuarios, eventos, recintos y compras. Si se instancia más de una vez, habría 
inconsistencia de datos. 

Factory Method: 
Requisito asociado: RF-028, 031 (Definen zonas con asientos numerados y acceso 
libre). 
Problema: ocultar la lógica compleja de creación de zonas/asientos y referirse a una 
sola base común sin acoplarse a una instancia específica. 

Builder: 
Requisito asociado: RF-012 (Administrar Eventos - Crear evento). 
Problema: La clase Evento requiere una configuración compleja y extensa (ID, 
nombre, fechas, categoría, recinto etc. 

Decorator:  
Requisito asociado: RF-009 (Agregar extras a la compra en el checkout). 
Problema: Con herencia cada combinación de servicios necesitaria una subclase 
distinta, los usuarios pueden seleccionar complementos para sus entradas (VIP, 
Seguro de cancelación, Parqueadero). 

Adapter:  
Requisito asociado: RF-021 (Simular proceso de pago) y RF-046 (Reportes). 
Problema: Existen librerías externas o sistemas de terceros como generadores de 
PDF/CSV) cuyas interfaces de métodos no coinciden con la lógica de dominio 
interna de la plataforma. 

Facade:  
Requisito asociado: Estructura transversal en todo el sistema (Desacoplamiento de 
JavaFX). 
Problema: Los Controladores de UI tendrían que orquestar interacciones complejas 
entre patrones de diseño. 

Strategy: 
Requisito asociado: RF-010 (Asignar y validar disponibilidad de zonas o asientos). 
Problema: La lógica para comprobar la disponibilidad y asignar una entrada cambia 
teniendo que usar switch extensos y complejos. 
 
State:  
Requisito asociado: RF-008, RF-018 (Gestionar estado de las compras y 
reembolsos). 
Problema: El objeto Compra cambia drásticamente su comportamiento según su 
etapa (Creada, Pagada, Confirmada, Cancelada, Reembolsada).  

Observer: 
Requisito asociado: Es transversal a todo el sistema (Notificaciones y sincronización 
de UI/Datos). 
Problema: Cuando ocurren eventos en la capa de negocio (un Evento cambia su 
estado a "FINALIZADO" o una Compra consume un asiento) los responsables 
necesitan enterarse de este cambio inmediatamente sin estar acoplados mediante 
dependencias directas 

Principios SOLID:

Cumpliendo con los principios SOLID en este proyecto hay tanto patrones como programacion funcional que cumple con estos principios
nuesto adapter es un patron que facilita una unica tarea, traduccion de interfaces, entonces a este se le carga de una unica responsabilidad,
traducir, tambien cumplimos el principio de estar abierto a la extension y cerrado a la modificacion mediante el patron state, ya que declarar estos
estados en el codigo como enlazados a para futuros cambios el que opere el codigo deberia modificar esa clase, entonces como estados propios de una clase
esta causaria problemas, tambien cumplimos con el principio de segregacion de interfaces haciendo referencia al sistema de reportes, mejor dicho a su interfaz ExportadorReporte,
una interfaz delgada que declara metodos que si o si van a ser usados en la aplicacion mas adelante y por ultimo tenemos la inversion de dependencias, nuestro EntradaDecorator, Entrada o CompraEstate
haciendo al codigo depender de abstracciones y no de clases concretas.

