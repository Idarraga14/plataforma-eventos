module co.edu.uniquindio.pgii.plataforma_eventos {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.uniquindio.pgii.plataforma_eventos to javafx.fxml;
    exports co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers;
    opens co.edu.uniquindio.pgii.plataforma_eventos.ui.controllers to javafx.fxml;
    exports co.edu.uniquindio.pgii.plataforma_eventos.ui;
    opens co.edu.uniquindio.pgii.plataforma_eventos.ui to javafx.fxml;
}