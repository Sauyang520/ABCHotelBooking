// Programmer Name: Mr. Sim Sau Yang
// Program Name: Login Page Controller
// First Written on: 16 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.Main;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The login controller verifies the staff account to proceed to the main page.
 */
public class LoginController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    @FXML
    private TextField textFieldAccID;
    @FXML
    private PasswordField passwordFieldAccPassword;
    @FXML
    private Label labelWarning;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // When press enter on account field, go to password field
        textFieldAccID.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        passwordFieldAccPassword.requestFocus();
                    }
                }
        );

        // When press enter on password field, run login method to verified
        passwordFieldAccPassword.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        login(keyEvent);
                    }
                }
        );
    }
    
    public void login(Event event) {
        LOGGER.debug("Login Button is clicked.");
        String accountID = textFieldAccID.getText();
        String accountPassword = passwordFieldAccPassword.getText();
        LOGGER.info("Entered Account: " + accountID + " | Password: " + accountPassword);

        if (Main.STAFF_ACCOUNT.containsKey(accountID)) {
            if (Main.STAFF_ACCOUNT.get(accountID).equals(accountPassword)) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com.hotelbooking.gui/MainPage.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(loader.load());
                    stage.setScene(scene);
                    stage.setTitle("ABC Hotel");
                    stage.centerOnScreen();
                    stage.show();
                    LOGGER.trace("Enter Main Page.");
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Cannot Open Main Page: " + e);
                }
            } else {
                passwordFieldAccPassword.clear();
                labelWarning.setText("Please enter correct password!");
                LOGGER.warn("Password Incorrect.");
            }
        } else {
            textFieldAccID.clear();
            passwordFieldAccPassword.clear();
            textFieldAccID.requestFocus();
            labelWarning.setText("Please enter correct username!");
            LOGGER.warn("Username Incorrect.");
        }
    }
}
