// Programmer Name: Mr. Sim Sau Yang
// Program Name: About Page Controller
// First Written on: 20 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Show information about ABC Hotel Booking System
 */
public class AboutPageController implements Initializable {

    @FXML
    private TextArea textAreaAbout;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textAreaAbout.setText("""
                @author Sim Sau Yang (TP065596)
                
                "ABC Hotel Booking System is created by @author as the assignment project of Introduction to Java Programming led by Mr. Usman.
                
                "Project Requirements:
                "Design and develop a room booking system for a hotel. The main purpose of this system is to simulate room booking in a week for a small hotel with only two floors with each floor consisting of 10 rooms. The charge per room is RM350.00 per night. The system will be used by hotel staff.
             
                "@author:
                "This is an interesting and challenging module. I have learned a lot of new functions and understood clearer the concept of OOP and JavaFX. I also learn to do logging to record the users' trace which I believe would be useful for future data analysis and system optimization. I enjoy coding this system. Hope you enjoy using ABC too >.<""");

        textAreaAbout.setDisable(true);
    }
}
