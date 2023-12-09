// Programmer Name: Mr. Sim Sau Yang
// Program Name: Check Out Page Controller
// First Written on: 18 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.bean.BookingDetails;
import com.hotelbooking.bean.Customer;
import com.hotelbooking.bean.Room;
import com.hotelbooking.utils.DataHolder;
import com.hotelbooking.utils.HotelBookingUtil;
import com.hotelbooking.utils.TableViewCheckOutAndViewBooking;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Checkout Page allows staff to see the booking details ->
 * 1. Charges of each rooms
 * 2. Tax applied
 * 3. Total amount
 * After the customer makes payment, the place order button is clicked to complete the booking.
 */
public class CheckOutPageController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckOutPageController.class);
    @FXML
    private TableView<TableViewCheckOutAndViewBooking> tableViewBooking;
    @FXML
    private TableColumn<TableViewCheckOutAndViewBooking, String> columnRoomId, columnDaysOfStay, columnCheckInDate, columnCheckOutDate;
    @FXML
    private TableColumn<TableViewCheckOutAndViewBooking, Double> columnPricePerNight, columnAmount;
    @FXML
    private RadioButton radioButtonCash, radioButtonCard, radioButtonEWallet;
    @FXML
    private Label labelCustomerId, labelNotice;
    @FXML
    private TextField textFieldToday, textFieldCustomerName, textFieldCustomerId, textFieldSubTotal, textFieldsServiceTax, textFieldTourismTax, textFieldTotal;

    private Customer customerInfo;
    private String customerPosition, paymentMethod;
    private final Map<List<String>, String> newSelectedRoomsData = new HashMap<>();
    private double amount, subTotal, serviceTax, tourismTax, total;

    public static final String CASH_PAYMENT = "Cash";
    public static final String CARD_PAYMENT = "Card";
    public static final String E_WALLET_PAYMENT = "E-Wallet";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get data from customer details page
        customerInfo = DataHolder.getInstance().getCustomerData();
        Map<List<String>, Room> selectedRoomsData = DataHolder.getInstance().getMapRoomBookingData();
        customerPosition = DataHolder.getInstance().getStringCustomerPositionData();

        LocalDateTime todayDate = LocalDateTime.now();
        String formattedTodayDate = todayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm EEE"));
        LOGGER.trace("Current check out time is set: " + formattedTodayDate);

        // Set up the TextField
        textFieldToday.setText(formattedTodayDate);
        textFieldCustomerName.setText(customerInfo.getCustomerName());
        if (customerInfo.getPassportNumber().equals("null")) {
            labelCustomerId.setText("IC Number");
            textFieldCustomerId.setText(customerInfo.getIcNumber());
        } else if (customerInfo.getIcNumber().equals("null")) {
            labelCustomerId.setText("Passport Number");
            textFieldCustomerId.setText(customerInfo.getPassportNumber());
        }

        // Prepare for the TableView
        columnRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        columnPricePerNight.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        columnDaysOfStay.setCellValueFactory(new PropertyValueFactory<>("daysOfStay"));
        columnCheckInDate.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        columnCheckOutDate.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        columnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));


        selectedRoomsData.forEach((strings, room) -> {
            int daysOfStay = Integer.parseInt(strings.get(0));
            String checkInDate = strings.get(1);
            String checkOutDate = strings.get(2);

            // Calculate the amount of each room by day(s) of stay
            BigDecimal bdRoomCharges = BigDecimal.valueOf(room.getCharges());
            BigDecimal bdDayOfStay = BigDecimal.valueOf(daysOfStay * 1.0);
            BigDecimal bdAmount = bdRoomCharges.multiply(bdDayOfStay);
            amount = bdAmount.doubleValue();

            // Create new Map to store room details, days of stay, check-in and check-out date
            List<String> dateListAndAmount = new ArrayList<>(strings);
            dateListAndAmount.add(amount + "");
            newSelectedRoomsData.put(dateListAndAmount, room.getRoomID());

            // Insert into each column
            TableViewCheckOutAndViewBooking tableViewCheckOut = new TableViewCheckOutAndViewBooking(room.getRoomID(), room.getCharges()
                    , daysOfStay + "", checkInDate, checkOutDate, amount, null, customerInfo.getCustomerName());
            ObservableList<TableViewCheckOutAndViewBooking> tableViewCheckOuts = tableViewBooking.getItems();
            tableViewCheckOuts.add(tableViewCheckOut);
            tableViewBooking.setItems(tableViewCheckOuts);

            // Calculate the subtotal, tourism tax
            BigDecimal bdSubTotal = BigDecimal.valueOf(subTotal).add(bdAmount);
            subTotal = bdSubTotal.doubleValue();

            if (customerInfo.getPassportNumber().equals("null")) {
                tourismTax = 0;
            } else if (customerInfo.getIcNumber().equals("null")) {
                int tourismTaxPerRoom = daysOfStay * 10;
                tourismTax += tourismTaxPerRoom;
            }
        });
        LOGGER.trace("Checkout TableView is set.");
        // Calculate the service tax, total
        BigDecimal bdSubTotal = BigDecimal.valueOf(subTotal);
        BigDecimal bdServiceTax = bdSubTotal.multiply(BigDecimal.valueOf(0.1));
        BigDecimal bdTourismTax = BigDecimal.valueOf(tourismTax);
        serviceTax = bdServiceTax.doubleValue();

        BigDecimal bdTotal = bdSubTotal.add(bdServiceTax).add(bdTourismTax)
                .divide(BigDecimal.valueOf(1.0), 2, RoundingMode.HALF_UP);
        total = bdTotal.doubleValue();

        // Adding information to TextField
        textFieldSubTotal.setText(subTotal + "");
        textFieldsServiceTax.setText(serviceTax + "");
        textFieldTourismTax.setText(tourismTax + "");
        textFieldTotal.setText(total + "");

        LOGGER.info("Total Price Calculated: " + total);
    }

    /**
     * To cancel the booking, the data stored in DataHolder will be emptied.
     * After cancel, automatically return to main page
     */
    @FXML
    public void cancel(ActionEvent event) {
        LOGGER.debug("Cancel Button is clicked.");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure to cancel booking?");
        alert.setContentText("Your will lost the filled data.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            LOGGER.debug("'OK' Button is clicked.");
            DataHolder.deleteInstance();

            goToNextPage(event, "/com.hotelbooking.gui/MainPage.fxml", "ABC Hotel Booking");
        }
    }

    /**
     * Define payment method, this system does not build with the payment system.
     * Once the customer complete payment, the staff can click
     */
    @FXML
    public void paymentMethod() {
        if (radioButtonCash.isSelected()) {
            paymentMethod = CASH_PAYMENT;
        } else if (radioButtonCard.isSelected()) {
            paymentMethod = CARD_PAYMENT;
        } else if (radioButtonEWallet.isSelected()) {
            paymentMethod = E_WALLET_PAYMENT;
        }
    }

    /**
     * Complete the order, changes made to:
     * 1. Save new customer information to text file
     * 2. Save booking details to text file
     */
    @FXML
    public void placeBooking(ActionEvent event) throws RuntimeException {
        LOGGER.debug("Place booking button is clicked.");
        // Make sure select the payment method
        if (paymentMethod != null) {
            goToNextPage(event, "/com.hotelbooking.gui/MainPage.fxml", "ABC Hotel Booking");

            // Save Booking Details to Booking Details Object
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String bookingConfirmDateTime = dateTimeFormatter.format(localDateTime);

            String receiptId;
            if (HotelBookingUtil.searchFile("ReceiptIdCount.txt")) {
                int receiptNumber;
                List<String> customerDetails = HotelBookingUtil.collectData("ReceiptIdCount.txt");
                // Create Receipt Number
                if (customerDetails.size() == 0) {
                    receiptNumber = 1;
                } else {
                    receiptNumber = Integer.parseInt(customerDetails.get(customerDetails.size() - 1).substring(1)) + 1;
                }
                receiptId = "Z" + receiptNumber;
                HotelBookingUtil.addData("ReceiptIdCount.txt", receiptId, false);
            } else {
                receiptId = "Z1";
                HotelBookingUtil.addData("ReceiptIdCount.txt", receiptId, false);
            }

            // Save to Booking Details Object
            BookingDetails bd = new BookingDetails(receiptId, customerInfo.getIcNumber(), customerInfo.getPassportNumber()
                    , customerInfo.getCustomerName(), newSelectedRoomsData, subTotal, serviceTax, tourismTax, total
                    , bookingConfirmDateTime, paymentMethod);

            LOGGER.info("Booking Details conformed: " + bd);
            // Move the roomID that is added as count at room selecting page to the last element of list of booking details
            bd.getRoomDetails().forEach((strings, s) -> {
                        String tempo = strings.get(3);
                        strings.remove(3);
                        strings.add(tempo);
                    }
            );

            // Save new customer information to text file
            if (customerPosition.equals(CustomerDetailsPageController.NEW_CUSTOMER)) {
                List<String> customerData = new ArrayList<>();
                Collections.addAll(customerData, customerInfo.getIcNumber(), customerInfo.getPassportNumber()
                        , customerInfo.getCustomerName(), customerInfo.getContactNumber(), customerInfo.getEmail());
                HotelBookingUtil.addData("Customer.txt", HotelBookingUtil.encapsulate(customerData, "#"), true);
                LOGGER.trace("New customer info saved to Customer.txt: " + customerData);
            }


            // Save Booking Details to text file
            List<String> halfEncapsulatedBookingData = new ArrayList<>();
            List<String> encapsulatedBookingData = new ArrayList<>();

            bd.getRoomDetails().forEach((strings, s) ->
                    halfEncapsulatedBookingData.add(s + "~" + (HotelBookingUtil.encapsulate(strings, "~"))));

            String bookingDetailsData = HotelBookingUtil.encapsulate(halfEncapsulatedBookingData, "|");
            Collections.addAll(encapsulatedBookingData, bd.getReceiptId(), bd.getCustomerIcNumber(), bd.getCustomerPassportNumber()
                    , bd.getCustomerName(), bookingDetailsData, bd.getSubTotal() + "", bd.getServiceTax() + "", bd.getTourismTax() + "", bd.getTotalPrice() + ""
                    , bd.getBookingConfirmDateTime(), bd.getPaymentMethod());
            String toBeWrite = HotelBookingUtil.encapsulate(encapsulatedBookingData, "#");
            HotelBookingUtil.addData("BookingDetails.txt", toBeWrite, true);
            LOGGER.trace("Booking Details saved to BookingDetails.txt");

            // Save Booking Details to Daily text file
            // Remove Counter of Booking details
            bd.getRoomDetails().forEach((strings, s) -> strings.remove(strings.size() - 1));

            DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            bd.getRoomDetails().forEach((strings, s) -> {
                List<String> roomBookings = new ArrayList<>();

                Collections.addAll(roomBookings, receiptId, bd.getCustomerIcNumber(), bd.getCustomerPassportNumber()
                        , s, HotelBookingUtil.encapsulate(strings, "#"));
                String dayRoomBooking = HotelBookingUtil.encapsulate(roomBookings, "#");

                int dayOfStay = Integer.parseInt(strings.get(0));
                LocalDate checkIn = LocalDate.parse(strings.get(1), dateTimeFormatter1);

                for (int i = 0; i < dayOfStay; i++) {
                    String filename = dateTimeFormatter1.format(checkIn.plusDays(i)) + ".txt";
                    HotelBookingUtil.addData(filename, dayRoomBooking, true);
                }
            });
            LOGGER.trace("Booking Details saved to daily txt file.");

            // Open Receipt Page
            Class<? extends CheckOutPageController> currentClass = getClass();
            try {
                HotelBookingUtil.createReceipt(bd, currentClass);
                LOGGER.trace("Open Receipt Page.");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("Cannot Open Receipt Page: " + e);
            }
        } else {
            labelNotice.setText("Please select a payment method!");
            LOGGER.warn("Payment method is not defined.");
        }
    }

    /**
     * Go back to customer details page
     */
    @FXML
    public void goBack(ActionEvent event) {
        LOGGER.debug("Back Button is clicked.");
        goToNextPage(event, "/com.hotelbooking.gui/CustomerDetailsPage.fxml", "Customer Details");
    }

    public void goToNextPage(ActionEvent event, String fileName, String pageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fileName));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(pageTitle);
            stage.show();
            LOGGER.trace("Enter " + fileName.replace("fxml", ""));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open " + fileName.replace("fxml", "") + ": " + e);
        }
    }
}
