// Programmer Name: Mr. Sim Sau Yang
// Program Name: Manage Booking Page Controller
// First Written on: 19 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.bean.BookingDetails;
import com.hotelbooking.utils.DataHolder;
import com.hotelbooking.utils.HotelBookingUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Enable staff to:
 * 1. Search Booking
 * 2. Modify Booking
 * 3. Delete Booking
 * 4. View Receipt
 * 5. Generate Report
 */
public class ManageBookingPageController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageBookingPageController.class);
    @FXML
    private CheckBox checkBoxCard, checkBoxCash, checkBoxEWallet;
    @FXML
    private TableView<BookingDetails> tableViewBookings;
    @FXML
    private TableColumn<BookingDetails, String> columnId, columnName, columnPayment, columnReceiptDate;
    @FXML
    private TableColumn<BookingDetails, Double> columnServiceTax, columnSubtotal, columnTotal, columnTourismTax;
    @FXML
    private DatePicker datePickerEnd, datePickerStart;
    @FXML
    private Label labelWarning;
    @FXML
    private final ToggleGroup searchGroup = new ToggleGroup();
    @FXML
    private RadioButton radioButtonDate, radioButtonId, radioButtonPayment, radioButtonViewAll;
    @FXML
    private TextField textFieldReceiptId;

    private final Image icon = new Image("/ABCHotelInfo/picture/HotelLogo.png");
    private final List<BookingDetails> allBookingDetails = new ArrayList<>();
    private final List<BookingDetails> deletedBookings = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        unCheck();
        // Get the booking details / Receipt from file
        if (HotelBookingUtil.searchFile("BookingDetails.txt")) {
            List<String> dirtyAllReceiptList = HotelBookingUtil.collectData("BookingDetails.txt");
            if (dirtyAllReceiptList.size() > 0) {
                List<List<String>> halfCleanAllReceiptList = new ArrayList<>();

                dirtyAllReceiptList.forEach(s -> halfCleanAllReceiptList.add(HotelBookingUtil.splitData(s, "#")));

                halfCleanAllReceiptList.forEach(strings -> {
                    List<String> roomsBooking = HotelBookingUtil.splitData(strings.get(4), Pattern.quote("|"));
                    String receiptID = strings.get(0);
                    String icNumber = strings.get(1);
                    String passportNumber = strings.get(2);
                    String name = strings.get(3);
                    Map<List<String>, String> bookingDetails = new HashMap<>();
                    for (String s : roomsBooking) {
                        List<String> roomBooking = HotelBookingUtil.splitData(s, Pattern.quote("~"));
                        List<String> detail = new ArrayList<>(roomBooking);
                        detail.remove(roomBooking.get(0));
                        bookingDetails.put(detail, roomBooking.get(0));
                    }
                    double subTotal = Double.parseDouble(strings.get(5));
                    double serviceTax = Double.parseDouble(strings.get(6));
                    double tourismTax = Double.parseDouble(strings.get(7));
                    double total = Double.parseDouble(strings.get(8));
                    String receiptGeneratedTime = strings.get(9);
                    String paymentMethod = strings.get(10);

                    allBookingDetails.add(new BookingDetails(receiptID, icNumber, passportNumber, name, bookingDetails
                            , subTotal, serviceTax, tourismTax, total, receiptGeneratedTime, paymentMethod));
                });

                LOGGER.trace("Data from Booking Details.txt is collected.");
            }
        }

        // Set up toggle group
        radioButtonId.setToggleGroup(searchGroup);
        radioButtonDate.setToggleGroup(searchGroup);
        radioButtonPayment.setToggleGroup(searchGroup);
        radioButtonViewAll.setToggleGroup(searchGroup);

        // Set up table view
        columnId.setCellValueFactory(new PropertyValueFactory<>("receiptId"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnReceiptDate.setCellValueFactory(new PropertyValueFactory<>("bookingConfirmDateTime"));
        columnPayment.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        columnSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        columnServiceTax.setCellValueFactory(new PropertyValueFactory<>("serviceTax"));
        columnTourismTax.setCellValueFactory(new PropertyValueFactory<>("tourismTax"));
        columnTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

    }

    /**
     * To delete the selected booking
     */
    @FXML
    public void delete() {
        LOGGER.debug("Delete Button is clicked.");
        BookingDetails selectedBooking = tableViewBookings.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            allBookingDetails.remove(selectedBooking);
            deletedBookings.add(selectedBooking);

            ObservableList<BookingDetails> bookingDetails = tableViewBookings.getItems();
            bookingDetails.remove(selectedBooking);
            tableViewBookings.setItems(bookingDetails);

            labelWarning.setText("Selected booking has been deleted.");
            LOGGER.info("Booking is deleted: " + selectedBooking);
        } else {
            labelWarning.setText("Please select a booking.");
            LOGGER.warn("No booking selected.");
        }
    }

    /**
     * To generate a report of the booking presents on tableView
     */
    @FXML
    public void generateReport() {
        LOGGER.debug("Generate Report Button is clicked.");
        ObservableList<BookingDetails> bookingDetails = tableViewBookings.getItems();
        List<BookingDetails> bookingDetailsList = new ArrayList<>(bookingDetails);

        if (bookingDetailsList.size() > 0) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com.hotelbooking.gui/ReportPage.fxml"));
                DataHolder.getInstance().setBookingDetailsListData(bookingDetailsList);
                Stage stage = new Stage();
                DataHolder.getInstance().setStageData(stage);
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Report");
                stage.getIcons().add(icon);
                stage.show();
                stage.setResizable(false);
                LOGGER.trace("Enter Report Page.");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Cannot Open Report Page: " + e);
            }
        }
    }

    /**
     * To control the selected criteria
     */
    @FXML
    public void unCheck() {
        if (radioButtonId.isSelected()) {
            setCheckBoxStatus(false, true);
            setDatePickerStatus(null, true);
            setTextFieldStatus(false);

        } else if (radioButtonDate.isSelected()) {
            setTextFieldStatus(true);
            setCheckBoxStatus(false, true);
            setDatePickerStatus(null, false);

        } else if (radioButtonPayment.isSelected()) {
            setTextFieldStatus(true);
            setDatePickerStatus(null, true);
            setCheckBoxStatus(false, false);
        } else if (radioButtonViewAll.isSelected()) {
            setTextFieldStatus(true);
            setCheckBoxStatus(false, true);
            setDatePickerStatus(null, true);
        }
    }

    /**
     * To refresh the page and show the Booking Details with selected criteria
     */
    @FXML
    public void refresh() {
        LOGGER.debug("Refresh Button is clicked.");
        labelWarning.setText("");
        tableViewBookings.getItems().clear();
        if (allBookingDetails.size() > 0) {
            if (radioButtonId.isSelected()) {
                if (!textFieldReceiptId.getText().isEmpty()) {
                    int key = -1;
                    ObservableList<BookingDetails> bookingDetails = tableViewBookings.getItems();
                    for (BookingDetails allBookingDetail : allBookingDetails) {
                        if (allBookingDetail.getReceiptId().contains(textFieldReceiptId.getText())) {
                            // Found
                            labelWarning.setText("");
                            bookingDetails.add(allBookingDetail);
                            tableViewBookings.setItems(bookingDetails);
                            key = 1;
                        }
                    }
                    LOGGER.trace("Booking found by ID.");
                    if (key < 0) {
                        labelWarning.setText("Receipt ID not found.");
                        LOGGER.warn("Receipt ID not found.");
                    }
                } else {
                    labelWarning.setText("Please enter Receipt ID.");
                    LOGGER.warn("Receipt ID is not entered.");
                }
            } else if (radioButtonDate.isSelected()) {
                LocalDate startDate = datePickerStart.getValue();
                LocalDate endDate = datePickerEnd.getValue();
                if (startDate == null || endDate == null) {
                    labelWarning.setText("Please select date.");
                    LOGGER.warn("Date is not selected.");
                } else {
                    if (ChronoUnit.DAYS.between(startDate, endDate) >= 0) {
                        List<BookingDetails> dateSearchBookings = new ArrayList<>();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        for (BookingDetails allBookingDetail : allBookingDetails) {
                            LocalDate receiptGenerateDate =
                                    LocalDate.parse(allBookingDetail.getBookingConfirmDateTime(), dateTimeFormatter);
                            if (ChronoUnit.DAYS.between(startDate, receiptGenerateDate) >= 0
                                    && ChronoUnit.DAYS.between(endDate, receiptGenerateDate) <= 0) {
                                dateSearchBookings.add(allBookingDetail);
                            }
                        }
                        ObservableList<BookingDetails> bookingDetails = tableViewBookings.getItems();
                        for (BookingDetails dateSearchBooking : dateSearchBookings) {
                            bookingDetails.add(dateSearchBooking);
                            tableViewBookings.setItems(bookingDetails);
                        }
                        LOGGER.trace("Booking found by receipt generated date.");
                    } else {
                        labelWarning.setText("We can't go back to the past...💔💔💔");
                        LOGGER.warn("The date-to-date selected is after-to-before.");
                    }
                }
            } else if (radioButtonPayment.isSelected()) {
                List<BookingDetails> paymentSearchBookings = new ArrayList<>();
                if (checkBoxCash.isSelected()) {
                    for (BookingDetails allBookingDetail : allBookingDetails) {
                        if (allBookingDetail.getPaymentMethod().equals(CheckOutPageController.CASH_PAYMENT)) {
                            paymentSearchBookings.add(allBookingDetail);
                        }
                    }
                }
                if (checkBoxCard.isSelected()) {
                    for (BookingDetails allBookingDetail : allBookingDetails) {
                        if (allBookingDetail.getPaymentMethod().equals(CheckOutPageController.CARD_PAYMENT)) {
                            paymentSearchBookings.add(allBookingDetail);
                        }
                    }
                }
                if (checkBoxEWallet.isSelected()) {
                    for (BookingDetails allBookingDetail : allBookingDetails) {
                        if (allBookingDetail.getPaymentMethod().equals(CheckOutPageController.E_WALLET_PAYMENT)) {
                            paymentSearchBookings.add(allBookingDetail);
                        }
                    }
                }
                ObservableList<BookingDetails> bookingDetails = tableViewBookings.getItems();
                for (BookingDetails paymentSearchBooking : paymentSearchBookings) {
                    bookingDetails.add(paymentSearchBooking);
                    tableViewBookings.setItems(bookingDetails);
                }
                LOGGER.trace("Booking found by payment method.");
            } else if (radioButtonViewAll.isSelected()) {
                ObservableList<BookingDetails> bookingDetails = tableViewBookings.getItems();
                for (BookingDetails allBookingDetail : allBookingDetails) {
                    bookingDetails.add(allBookingDetail);
                    tableViewBookings.setItems(bookingDetails);
                }
                LOGGER.trace("All bookings are shown.");
            } else {
                labelWarning.setText("Please select a criteria.");
                LOGGER.warn("Criteria is not selected.");
            }
        } else {
            labelWarning.setText("No Bookings in Database.");
        }

    }

    /**
     * View the receipt
     */
    @FXML
    public void viewReceipt() {
        LOGGER.debug("View Receipt Button is clicked.");
        BookingDetails selectedBooking = tableViewBookings.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            LOGGER.info("Receipt selected: " + selectedBooking);
            Class<? extends ManageBookingPageController> currentClass = getClass();
            try {
                HotelBookingUtil.createReceipt(selectedBooking, currentClass);
                LOGGER.trace("Open Receipt Page.");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Cannot Open Receipt Page: " + e);
            }
            labelWarning.setText("");
        } else {
            labelWarning.setText("Please select a booking");
            LOGGER.warn("Booking is not selected.");
        }
    }

    public void modify(ActionEvent event) {
        LOGGER.debug("Modify Button Clicked");
        BookingDetails selectedBooking = tableViewBookings.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            try {
                DataHolder.getInstance().setBookingDetailsData(selectedBooking);
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com.hotelbooking.gui/ModifyBookingPage.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Modify Booking");
                stage.getIcons().add(icon);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Cannon enter modify booking page: " + e);
            }
        }
    }

    /**
     * Go back to Main Page
     */
    @FXML
    public void goBack(ActionEvent event) {
        LOGGER.debug("Back Button is clicked.");
        try {
            goToNextPage(event, "/com.hotelbooking.gui/MainPage.fxml", "ABC Hotel Booking");
            LOGGER.trace("Back to Main Page.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open Receipt Page: " + e);
        }
    }


    /**
     * To save the changes and return to main page
     */
    @FXML
    public void saveAndReturn(ActionEvent event) {
        if (deletedBookings.size() > 0) {
            // Write the latest booking ID into text file
            // Format: RcptID#IC#Passport#Name#RmID~DoS~ChkIn~ChkOut~Amount|...(Room Details)...|...#SubT#SerT#TouT#Total#RcptGeneDate#pMethod
            List<String> bookingDetails = new ArrayList<>();
            for (BookingDetails bd : allBookingDetails) {
                List<String> semiDirtyData = new ArrayList<>();
                bd.getRoomDetails().forEach((strings, s) ->
                        semiDirtyData.add(s + "~" + HotelBookingUtil.encapsulate(strings, "~")));

                List<String> semiCleanedData = new ArrayList<>();
                Collections.addAll(semiCleanedData, bd.getReceiptId(), bd.getCustomerIcNumber(), bd.getCustomerPassportNumber()
                        , bd.getCustomerName(), HotelBookingUtil.encapsulate(semiDirtyData, "|"), bd.getSubTotal() + ""
                        , bd.getServiceTax() + "", bd.getTourismTax() + "", bd.getTotalPrice() + "", bd.getBookingConfirmDateTime(), bd.getPaymentMethod());
                String bookingDetail = HotelBookingUtil.encapsulate(semiCleanedData, "#");
                bookingDetails.add(bookingDetail);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bookingDetails.size(); i++) {
                stringBuilder.append(bookingDetails.get(i)).append(i < bookingDetails.size() - 1 ? "\n" : "");
            }
            if (stringBuilder.toString().isEmpty()) {
                // if there is no more booking in bookingDetails file, delete it
                File file = new File("src/main/resources/ABCHotelInfo/data/BookingDetails.txt");
                boolean delete = file.delete();
                if (delete) {
                    LOGGER.trace("BookingDetails.txt is deleted due to empty data.");
                } else {
                    LOGGER.trace("BookingDetails.txt cannot be deleted even empty data.");
                }
            } else {
                HotelBookingUtil.addData("BookingDetails.txt", stringBuilder.toString(), false);
                LOGGER.info("Latest Booking Details saved to BookingDetails.txt");
            }

            // Search for the existing daily booking file and delete the booking
            DateTimeFormatter dateTimeFormatterFile = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Set<String> fileToOpen = new HashSet<>();
            for (BookingDetails deletedBooking : deletedBookings) {
                deletedBooking.getRoomDetails().forEach((strings, s) -> {
                    LocalDate checkInDate = LocalDate.parse(strings.get(1), dateTimeFormatterFile);
                    LocalDate checkOutDate = LocalDate.parse(strings.get(2), dateTimeFormatterFile);

                    for (long i = 0; i < ChronoUnit.DAYS.between(checkInDate, checkOutDate); i++) {
                        String filename = dateTimeFormatterFile.format(checkInDate.plusDays(i)) + ".txt";
                        fileToOpen.add(filename);
                    }
                });
            }
            for (BookingDetails deletedBooking : deletedBookings) {
                for (String filename : fileToOpen) {
                    StringBuilder stringBuilder1 = new StringBuilder();
                    List<String> bookingsOfTheDay = HotelBookingUtil.collectData(filename);
                    List<String> tempoList = new ArrayList<>(bookingsOfTheDay);
                    for (String s1 : tempoList) {
                        List<String> tempo = HotelBookingUtil.splitData(s1, "#");
                        if (tempo.get(0).equals(deletedBooking.getReceiptId())) {
                            bookingsOfTheDay.remove(s1);
                        }
                    }

                    if (bookingsOfTheDay.size() > 0) {
                        for (int j = 0; j < bookingsOfTheDay.size(); j++) {
                            stringBuilder1.append(bookingsOfTheDay.get(j)).append(j < bookingsOfTheDay.size() - 1 ? "\n" : "");
                        }
                        HotelBookingUtil.addData(filename, stringBuilder1.toString(), false);
                        LOGGER.info("Daily Bookings " + filename + " files updated.");
                    } else {
                        // If there is no more booking in the daily file, delete it
                        File file = new File("src/main/resources/ABCHotelInfo/data/" + filename);
                        boolean delete = file.delete();
                        if (delete) {
                            LOGGER.trace("Daily Booking" + filename + " is deleted due to empty data.");
                        } else {
                            LOGGER.trace("Daily Booking" + filename + " cannot be deleted even empty data.");
                        }
                    }
                }
            }
        }
        // Back to Main Page
        try {
            goToNextPage(event, "/com.hotelbooking.gui/MainPage.fxml", "ABC Hotel Booking");
            LOGGER.trace("Enter Main Page.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Open Main Page.");
        }
    }

    public void goToNextPage(ActionEvent event, String filename, String pageTitle) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(filename));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle(pageTitle);
        stage.show();
    }


    public void setCheckBoxStatus(Boolean selected, Boolean disable) {
        checkBoxCash.setSelected(selected);
        checkBoxCard.setSelected(selected);
        checkBoxEWallet.setSelected(selected);
        checkBoxCash.setDisable(disable);
        checkBoxCard.setDisable(disable);
        checkBoxEWallet.setDisable(disable);
    }

    public void setDatePickerStatus(LocalDate value, Boolean disable) {
        datePickerStart.setValue(value);
        datePickerEnd.setValue(value);
        datePickerStart.setDisable(disable);
        datePickerEnd.setDisable(disable);
    }

    public void setTextFieldStatus(Boolean disable) {
        textFieldReceiptId.clear();
        textFieldReceiptId.setDisable(disable);
    }
}
