// Programmer Name: Mr. Sim Sau Yang
// Program Name: Customer Details Page Controller
// First Written on: 18 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.bean.Customer;
import com.hotelbooking.bean.Room;
import com.hotelbooking.utils.DataHolder;
import com.hotelbooking.utils.HotelBookingUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Customer Details Page allows the staff to enter the customer details.
 */
public class CustomerDetailsPageController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDetailsPageController.class);
    @FXML
    private TextField textFieldCustomerName, textFieldCustomerId, textFieldContactNumber, textFieldEmail;
    @FXML
    private ListView<String> listViewRoomBookingData;
    @FXML
    private RadioButton radioButtonIcNumber, radioButtonPassportNumber;
    @FXML
    private Label labelNotice;

    private Map<List<String>, Room> selectedRoomsData;
    private final List<Customer> customers = new ArrayList<>();

    private String customerPosition = NEW_CUSTOMER;
    public static final String OLD_CUSTOMER = "oldCustomer";
    public static final String NEW_CUSTOMER = "newCustomer";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        radioButtonIcNumber.setSelected(true);
        // Let the key press ENTER bring convenience to the user
        textFieldCustomerId.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        textFieldCustomerName.requestFocus();
                        getCustomerID();
                    }
                }
        );
        textFieldCustomerName.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        textFieldContactNumber.requestFocus();
                    }
                }
        );
        textFieldContactNumber.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        textFieldEmail.requestFocus();
                    }
                }
        );
        textFieldEmail.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        checkOut(keyEvent);
                    }
                }
        );

        // Set Up List View
        selectedRoomsData = DataHolder.getInstance().getMapRoomBookingData();
        selectedRoomsData.forEach((strings, room) ->
                listViewRoomBookingData.getItems().add(room.getRoomID() + ": " + strings.get(1) + " -> " + strings.get(2)));
        LOGGER.trace("Selected Room(s) is added into listView.");

        // Get Customer Data
        if (HotelBookingUtil.searchFile("Customer.txt")) {
            List<String> customersInfo = HotelBookingUtil.collectData("Customer.txt");
            if (customersInfo.size() > 0) {
                for (String customer : customersInfo) {
                    List<String> customerData = HotelBookingUtil.splitData(customer, Pattern.quote("#"));
                    customers.add(new Customer(customerData.get(0), customerData.get(1), customerData.get(2)
                            , customerData.get(3), customerData.get(4)));
                }
                LOGGER.trace("Customer data is loaded from Customer.txt.");
            }
        }
    }

    /**
     * Proceed to checkout page if the information is completed.
     */
    @FXML
    public void checkOut(Event event) {
        LOGGER.debug("Checkout Button is clicked.");
        getCustomerID();
        // Some text fields is not filled
        if (textFieldCustomerName.getText().trim().isEmpty() || textFieldCustomerId.getText().trim().isEmpty()
                || textFieldContactNumber.getText().trim().isEmpty() || textFieldEmail.getText().trim().isEmpty()) {
            labelNotice.setText("Please complete the information.");
            LOGGER.warn("The customer information is not completed.");
        } else if (radioButtonIcNumber.isSelected() && textFieldCustomerId.getText().matches("\\d{12}")) {
            // Validate IC Number
            try {
                Customer bookingCustomer = new Customer(textFieldCustomerId.getText(), "null",
                        textFieldCustomerName.getText(), textFieldContactNumber.getText(), textFieldEmail.getText());

                goToCheckOut(bookingCustomer, event);
                LOGGER.trace("Enter Check Out Page.");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Cannot Open Check Out Page: " + e);
            }
        } else if (radioButtonPassportNumber.isSelected() && textFieldCustomerId.getText().matches("[A-Z0-9]{2}\\d{7}")) {
            // Validate Passport Number
            Customer bookingCustomer = new Customer("null", textFieldCustomerId.getText()
                    , textFieldCustomerName.getText(), textFieldContactNumber.getText(), textFieldEmail.getText());
            try {
                goToCheckOut(bookingCustomer, event);
                LOGGER.trace("Enter Check Out Page.");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Cannot Open Check Out Page: " + e);
            }
        } else {
            if (radioButtonIcNumber.isSelected()) {
                labelNotice.setText("Please enter proper IC number.");
                LOGGER.warn("The IC number is incorrect.");
            } else if (radioButtonPassportNumber.isSelected()) {
                labelNotice.setText("Please enter proper passport number.");
                LOGGER.warn("The Passport number is incorrect.");
            } else {
                labelNotice.setText("Please select IC Number or Passport Number.");
                LOGGER.warn("The radioButton IC / Passport is not selected.");
            }
        }
    }

    /**
     * The selected RadioButton is defined to use icNumber or passportNumber
     */
    @FXML
    public void getCustomerID() {
        labelNotice.setText("");
        if (radioButtonIcNumber.isSelected()) {
            LOGGER.debug("RadioButton IC is selected.");
            if (HotelBookingUtil.searchFile("Customer.txt")) {
                for (Customer customer : customers) {
                    // The customer existed
                    if (customer.getIcNumber().equals(textFieldCustomerId.getText())) {
                        textFieldCustomerName.setText(customer.getCustomerName());
                        textFieldContactNumber.setText(customer.getContactNumber());
                        textFieldEmail.setText(customer.getEmail());
                        labelNotice.setText("He/She is our old customer!");
                        customerPosition = OLD_CUSTOMER;
                        LOGGER.info("Customer Info is found on existed file: " + customer);
                    }
                }
            } else {
                // No customer data in file
                labelNotice.setText("Opps! He/She is our new customer! Enter customer information to complete booking.");
                LOGGER.trace("Customer not found from file.");
            }
        } else if (radioButtonPassportNumber.isSelected()) {
            LOGGER.debug("RadioButton Passport is selected.");
            if (HotelBookingUtil.searchFile("Customer.txt")) {
                for (Customer customer : customers) {
                    // The customer existed
                    if (customer.getPassportNumber().equals(textFieldCustomerId.getText())) {
                        textFieldCustomerName.setText(customer.getCustomerName());
                        textFieldContactNumber.setText(customer.getContactNumber());
                        textFieldEmail.setText(customer.getEmail());
                        labelNotice.setText("He/She is our old customer!");
                        customerPosition = OLD_CUSTOMER;
                        LOGGER.info("Customer Info is found on existed file: " + customer);
                    }
                }
            } else {
                // No customer data in file
                labelNotice.setText("Opps! He/She is our new customer! Enter customer information to complete booking.");
                LOGGER.trace("Customer not found from file.");
            }
        }
    }

    @FXML
    public void refresh() {
        LOGGER.debug("Refresh Button is clicked.");
        getCustomerID();
    }

    /**
     * Go back to select room page
     */
    @FXML
    public void goBack(ActionEvent event) {
        LOGGER.debug("Back Button is clicked.");
        try {
            // Back to Rooms Booking Page
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com.hotelbooking.gui/RoomsBookingPage.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Rooms Booking");
            stage.show();
            LOGGER.trace("Back to Booking Page.");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open Booking Page.");
        }
    }

    public void goToCheckOut(Customer bookingCustomer, Event event) throws IOException {
        LOGGER.info("Customer Info: " + bookingCustomer);
        DataHolder.getInstance().setCustomerData(bookingCustomer);
        DataHolder.getInstance().setMapRoomBookingData(selectedRoomsData);
        DataHolder.getInstance().setStringCustomerPositionData(customerPosition);

        // Go to Check Out
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com.hotelbooking.gui/CheckOutPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Checkout");
        stage.show();
    }
}
