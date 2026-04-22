package co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers;

import co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.PlataformaEventosSingleton;
import co.edu.uniquindio.pgii.plataforma_eventos.ui.util.ViewNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

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
