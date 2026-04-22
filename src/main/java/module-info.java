module co.edu.uniquindio.pgii.plataforma_eventos {
    requires javafx.controls;
    requires javafx.fxml;

    // Clase principal de la aplicación (JavaFX Application)
    exports co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers;
    opens co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers to javafx.fxml, javafx.graphics;

    // Controladores de perfil Usuario
    exports co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario;
    opens co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.usuario to javafx.fxml;

    // Controladores de perfil Administrador
    exports co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin;
    opens co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers.admin to javafx.fxml;

    exports co.edu.uniquindio.pgii.plataforma_eventos.ui;
    opens co.edu.uniquindio.pgii.plataforma_eventos.ui to javafx.fxml;
}