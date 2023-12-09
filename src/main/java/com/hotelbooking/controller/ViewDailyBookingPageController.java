// Programmer Name: Mr. Sim Sau Yang
// Program Name: View Daily Booking Page Controller
// First Written on: 18 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.bean.Customer;
import com.hotelbooking.utils.HotelBookingUtil;
import com.hotelbooking.utils.TableViewCheckOutAndViewBooking;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class ViewDailyBookingPageController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewDailyBookingPageController.class);
    @FXML
    private TableView<TableViewCheckOutAndViewBooking> tableViewDailyBooking;
    @FXML
    private TableColumn<TableViewCheckOutAndViewBooking, String> columnCheckIn, columnCheckOut, columnName
            , columnReceiptId, columnRoomId;
    @FXML
    private DatePicker datePickerDate;
    @FXML
    private Label labelWarning;

    private final List<Customer> customerDetails = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up TableView
        columnRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        columnReceiptId.setCellValueFactory(new PropertyValueFactory<>("receiptID"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        columnCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        // Get the customer data from txt file and put into Customer Object in a list
        HotelBookingUtil.collectCustomers(customerDetails);
    }

    @FXML
    public void viewDailyBooking() {
        tableViewDailyBooking.getItems().clear();
        labelWarning.setText("");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate selectDate = datePickerDate.getValue();

        LOGGER.debug("Date is selected: " + selectDate);
        String filename = dateTimeFormatter.format(selectDate) + ".txt";
        if (HotelBookingUtil.searchFile(filename)) {
            List<List<String>> cleanBookingsOfTheDay = new ArrayList<>();
            HotelBookingUtil.collectData(filename).forEach(s ->
                    cleanBookingsOfTheDay.add(HotelBookingUtil.splitData(s, Pattern.quote("#"))));

            Map<List<String>, String> toBeShow = new HashMap<>();
            for (List<String> booking : cleanBookingsOfTheDay) {
                int key = -1;
                for (Customer customer : customerDetails) {
                    if (booking.get(2).equals("null")) {
                        // Get Customer from IC
                        if (booking.get(1).equals(customer.getIcNumber())) {
                            toBeShow.put(booking, customer.getCustomerName());
                            key = 1;
                        }
                    } else if (booking.get(1).equals("null")) {
                        // Get Customer from PASSPORT
                        if (booking.get(2).equals(customer.getPassportNumber())) {
                            toBeShow.put(booking, customer.getCustomerName());
                            key = 1;
                        }
                    }

                }
                if (key < 0) {
                    // Can't find the customer
                    toBeShow.put(booking, "Null");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Some customer information might been modified!");
                    alert.setContentText("The modified customer name column shows the name 'Null'.");
                    alert.showAndWait();
                    labelWarning.setText("Some customer information might be changed.");
                }

            }
            ObservableList<TableViewCheckOutAndViewBooking> bookings = tableViewDailyBooking.getItems();
            toBeShow.forEach((booking, customer) ->
                bookings.add(new TableViewCheckOutAndViewBooking(booking.get(3), null, booking.get(4)
                        , booking.get(5), booking.get(6), Double.valueOf(booking.get(7)), booking.get(0), customer))
            );
            tableViewDailyBooking.setItems(bookings);
            LOGGER.trace("Booking Shown.");
        } else {
            labelWarning.setText("No booking on the day.");
            LOGGER.warn("No booking found on the day.");
        }
    }

    /**
     * Go back to Main Page
     */
    @FXML
    public void goBack(ActionEvent event) {
        LOGGER.debug("Back Button is clicked.");
        // Back to Main Page
        try {
            Class<? extends ViewDailyBookingPageController> currentClass = getClass();
            HotelBookingUtil.goToMainPage(event, LOGGER, currentClass);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Cannot Back to Main Page: " + e);
        }
    }
}
