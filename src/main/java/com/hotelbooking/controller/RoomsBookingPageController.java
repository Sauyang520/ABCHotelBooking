// Programmer Name: Mr. Sim Sau Yang
// Program Name: About Page Controller
// First Written on: 16 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.Main;
import com.hotelbooking.bean.Room;
import com.hotelbooking.utils.DataHolder;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * Book the rooms
 */
public class RoomsBookingPageController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsBookingPageController.class);
    @FXML
    private ChoiceBox<String> choiceBoxType;
    @FXML
    private TableView<Room> tableViewAvailableRooms;
    @FXML
    private TableColumn<Room, Double> columnPrice;
    @FXML
    private TableColumn<Room, String> columnRoomId, columnType;
    @FXML
    private DatePicker datePickerCheckIn, datePickerCheckOut;
    @FXML
    private ListView<String> listViewSelectedRoom;
    @FXML
    private TextField textFieldStayDay;
    @FXML
    private Label labelWarning;

    public static final String SINGLE_ROOM = "Single";
    public static final String COUPLE_ROOM = "Couple";

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final Set<Room> allRooms = new HashSet<>(Main.ROOMS);
    private final Set<Room> notAvailableRooms = new HashSet<>();
    private final Map<List<String>, Room> selectedRooms = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ChoiceBox
        choiceBoxType.getItems().addAll(Main.ROOM_TYPE);
        choiceBoxType.setOnAction(this::search);

        // Set up for Tableview
        columnRoomId.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("charges"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("roomType"));


        // Select the room(s) and add into the list
        tableViewAvailableRooms.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Room>) change -> {
            Room selectedRoom = tableViewAvailableRooms.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                LOGGER.debug("TableView row is clicked, Room" + selectedRoom.getRoomID() + " is selected.");
                String dayOfStay = textFieldStayDay.getText();
                String checkInDate = datePickerCheckIn.getValue().format(dateTimeFormatter);
                String checkOutDate = datePickerCheckOut.getValue().format(dateTimeFormatter);
                List<String> dateDetails = new ArrayList<>(Arrays.asList(dayOfStay, checkInDate, checkOutDate, selectedRoom.getRoomID()));

                selectedRooms.put(dateDetails, selectedRoom);
                notAvailableRooms.add(selectedRoom);

                // Add to listView
                listViewSelectedRoom.getItems().clear();
                selectedRooms.forEach((strings, room) ->
                        listViewSelectedRoom.getItems().add(room.getRoomID() + ", " + strings.get(0) + ", " + strings.get(1) + ", " + strings.get(2)));
                LOGGER.trace("Selected Room(s) is added into listView.");
            }
        });
    }


    /**
     * Search for the available room(s) and show on the table
     */
    @FXML
    public void search(ActionEvent event) {
        LOGGER.debug("ChoiceBox / DatePickerStart / DatePickerEnd is clicked.");
        tableViewAvailableRooms.getItems().clear();
        notAvailableRooms.clear();

        String selectedType = choiceBoxType.getValue();
        if (selectedType != null) {
            switch (selectedType) {
                case SINGLE_ROOM -> {
                    for (Room room : allRooms) {
                        if (!room.getRoomType().equals(SINGLE_ROOM)) {
                            notAvailableRooms.add(room);
                        }
                    }
                }
                case COUPLE_ROOM -> {
                    for (Room room : allRooms) {
                        if (!room.getRoomType().equals(COUPLE_ROOM)) {
                            notAvailableRooms.add(room);
                        }
                    }
                }
            }
        }

        Set<Room> availableRooms = new HashSet<>(allRooms);
        Set<String> notAvailableRoomsId = new HashSet<>();
        LocalDate checkIn = datePickerCheckIn.getValue();
        LocalDate checkOut = datePickerCheckOut.getValue();

        if (selectedRooms.size() > 0) {
            selectedRooms.forEach((strings, room) -> {
                LocalDate selectedCheckIn = LocalDate.parse(strings.get(1), dateTimeFormatter);
                LocalDate selectedCheckOut = LocalDate.parse(strings.get(2), dateTimeFormatter);

                // Determine if the new date is out of range of selected room date
                // Exp: newCheckIn -> newCheckOut => selectedCheckIn -> selectedCheckOut || selectedCheckIn -> selectedCheckOut => newCheckIn -> newCheckOut
                if (!(((ChronoUnit.DAYS.between(checkIn, selectedCheckIn) > 0) && (ChronoUnit.DAYS.between(checkOut, selectedCheckIn) >= 0))
                        || ((ChronoUnit.DAYS.between(selectedCheckOut, checkIn) >= 0) && (ChronoUnit.DAYS.between(selectedCheckOut, checkOut) > 0)))) {
                    // The date contrast with the selected booking, the room is not available
                    notAvailableRooms.add(room);
                }
            });
        }

        // Determine the check in date cannot be past
        if (checkIn != null) {
            if (ChronoUnit.DAYS.between(LocalDate.now(), checkIn) >= 0) {
                // Check out date should be selected to filter available room
                if (checkOut != null) {
                    // Make sure the check in date is before check out date
                    if (ChronoUnit.DAYS.between(checkIn, checkOut) > 0) {
                        labelWarning.setText("");
                        // Define the day(s) of stay
                        long daysOfStay;
                        daysOfStay = ChronoUnit.DAYS.between(checkIn, checkOut);
                        textFieldStayDay.setText(daysOfStay + "");

                        // Search for existed bookings from file
                        for (long i = 0; i < ChronoUnit.DAYS.between(checkIn, checkOut); i++) {
                            String fileName = dateTimeFormatter.format(checkIn.plusDays(i)) + ".txt";
                            // If the file existed, put the booking into file
                            if (HotelBookingUtil.searchFile(fileName)) {
                                List<String> bookings = HotelBookingUtil.collectData(fileName);
                                LOGGER.trace("Filename: " + fileName + " is searched.");
                                for (String booking : bookings) {
                                    // Get the existed booking room id
                                    notAvailableRoomsId.add(HotelBookingUtil.splitData(booking, Pattern.quote("#")).get(3));
                                }
                            }
                        }
                    } else {
                        // Select past check-out date
                        labelWarning.setText("The time cannot go reverse...");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Hey dude! The time cannot go reverse!");
                        alert.setContentText("How can you check in today and check out yesterday.\nAlthough I hope to go back to the past so...");
                        alert.showAndWait();
                        notAvailableRooms.addAll(allRooms);
                        LOGGER.warn("The selected check out date is before check in date.");
                    }
                } else {
                    // Check out date is not selected
                    labelWarning.setText("Please select check out date.");
                    notAvailableRooms.addAll(allRooms);
                }
            } else {
                // Select future check-in date
                labelWarning.setText("Don't dwell on past!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Hey dude! Don't dwell on past!");
                alert.setContentText("We cannot live in the past...\nLet's look forward!");
                alert.showAndWait();
                notAvailableRooms.addAll(allRooms);
                LOGGER.warn("The selected check in date is past.");
            }
        } else {
            notAvailableRooms.addAll(allRooms);
        }

        // Use the room id to search for room object and put into notAvailableRooms
        for (Room room : allRooms) {
            for (String roomId : notAvailableRoomsId) {
                if (room.getRoomID().equals(roomId)) {
                    notAvailableRooms.add(room);
                }
            }
        }

        availableRooms.removeAll(notAvailableRooms);
        ObservableList<Room> rooms = tableViewAvailableRooms.getItems();
        List<Room> sortRoom = new ArrayList<>(availableRooms);
        Collections.sort(sortRoom);
        rooms.addAll(sortRoom);
        tableViewAvailableRooms.setItems(rooms);
}

    /**
     * Confirm the booking and enter the next page to fill in the customer details
     */
    @FXML
    public void confirm(ActionEvent event) {
        LOGGER.debug("Proceed Button is Clicked.");
        if (selectedRooms.size() > 0) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com.hotelbooking.gui/CustomerDetailsPage.fxml"));

                DataHolder.getInstance().setMapRoomBookingData(selectedRooms);
                LOGGER.info("Selected Rooms: " + selectedRooms);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.setTitle("Customer Details");
                LOGGER.trace("Enter Customer Details Page.");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("Cannot Open Customer Details Page: " + e);
            }
        } else {
            labelWarning.setText("You must select at least ONE room!");
            LOGGER.warn("No room is selected.");
        }
    }

    /**
     * Remove the selected room(s) which shows on the list
     */
    @FXML
    public void remove() {
        LOGGER.debug("Remove Button is Clicked.");
        String selectedFromList = listViewSelectedRoom.getSelectionModel().getSelectedItem();
        if (selectedFromList != null) {
            List<String> selectedRoom = HotelBookingUtil.splitData(selectedFromList, ", ");
            Map<List<String>, Room> tempoList = new HashMap<>(selectedRooms);
            // remove the selected room
            tempoList.forEach((strings, room) -> {
                if (room.getRoomID().equals(selectedRoom.get(0))) {
                    selectedRooms.remove(strings);
                    LOGGER.info("Remove Selected Booking: " + room + ", " + strings);
                }
            });

            listViewSelectedRoom.getItems().remove(selectedFromList);
        } else {
            labelWarning.setText("Please select a booking to delete.");
            LOGGER.warn("No booking is selected to delete.");
        }
    }

    /**
     * Go back to Main Page
     */
    @FXML
    void goBack(ActionEvent event) {
        LOGGER.debug("Back Button is clicked.");
        try {
            Class<? extends RoomsBookingPageController> currentClass = getClass();
            HotelBookingUtil.goToMainPage(event, LOGGER, currentClass);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannot Back to Main Page: " + e);
        }
    }
}
