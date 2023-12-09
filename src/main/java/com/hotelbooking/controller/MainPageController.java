// Programmer Name: Mr. Sim Sau Yang
// Program Name: Main Page Controller
// First Written on: 16 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Provide buttons for staff to navigate to corresponding page
 */
public class MainPageController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainPageController.class);
    @FXML
    MenuBar menuBar;
    @FXML
    ImageView imageViewHotel, imageViewLogo;

    private final Image icon = new Image(getClass().getResourceAsStream("/ABCHotelInfo/picture/HotelLogo.png"));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image logo = new Image(getClass().getResourceAsStream("/ABCHotelInfo/picture/MainPageBackground.jpg"));
        imageViewHotel.setImage(logo);
        imageViewLogo.setImage(icon);
    }

    /**
     * Show Help Page
     */
    @FXML
    public void goHelp() {
        LOGGER.debug("Select -> Help is clicked.");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com.hotelbooking.gui/HelpPage.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Help");
            stage.getIcons().add(icon);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
            LOGGER.trace("Enter Help Page.");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open Help Page: " + e);
        }
    }

    /**
     * Show About Page
     */
    @FXML
    public void showAbout() {
        LOGGER.debug("Select -> About is clicked.");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com.hotelbooking.gui/AboutPage.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("About");
            stage.getIcons().add(icon);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
            LOGGER.trace("Enter About Page.");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open About Page: " + e);
        }
    }

    /**
     * Go to Room Booking page
     */
    @FXML
    public void goBooking() {
        LOGGER.debug("Booking Now Button / Select -> Rooms Booking is clicked.");
        try {
            goToNextPage("/com.hotelbooking.gui/RoomsBookingPage.fxml", "Rooms Booking");
            LOGGER.trace("Enter Rooms Booking Page.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Enter Rooms Booking Page: " + e);
        }
    }

    /**
     * Go to manage customer page
     */
    @FXML
    public void goManageCustomer() {
        LOGGER.debug("Manage Customer Details Button / Select -> Manage Customer Details is clicked.");
        try {
            goToNextPage("/com.hotelbooking.gui/ManageCustomersPage.fxml", "Manage Customer Information");
            LOGGER.trace("Enter Manage Customers Page.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Enter Manage Customer Page: " + e);
        }
    }

    /**
     * Go to manage booking page
     */
    @FXML
    public void goManageBooking() {
        LOGGER.debug("Manage Booking Details Button / Select -> Manage Booking Details is clicked.");
        try {
            goToNextPage("/com.hotelbooking.gui/ManageBookingPage.fxml", "Manage & View Receipt");
            LOGGER.trace("Enter Manage Booking Page.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open Manage Booking Page: " + e);
        }
    }

    /**
     * Go to view daily booking page
     */
    @FXML
    public void viewDailyBooking() {
        LOGGER.debug("View Daily Booking Button / Select -> View Daily Booking is clicked.");
        try {
            goToNextPage("/com.hotelbooking.gui/ViewDailyBookingPage.fxml", "View Daily Bookings");
            LOGGER.trace("Enter View Daily Bookings Page.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open View Daily Bookings Page: " + e);
        }
    }

    /**
     * Back to login page
     */
    @FXML
    public void logout() {
        LOGGER.debug("Select -> Logout is clicked.");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout!");
        alert.setContentText("Click OK to logout.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            LOGGER.debug("'OK' Button is clicked.");
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com.hotelbooking.gui/LoginPage.fxml"));
                Stage stage = (Stage) menuBar.getScene().getWindow();
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.setTitle("Login");
                stage.show();
                stage.centerOnScreen();
                LOGGER.trace("Logout to Login Page.");
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Cannot Logout: " + e);
            }
        }
    }

    public void goToNextPage(String fxmlFileName, String stageTitle) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFileName));
        Stage stage = (Stage) menuBar.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.show();
    }
}

