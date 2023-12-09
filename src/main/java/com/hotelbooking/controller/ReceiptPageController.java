// Programmer Name: Mr. Sim Sau Yang
// Program Name: Receipt Page Controller
// First Written on: 19 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.bean.BookingDetails;
import com.hotelbooking.utils.DataHolder;
import com.hotelbooking.utils.HotelBookingUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Show the receipt Details.
 */
public class ReceiptPageController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptPageController.class);
    @FXML
    Label labelDateTime, labelReceiptId, labelCustomerName, labelPaymentMethod, labelSubTotal, labelServiceTax, labelTourismTax, labelTotal;
    @FXML
    TextArea textAreaRoomId, textAreaDaysOfStay, textAreaCheckInDate, textAreaCheckOutDate, textAreaAmount;

    private BookingDetails bookingDetails;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookingDetails = DataHolder.getInstance().getBookingDetailsData();
        // Paste all data to receipt
        labelDateTime.setText(bookingDetails.getBookingConfirmDateTime());
        labelReceiptId.setText(bookingDetails.getReceiptId());
        labelCustomerName.setText(bookingDetails.getCustomerName());

        bookingDetails.getRoomDetails().forEach((strings, s) -> {
            textAreaRoomId.setText(textAreaRoomId.getText() + s + "\n");
            textAreaDaysOfStay.setText(textAreaDaysOfStay.getText() + strings.get(0) + "\n");
            textAreaCheckInDate.setText(textAreaCheckInDate.getText() + strings.get(1) + "\n");
            textAreaCheckOutDate.setText(textAreaCheckOutDate.getText() + strings.get(2) + "\n");
            textAreaAmount.setText(textAreaAmount.getText() + strings.get(3) + "\n");
        });

        labelPaymentMethod.setText(bookingDetails.getPaymentMethod());
        labelSubTotal.setText(bookingDetails.getSubTotal() + "");
        labelServiceTax.setText(bookingDetails.getServiceTax() + "");
        labelTourismTax.setText(bookingDetails.getTourismTax() + "");
        labelTotal.setText(bookingDetails.getTotalPrice() + "");

        LOGGER.trace("Booking Details filled into receipt.");

        Stage stage = DataHolder.getInstance().getStageData();
        stage.setOnCloseRequest(event -> {
            LOGGER.debug("'X' Cancel Button is clicked.");
            event.consume();
            saveNotice(stage);
        });
    }

    /**
     * Save the receipt as .png image in the Receipt directory and return to main page.
     */
    @FXML
    public void save(ActionEvent event) {
        LOGGER.debug("Save Button is clicked.");
        String filename = "src/main/resources/ABCHotelInfo/receipt/" + bookingDetails.getReceiptId() + ".png";
        HotelBookingUtil.savePNGAndClose(filename, event, LOGGER);
    }

    public void saveNotice(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Receipt");
        alert.setHeaderText("Your receipt is not saved!");
        alert.setContentText("Click OK to close.\nClick Cancel -> Save to save receipt.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            LOGGER.debug("'OK' Button is clicked.");
            DataHolder.deleteInstance();
            stage.close();
            LOGGER.trace("Close Receipt Page.");
        }
    }
}
