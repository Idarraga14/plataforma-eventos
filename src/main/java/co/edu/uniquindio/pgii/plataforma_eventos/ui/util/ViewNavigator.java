package co.edu.uniquindio.pgii.plataforma_eventos.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utilidad estática de navegación entre vistas FXML de la aplicación JavaFX.
 *
 * <p>Centraliza la lógica de carga de FXML (rutas, {@link FXMLLoader}, creación de
 * {@link Scene} y centrado del {@link Stage}) en un único punto, evitando que los
 * controladores repitan bloques try-catch de {@code FXMLLoader} en cada transición.</p>
 *
 * <p>Las rutas base de vistas de usuario ({@code /views/usuario/}) y de admin
 * ({@code /views/admin/}) están definidas como constantes privadas; los controladores
 * sólo pasan el nombre del archivo FXML (ej. {@code "LoginView.fxml"}).</p>
 *
 * <p>[Requerimiento: RF-001] - Soporta la navegación Login → Explorar/Dashboard.</p>
 * <p>[Requerimiento: RF-002] - Soporta la navegación a la vista de exploración de eventos.</p>
 */
public class ViewNavigator {
    private static final String RUTA_USUARIO = "/co/edu/uniquindio/pgii/plataforma_eventos/views/usuario/";
    private static final String RUTA_ADMIN = "/co/edu/uniquindio/pgii/plataforma_eventos/views/admin/";

    /**
     * Carga un FXML del módulo de usuario en el {@code Stage} dado.
     *
     * @param fxmlName nombre del archivo FXML (p.ej. {@code "LoginView.fxml"})
     * @param stage    ventana principal donde se mostrará la nueva escena
     */
    public static void cargarVistaUsuario(String fxmlName, Stage stage) {
        cargarVista(RUTA_USUARIO + fxmlName, stage);
    }

    /**
     * Carga un FXML del módulo de administración en el {@code Stage} dado.
     *
     * @param fxmlName nombre del archivo FXML (p.ej. {@code "AdminDashboardView.fxml"})
     * @param stage    ventana principal donde se mostrará la nueva escena
     */
    public static void cargarVistaAdmin(String fxmlName, Stage stage) {
        cargarVista(RUTA_ADMIN + fxmlName, stage);
    }

    private static void cargarVista(String rutaFxml, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewNavigator.class.getResource(rutaFxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Error crítico al cargar la vista: " + rutaFxml);
            e.printStackTrace();
        }
    }
}
