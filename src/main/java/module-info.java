module umg.edu.proyectobd {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.desktop;
    requires itextpdf;

    opens umg.edu.proyectobd to javafx.fxml;
    exports umg.edu.proyectobd;
    exports umg.edu.proyectobd.Controllers;
    opens umg.edu.proyectobd.Controllers to javafx.fxml;
    exports umg.edu.proyectobd.DB;
    opens umg.edu.proyectobd.DB to javafx.fxml;
}