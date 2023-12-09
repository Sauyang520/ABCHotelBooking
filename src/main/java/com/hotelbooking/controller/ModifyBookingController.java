// Programmer Name: Mr. Sim Sau Yang
// Program Name: Modify Booking Page Controller
// First Written on: 5 June 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.controller;

import com.hotelbooking.Main;
import com.hotelbooking.bean.BookingDetails;
import com.hotelbooking.bean.Room;
import com.hotelbooking.utils.DataHolder;
import com.hotelbooking.utils.HotelBookingUtil;
import com.hotelbooking.utils.TableViewCheckOutAndViewBooking;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * To modify the booking
 */
public class ModifyBookingController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageBookingPageController.class);
    @FXML
    private TableColumn<TableViewCheckOutAndViewBooking, String> columnRoom, columnCheckin, columnCheckout;
    @FXML
    private TableColumn<Room, String> columnRoomSwitch, columnTypeSwitch;
    @FXML
    private TableView<TableViewCheckOutAndViewBooking> tableViewOriginal;
    @FXML
    private TableView<Room> tableViewSwitch;

    private final Set<Room> notAvailableRooms = new HashSet<>();
    private final Set<String> notAvailableString = new HashSet<>();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final BookingDetails bookingDetails = DataHolder.getInstance().getBookingDetailsData();
    private final Map<String, String> switchRooms = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up the table column
        columnRoom.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        columnCheckin.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        columnCheckout.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        columnRoomSwitch.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        columnTypeSwitch.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        prepareData();
    }

    /**
     * Make the table show the available room to be changed
     */
    public void prepareData() {
        tableViewOriginal.getItems().clear();
        tableViewSwitch.getItems().clear();
        notAvailableRooms.clear();
        notAvailableString.clear();

        // Insert the room booking details into original table
        ObservableList<TableViewCheckOutAndViewBooking> selectedBookings = tableViewOriginal.getItems();
        bookingDetails.getRoomDetails().forEach((strings, s) ->
                    selectedBookings.add(new TableViewCheckOutAndViewBooking(s, null, strings.get(0), strings.get(1)
                            , strings.get(2), Double.parseDouble(strings.get(3)), bookingDetails.getReceiptId(), bookingDetails.getCustomerName())));
        tableViewOriginal.setItems(selectedBookings);

        // Show available changed room on switch table:
        // Only the rooms that is available on the day, and the charges per night is same to original room are available.
        tableViewOriginal.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TableViewCheckOutAndViewBooking> change) -> {

                ObservableList<Room> availableRooms = tableViewSwitch.getItems();
                List<Room> tempo = new ArrayList<>(Main.ROOMS);

                TableViewCheckOutAndViewBooking selectedBooking = tableViewOriginal.getSelectionModel().getSelectedItem();

                if (selectedBooking != null) {
                    LocalDate checkIn = LocalDate.parse(selectedBooking.getCheckInDate(), dateTimeFormatter);
                    int dayOfStay = Integer.parseInt(selectedBooking.getDaysOfStay());

                    for (int i = 0; i < dayOfStay; i++) {
                        String filename = checkIn.plusDays(i).format(dateTimeFormatter);
                        List<String> bookedRooms = HotelBookingUtil.collectData(filename + ".txt");
                        for (String bookedRoom : bookedRooms) {
                            List<String> bookedRoomData = HotelBookingUtil.splitData(bookedRoom, "#");
                            notAvailableString.add(bookedRoomData.get(3));
                        }

                    }
                    for (Room room : Main.ROOMS) {
                        for (String s : notAvailableString) {
                            if ((s.equals(room.getRoomID()))) {
                                notAvailableRooms.add(room);
                            }
                        }
                    }
                    tempo.removeAll(notAvailableRooms);
                    List<Room> availableSwitchRooms = new ArrayList<>(tempo);
                    for (Room room : tempo) {
                        if ((room.getCharges() * dayOfStay) != selectedBooking.getAmount()) {
                            availableSwitchRooms.remove(room);
                        }
                    }
                    availableRooms.addAll(availableSwitchRooms);
                    tableViewSwitch.setItems(availableRooms);
                }
        });
    }

    /**
     * Switch from the original room to new selected room
     */
    @FXML
    public void confirm() {
        LOGGER.debug("OK Button Clicked.");
        TableViewCheckOutAndViewBooking selectedOrigin = tableViewOriginal.getSelectionModel().getSelectedItem();
        Room selectedSwitch = tableViewSwitch.getSelectionModel().getSelectedItem();

        if (selectedOrigin != null && selectedSwitch != null) {
            Map<List<String>, String> tempo = new HashMap<>(bookingDetails.getRoomDetails());
            Map<List<String>, String> tempo2 = new HashMap<>(bookingDetails.getRoomDetails());
            List<String> roomDetail = new ArrayList<>();
            Collections.addAll(roomDetail, selectedOrigin.getDaysOfStay(), selectedOrigin.getCheckInDate()
                    , selectedOrigin.getCheckOutDate(), selectedOrigin.getAmount().toString(), selectedOrigin.getRoomId());

            tempo.forEach((strings, s) -> {
                    if (new HashSet<>(strings).containsAll(roomDetail)) {
                        tempo2.replace(roomDetail, selectedSwitch.getRoomID());
                    }
                }
            );

            bookingDetails.setRoomDetails(tempo2);
            prepareData();

            switchRooms.put(selectedOrigin.getRoomId(), selectedSwitch.getRoomID());
            LOGGER.info("Switch Room: " + switchRooms);
        }
    }

    /**
     * Save the changes into text file and return to the manage booking page
     */
    @FXML
    public void save(ActionEvent event) {
        LOGGER.debug("Save Button Clicked.");
        List<String> booking = HotelBookingUtil.collectData("BookingDetails.txt");
        List<String> tempo = new ArrayList<>(booking);
        for (int i = 0; i < tempo.size(); i++) {
            if (HotelBookingUtil.splitData(tempo.get(i), "#").get(0).equals(bookingDetails.getReceiptId())) {
                String switchData = booking.get(i);
                for (String s : switchRooms.keySet()) {
                    String tempo2;
                    tempo2 = switchData.replace(s, switchRooms.get(s)).replace("~" + s, "~" + switchRooms.get(s));
                    switchData = tempo2;
                }
                booking.set(i, switchData);
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < booking.size(); i++) {
            sb.append(booking.get(i)).append(i < booking.size() - 1 ? "\n" : "");
        }
        HotelBookingUtil.addData("BookingDetails.txt", sb.toString(), false);


        for (List<String> list : bookingDetails.getRoomDetails().keySet()) {
            LocalDate date = LocalDate.parse(list.get(1), dateTimeFormatter);
            int dayOfStay = Integer.parseInt(list.get(0));
            for (int i = 0; i < dayOfStay; i++) {
                String filename = date.plusDays(i).format(dateTimeFormatter) + ".txt";
                List<String> dailyBookings = HotelBookingUtil.collectData(filename);
                List<String> tempo2 = new ArrayList<>(dailyBookings);
                for (int i1 = 0; i1 < tempo2.size(); i1++) {
                    if (HotelBookingUtil.splitData(dailyBookings.get(i1), "#").get(0).equals(bookingDetails.getReceiptId())) {
                        String switchData = dailyBookings.get(i1);
                        for (String s : switchRooms.keySet()) {
                            if (dailyBookings.get(i1).contains(s)){
                                String tempo3;
                                tempo3 = switchData.replace(s, switchRooms.get(s));
                                switchData = tempo3;
                            }
                        }
                        dailyBookings.set(i1, switchData);
                    }
                }
                StringBuilder sb1 = new StringBuilder();
                for (;i < dailyBookings.size(); i++) {
                    sb1.append(dailyBookings.get(i)).append(i < dailyBookings.size() - 1 ? "\n" : "");
                }
                HotelBookingUtil.addData(filename, sb1.toString(), false);
            }
            LOGGER.info("Room modified: " + switchRooms);
        }
        back(event);
    }

    /**
     * Back to manage booking page
     */
    @FXML
    public void cancel(ActionEvent event) {
        LOGGER.debug("Cancel Button Clicked.");
        back(event);
    }

    public void back(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com.hotelbooking.gui/ManageBookingPage.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Modify Booking");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Cannon enter manage booking page: " + e);
        }
    }
}
