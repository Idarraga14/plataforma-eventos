package co.edu.uniquindio.pgii.plataforma_eventos.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewNavigator {
    // Rutas base para no repetir texto
    private static final String RUTA_USUARIO = "/co/edu/uniquindio/pgii/plataforma_eventos/views/usuario/";
    private static final String RUTA_ADMIN = "/co/edu/uniquindio/pgii/plataforma_eventos/views/admin/";

    /**
     * Carga una vista en la ventana actual.
     */
    public static void cargarVistaUsuario(String fxmlName, Stage stage) {
        cargarVista(RUTA_USUARIO + fxmlName, stage);
    }

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
