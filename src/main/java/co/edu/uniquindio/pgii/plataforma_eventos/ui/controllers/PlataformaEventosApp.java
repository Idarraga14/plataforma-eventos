package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers;

import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.PlataformaEventosSingleton;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX principal: configura el {@code Stage} e inicia la vista de Login.
 *
 * <p>Actúa como punto de arranque lógico del sistema JavaFX. Su única responsabilidad
 * en {@link #start(Stage)} es: (1) forzar la inicialización eaguer del
 * {@code PlataformaEventosSingleton} (y por ende la carga de datos de prueba),
 * (2) configurar la ventana principal, y (3) delegar la carga de la primera vista
 * a {@link co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator}.</p>
 *
 * <p>[Requerimiento: RF-045] - Desencadena la inicialización del Singleton y los datos
 * de prueba al arrancar la aplicación.</p>
 * <p>[Patrón: Singleton] - La llamada a {@code PlataformaEventosSingleton.getInstance()}
 * fuerza la creación de la instancia única antes de cargar cualquier controlador.</p>
 */
public class PlataformaEventosApp extends Application {
    @Override
    public void start(Stage primaryStage) {

        // 1. "Despertar" la base de datos (Singleton)
        // Al llamar a getInstance(), forzamos la ejecución del constructor
        // y la inicialización de los datos de prueba (usuarios, recintos, etc.)
        PlataformaEventosSingleton.getInstance();

        // 2. Configurar la ventana principal (Stage)
        primaryStage.setTitle("Plataforma de Gestión de Eventos");
        primaryStage.setResizable(false); // Recomendado para evitar descuadres en el diseño inicial

        // 3. Delegar la carga de la primera pantalla a nuestro enrutador
        // El punto de entrada lógico de cualquier sistema es el Login
        ViewNavigator.cargarVistaUsuario("LoginView.fxml", primaryStage);
    }
}
