// Programmer Name: Mr. Sim Sau Yang
// Program Name: Help Page Controller
// First Written on: 20 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Show function and rule of ABC Hotel Booking System.
 */
public class HelpPageController implements Initializable {

    @FXML
    private TextArea textAreaHelp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textAreaHelp.setText("""
                1. One Booking allows many different rooms with different dates and day(s) of stay.

                2. The same room can be booked twice or more in one booking if only the days of the room booked do not conflict with each other.

                3. ABC Hotel Booking System functions:
                    a. Staff Login
                    b. Rooms Booking
                    c. Manage Customer Information
                    d. Manage Bookings
                    e. View Dailly Bookings
                    f. Export Receipt and Report

                4. ABC Hotel rules:
                    a. The customer must check in after 1400 (GMT+8) of the check-in date.
                    b. The customer must check out before 1200 (GMT+8) of the check-out date.
                    c. The tourism tax is only applied to foreigners who register using PASSPORT NUMBER.""");
    }
}
