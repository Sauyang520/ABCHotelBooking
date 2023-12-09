// Programmer Name: Mr. Sim Sau Yang
// Program Name: Manage Customer Page Controller
// First Written on: 17 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.bean.Customer;
import com.hotelbooking.utils.HotelBookingUtil;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * To manage customer info.
 * 1. View
 * 2. Add
 * 3. Modify
 * 4. Delete
 */
public class ManageCustomersPageController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageCustomersPageController.class);
    @FXML
    private RadioButton radioButtonIc, radioButtonPassport, radioButtonName, radioButtonContact, radioButtonEmail, radioButtonAllCustomer, radioButtonNewIc, radioButtonNewPassport;
    @FXML
    private final ToggleGroup groupNew = new ToggleGroup();
    @FXML
    private final ToggleGroup groupSearch = new ToggleGroup();
    @FXML
    private TextField textFieldSearch, textFieldCustomerName, textFieldCustomerId, textFieldContactNumber, textFieldEmail;
    @FXML
    private TableView<Customer> tableViewCustomer;
    @FXML
    private TableColumn<Customer, String> columnIc, columnPassport, columnName, columnContact, columnEmail;
    @FXML
    private CheckBox checkBoxIc, checkBoxPassport;
    @FXML
    private Label labelWarning;

    private final List<Customer> customerDetails = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get the customer data from txt file and put into Customer Object in a list
        HotelBookingUtil.collectCustomers(customerDetails);

        // Set up tableView
        columnIc.setCellValueFactory(new PropertyValueFactory<>("icNumber"));
        columnPassport.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Set up toggle group
        radioButtonIc.setToggleGroup(groupSearch);
        radioButtonPassport.setToggleGroup(groupSearch);
        radioButtonName.setToggleGroup(groupSearch);
        radioButtonContact.setToggleGroup(groupSearch);
        radioButtonEmail.setToggleGroup(groupSearch);
        radioButtonAllCustomer.setToggleGroup(groupSearch);

        radioButtonNewIc.setToggleGroup(groupNew);
        radioButtonNewPassport.setToggleGroup(groupNew);

        // Add Listener to table for Modify Function
        tableViewCustomer.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Customer> c) -> {
            Customer selectedC = tableViewCustomer.getSelectionModel().getSelectedItem();
            if (selectedC != null) {
                LOGGER.debug("Customer is selected: " + selectedC);

                textFieldCustomerName.setText(selectedC.getCustomerName());
                textFieldContactNumber.setText(selectedC.getContactNumber());
                textFieldEmail.setText(selectedC.getEmail());
                if (!selectedC.getIcNumber().equals("null")) {
                    textFieldCustomerId.setText(selectedC.getIcNumber());
                    radioButtonNewIc.setSelected(true);
                } else if (!selectedC.getPassportNumber().equals("null")) {
                    textFieldCustomerId.setText(selectedC.getPassportNumber());
                    radioButtonNewPassport.setSelected(true);
                }
            } else {
                textFieldCustomerId.clear();
                textFieldCustomerName.clear();
                textFieldContactNumber.clear();
                textFieldEmail.clear();
            }
        });
    }

    /**
     * To uncheck the choice box of view all customer
     */
    @FXML
    public void unCheck() {
        if (radioButtonIc.isSelected() || radioButtonPassport.isSelected() || radioButtonName.isSelected()
                || radioButtonContact.isSelected() || radioButtonEmail.isSelected()) {
            setCheckBoxStatus(false, true);
        } else if (radioButtonAllCustomer.isSelected()) {
            setCheckBoxStatus(true, false);
        }
    }

    /**
     * To search the customer using name, ic, passport, contact, email, and view all customer data
     */
    @FXML
    public void refresh() {
        LOGGER.debug("Refresh Button is clicked.");
        // Clear the text field
        textFieldCustomerName.clear();
        textFieldCustomerId.clear();
        textFieldContactNumber.clear();
        textFieldEmail.clear();

        if (customerDetails.size() > 0) {
            if (!textFieldSearch.getText().equalsIgnoreCase("null")) {
                if (radioButtonIc.isSelected()) {
                    labelWarning.setText("");
                    tableViewCustomer.getItems().clear();
                    int key = -1;
                    ObservableList<Customer> customers = tableViewCustomer.getItems();
                    for (Customer customerDetail : customerDetails) {
                        if (customerDetail.getIcNumber().equals(textFieldSearch.getText())) {
                            customers.add(customerDetail);
                            tableViewCustomer.setItems(customers);
                            key = 1;
                            LOGGER.trace("Customer found by IC.");
                            break;
                        }
                    }
                    if (key == -1) {
                        labelWarning.setText("Customer Not Found.");
                        LOGGER.warn("Customer Not Found.");
                    }
                } else if (radioButtonPassport.isSelected()) {
                    labelWarning.setText("");
                    tableViewCustomer.getItems().clear();
                    int key = -1;
                    ObservableList<Customer> customers = tableViewCustomer.getItems();
                    for (Customer customerDetail : customerDetails) {
                        if (customerDetail.getPassportNumber().equals(textFieldSearch.getText())) {
                            customers.add(customerDetail);
                            tableViewCustomer.setItems(customers);
                            key = 1;
                            LOGGER.trace("Customer found by passport.");
                            break;
                        }
                    }
                    if (key == -1) {
                        labelWarning.setText("Customer Not Found.");
                        LOGGER.warn("Customer Not Found.");
                    }
                } else if (radioButtonName.isSelected()) {
                    labelWarning.setText("");
                    tableViewCustomer.getItems().clear();
                    int key = -1;
                    ObservableList<Customer> customers = tableViewCustomer.getItems();
                    for (Customer customerDetail : customerDetails) {
                        if (customerDetail.getCustomerName().toLowerCase().contains(textFieldSearch.getText().toLowerCase())) {
                            customers.add(customerDetail);
                            tableViewCustomer.setItems(customers);
                            key = 1;
                            LOGGER.trace("Customer found by name.");
                        }
                    }
                    if (key == -1) {
                        labelWarning.setText("Customer Not Found.");
                        LOGGER.warn("Customer Not Found.");
                    }
                } else if (radioButtonContact.isSelected()) {
                    labelWarning.setText("");
                    tableViewCustomer.getItems().clear();
                    int key = -1;
                    ObservableList<Customer> customers = tableViewCustomer.getItems();
                    for (Customer customerDetail : customerDetails) {
                        if (customerDetail.getContactNumber().contains(textFieldSearch.getText())) {
                            customers.add(customerDetail);
                            tableViewCustomer.setItems(customers);
                            key = 1;
                            LOGGER.trace("Customer found by contact.");
                        }
                    }
                    if (key == -1) {
                        labelWarning.setText("Customer Not Found.");
                        LOGGER.warn("Customer Not Found.");
                    }
                } else if (radioButtonEmail.isSelected()) {
                    labelWarning.setText("");
                    tableViewCustomer.getItems().clear();
                    int key = -1;
                    ObservableList<Customer> customers = tableViewCustomer.getItems();
                    for (Customer customerDetail : customerDetails) {
                        if (customerDetail.getEmail().contains(textFieldSearch.getText())) {
                            customers.add(customerDetail);
                            tableViewCustomer.setItems(customers);
                            key = 1;
                            LOGGER.trace("Customer found by email.");
                        }
                    }
                    if (key == -1) {
                        labelWarning.setText("Customer Not Found.");
                        LOGGER.warn("Customer Not Found.");
                    }
                } else if (radioButtonAllCustomer.isSelected()) {
                    labelWarning.setText("");
                    tableViewCustomer.getItems().clear();
                    ObservableList<Customer> customers = tableViewCustomer.getItems();
                    List<Customer> toShow = new ArrayList<>();
                    if (checkBoxIc.isSelected()) {
                        for (Customer customerDetail : customerDetails) {
                            if (customerDetail.getPassportNumber().equals("null")) {
                                toShow.add(customerDetail);
                            }
                        }
                    }
                    if (checkBoxPassport.isSelected()) {
                        for (Customer customerDetail : customerDetails) {
                            if (customerDetail.getIcNumber().equals("null")) {
                                toShow.add(customerDetail);
                            }
                        }
                    }
                    for (Customer customerDetail : toShow) {
                        customers.add(customerDetail);
                        tableViewCustomer.setItems(customers);
                    }
                    textFieldSearch.clear();
                    LOGGER.trace("All customers' details are shown.");
                } else {
                    labelWarning.setText("Please select a criteria.");
                    LOGGER.warn("Criteria is not selected.");
                }
            } else {
                tableViewCustomer.getItems().clear();
                textFieldSearch.clear();
                labelWarning.setText("Hey! You cannot type null!");
                LOGGER.warn("'null' is typed in the field.");
            }
        } else {
            labelWarning.setText("No customer details in database.");
            LOGGER.warn("No customer details in file.");
        }

        groupNew.selectToggle(null);
    }

    /**
     * Add new customer to list
     */
    @FXML
    public void addNewCustomer() {
        LOGGER.debug("Add Button is clicked.");
        int key = -1;
        Customer newCustomer;
        if (textFieldCustomerName.getText().trim().isEmpty() || textFieldCustomerId.getText().trim().isEmpty()
                || textFieldContactNumber.getText().trim().isEmpty() || textFieldEmail.getText().trim().isEmpty()) {
            labelWarning.setText("Please complete the information to add new customer.");
            LOGGER.warn("The customer information is not completed.");
        } else if (radioButtonNewIc.isSelected() && textFieldCustomerId.getText().matches("\\d{12}")) {
            List<Customer> tempoList = new ArrayList<>(customerDetails);

            for (Customer customer : tempoList) {
                if (textFieldCustomerId.getText().equals(customer.getIcNumber())) {
                    key = 1;
                    break;
                }
            }
            if (key >= 0) {
                labelWarning.setText("Customer Existed!");
                LOGGER.warn("The customer existed.");
            } else {
                newCustomer = new Customer(textFieldCustomerId.getText(), "null", textFieldCustomerName.getText()
                        , textFieldContactNumber.getText(), textFieldEmail.getText());
                customerDetails.add(newCustomer);
                addCustomerToTable(newCustomer);
            }
        } else if (radioButtonNewPassport.isSelected() && textFieldCustomerId.getText().matches("[A-Z0-9]{2}\\d{7}")) {
            List<Customer> tempoList = new ArrayList<>(customerDetails);
            for (Customer customer : tempoList) {
                if (textFieldCustomerId.getText().equals(customer.getPassportNumber())) {
                    key = 1;
                    break;
                }
            }
            if (key >= 0) {
                labelWarning.setText("Customer Existed!");
                LOGGER.warn("The customer existed.");
            } else {
                newCustomer = new Customer("null", textFieldCustomerId.getText(), textFieldCustomerName.getText()
                        , textFieldContactNumber.getText(), textFieldEmail.getText());
                customerDetails.add(newCustomer);
                addCustomerToTable(newCustomer);
            }
        } else {
            if (radioButtonNewIc.isSelected()) {
                labelWarning.setText("Please enter proper IC number.");
                LOGGER.warn("The IC number is incorrect.");
            } else if (radioButtonNewPassport.isSelected()) {
                labelWarning.setText("Please enter proper passport number.");
                LOGGER.warn("The Passport number is incorrect.");
            } else {
                labelWarning.setText("Please select IC / Passport");
                LOGGER.warn("RadioButton is not selected.");
            }
        }
    }

    public void addCustomerToTable(Customer newCustomer) {
        textFieldCustomerId.clear();
        textFieldCustomerName.clear();
        textFieldContactNumber.clear();
        textFieldEmail.clear();
        groupNew.selectToggle(null);
        labelWarning.setText("Customer Successfully added.");
        LOGGER.info("Customer added: " + newCustomer);

        radioButtonAllCustomer.setSelected(true);
        tableViewCustomer.getItems().clear();
        ObservableList<Customer> customers = tableViewCustomer.getItems();
        for (Customer customerDetail : customerDetails) {
            customers.add(customerDetail);
            tableViewCustomer.setItems(customers);
        }
        LOGGER.trace("TableView Refresh.");
    }

    /**
     * Modify old customer details
     */
    @FXML
    public void modifyCustomer() {
        Customer selectedC = tableViewCustomer.getSelectionModel().getSelectedItem();
        int key = -1;
        if (selectedC != null) {
            if (textFieldCustomerName.getText().trim().isEmpty() || textFieldCustomerId.getText().trim().isEmpty()
                    || textFieldContactNumber.getText().trim().isEmpty() || textFieldEmail.getText().trim().isEmpty()) {
                labelWarning.setText("Please complete the information");
                LOGGER.warn("Customer modified information is not completed.");

            } else if (radioButtonNewIc.isSelected() && textFieldCustomerId.getText().matches("\\d{12}")) {
                // IC Information correct
                selectedC.setIcNumber(textFieldCustomerId.getText());
                selectedC.setPassportNumber("null");
                selectedC.setCustomerName(textFieldCustomerName.getText());
                selectedC.setContactNumber(textFieldContactNumber.getText());
                selectedC.setEmail(textFieldEmail.getText());
                key = 1;
            } else if (radioButtonNewPassport.isSelected() && textFieldCustomerId.getText().matches("[A-Z0-9]{2}\\d{7}")) {
                // Passport Information correct
                selectedC.setPassportNumber(textFieldCustomerId.getText());
                selectedC.setIcNumber("null");
                selectedC.setCustomerName(textFieldCustomerName.getText());
                selectedC.setContactNumber(textFieldContactNumber.getText());
                selectedC.setEmail(textFieldEmail.getText());
                key = 1;
            } else {
                if (radioButtonNewIc.isSelected()) {
                    labelWarning.setText("Please enter proper IC number.");
                    LOGGER.warn("The IC number is incorrect.");
                } else if (radioButtonNewPassport.isSelected()) {
                    labelWarning.setText("Please enter proper passport number.");
                    LOGGER.warn("The Passport number is incorrect.");
                }
            }
            if (key > 0) {
                labelWarning.setText("Customer: " + selectedC.getCustomerName() + " data has been modified.");
                LOGGER.info("Customer has been modified: " + selectedC);

                radioButtonAllCustomer.setSelected(true);
                tableViewCustomer.getItems().clear();
                ObservableList<Customer> customers = tableViewCustomer.getItems();
                for (Customer customerDetail : customerDetails) {
                    customers.add(customerDetail);
                    tableViewCustomer.setItems(customers);
                }
                LOGGER.trace("Customer TableView Updated.");
            }
        } else {
            labelWarning.setText("Please select customer to modify.");
            LOGGER.warn("The customer to be modified is not selected.");
        }
    }

    /**
     * Delete the customer.
     */
    @FXML
    public void deleteCustomer() {
        LOGGER.debug("Delete Button is clicked.");
        Customer selectedCustomer = tableViewCustomer.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            customerDetails.remove(selectedCustomer);
            tableViewCustomer.getItems().remove(selectedCustomer);
            labelWarning.setText("Customer Data Removed.");
            LOGGER.info("Customer is removed: " + selectedCustomer);
        } else {
            labelWarning.setText("Not select any customer!");
            LOGGER.warn("Not selecting customer to be removed.");
        }
    }

    /**
     * Save the new changed customer list to text file and return to main page.
     */
    @FXML
    public void saveAndReturn(ActionEvent event) {
        LOGGER.debug("Save and Return Button is clicked.");
        // Write the latest customer data into text file
        // Format: IC#Passport#Name#Contact#Email
        if (customerDetails.size() > 0) {

            List<String> customersData = new ArrayList<>();
            customerDetails.forEach(customer -> {
                List<String> customerData = new ArrayList<>();
                Collections.addAll(customerData, customer.getIcNumber(), customer.getPassportNumber()
                        , customer.getCustomerName(), customer.getContactNumber(), customer.getEmail());
                customersData.add(HotelBookingUtil.encapsulate(customerData, "#"));
            });
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < customersData.size(); i++) {
                stringBuilder.append(customersData.get(i)).append(i == customersData.size() - 1 ? "" : "\n");
            }
            HotelBookingUtil.addData("Customer.txt", stringBuilder.toString(), false);
            LOGGER.trace("Latest Customer Details saved to Customer.txt");
        } else {
            // Since there is no any customer data, delete the file
            File file = new File("src/main/resources/ABCHotelInfo/data/Customer.txt");
            boolean delete = file.delete();
            if (delete){
            LOGGER.trace("Customer.txt is removed due to empty customer list.");
            }else {
                LOGGER.trace("Customer.txt cannot be removed even empty customer list.");
            }
        }
        try {
            backToMain(event);
            LOGGER.trace("Back to Main Page.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Back to Main Page: " + e);
        }
    }

    /**
     * Go back to main page
     */
    @FXML
    public void goBack(ActionEvent event) {
        LOGGER.debug("Back Button is clicked.");
        customerDetails.clear();
        try {
            backToMain(event);
            LOGGER.trace("Back to Main Page.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Back to Main Page: " + e);
        }
    }

    private void backToMain(ActionEvent event) throws IOException {
        // Back to Main Page
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com.hotelbooking.gui/MainPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("ABC Hotel Booking");
        stage.show();
    }

    private void setCheckBoxStatus(Boolean selected, Boolean disable) {
        checkBoxIc.setSelected(selected);
        checkBoxPassport.setSelected(selected);

        checkBoxIc.setDisable(disable);
        checkBoxPassport.setDisable(disable);
    }
}
