module com.hotelbooking {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    opens com.hotelbooking.bean to javafx.base;
    opens com.hotelbooking.utils to java.base;

    opens com.hotelbooking to javafx.fxml;
    exports com.hotelbooking;

    opens com.hotelbooking.controller to javafx.fxml;
    exports com.hotelbooking.controller;
}