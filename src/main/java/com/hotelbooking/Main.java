package com.hotelbooking;

import com.hotelbooking.bean.Room;
import com.hotelbooking.bean.Staff;
import com.hotelbooking.utils.HotelBookingUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The Main Class to run the ABC Hotel Booking System. The first GUI is Login Page.
 * The Staff account and Room are initialized in this class.
 */
public class Main extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static final Map<String, String> STAFF_ACCOUNT = new HashMap<>();
    public static final List<Room> ROOMS = new ArrayList<>();
    public static final Set<String> ROOM_TYPE = new HashSet<>();


    static {
        // Prepare the Staff Account; Username and Password
        List<String> staffList = HotelBookingUtil.collectData("Staff.txt");
        for (String s : staffList) {
            List<String> staffData = HotelBookingUtil.splitData(s, "#");
            Staff staff = new Staff(staffData.get(0), staffData.get(1));
            STAFF_ACCOUNT.put(staff.getUsername(), staff.getPassword());
        }
        LOGGER.trace("Staff account is prepared.");

        // Prepare the Rooms
        List<String> roomsList = HotelBookingUtil.collectData("Room.txt");
        ROOM_TYPE.add("All");
        for (String s : roomsList) {
            List<String> roomData = HotelBookingUtil.splitData(s, "#");
            Room room = new Room(roomData.get(0), roomData.get(1), roomData.get(2), Double.parseDouble(roomData.get(3)));
            ROOMS.add(room);
            ROOM_TYPE.add(roomData.get(2));
        }
        LOGGER.trace("Rooms Information is prepared.");
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com.hotelbooking.gui/LoginPage.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            stage.setTitle("Login");
            Image icon = new Image(getClass().getResourceAsStream("/ABCHotelInfo/picture/HotelLogo.png"));
            stage.getIcons().add(icon);
            stage.centerOnScreen();
            stage.show();
            stage.setResizable(false);
            LOGGER.trace("Enter Login Page.");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open Login Page: " + e);
        }

        stage.setOnCloseRequest(event -> {
            LOGGER.debug("'X' (Close) Button is clicked.");
            event.consume();
            logout(stage);
        });
    }

    public void logout(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close");
        alert.setHeaderText("You are about to close the system!");
        alert.setContentText("Click OK to end program.");
        alert.getDialogPane();
        if (alert.showAndWait().get() == ButtonType.OK) {
            LOGGER.debug("'OK' is clicked. End Program.");
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
