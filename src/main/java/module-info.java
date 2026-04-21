module co.edu.uniquindio.pgii.plataforma_eventos {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.pgii.plataforma_eventos to javafx.fxml;
    exports co.edu.uniquindio.pgii.plataforma_eventos;
}