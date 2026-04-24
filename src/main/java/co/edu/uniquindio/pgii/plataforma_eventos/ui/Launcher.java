package co.edu.uniquindio.pgii.plataforma_eventos.ui;

import co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.PlataformaEventosApp;
import javafx.application.Application;

/**
 * Punto de entrada ejecutable de la aplicación JavaFX.
 *
 * <p>Delega el arranque de JavaFX a {@link PlataformaEventosApp}, separando el
 * {@code main()} ejecutable de la subclase de {@link Application}. Esta separación
 * es necesaria cuando el artefacto se empaqueta como JAR sin módulos explícitos,
 * ya que algunos lanzadores no reconocen directamente subclases de {@code Application}
 * como clase principal.</p>
 *
 * <p>[Requerimiento: RF-045] - Actúa como punto de arranque que desencadena la
 * inicialización del {@code PlataformaEventosSingleton} y la carga de datos de prueba.</p>
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(PlataformaEventosApp.class, args);
    }
}
