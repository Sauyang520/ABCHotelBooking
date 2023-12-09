// Programmer Name: Mr. Sim Sau Yang
// Program Name: DataHolder Class
// First Written on: 15 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.utils;

import com.hotelbooking.bean.BookingDetails;
import com.hotelbooking.bean.Customer;
import com.hotelbooking.bean.Room;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * To transfer the data from a page (controller) to another page (controller)
 */
public class DataHolder {

    private static DataHolder instance;
    private String stringCustomerPositionData;
    private Map<List<String>, Room> mapRoomBookingData;
    private List<BookingDetails> bookingDetailsListData;
    private BookingDetails bookingDetailsData;
    private Customer customerData;
    private Stage stageData;

    private DataHolder() {}

    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public static void deleteInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    public List<BookingDetails> getBookingDetailsListData() {
        return bookingDetailsListData;
    }

    public void setBookingDetailsListData(List<BookingDetails> bookingDetailsListData) {
        this.bookingDetailsListData = bookingDetailsListData;
    }

    public Stage getStageData() {
        return stageData;
    }

    public void setStageData(Stage stageData) {
        this.stageData = stageData;
    }

    public BookingDetails getBookingDetailsData() {
        return bookingDetailsData;
    }

    public void setBookingDetailsData(BookingDetails bookingDetailsData) {
        this.bookingDetailsData = bookingDetailsData;
    }

    public String getStringCustomerPositionData() {
        return stringCustomerPositionData;
    }

    public void setStringCustomerPositionData(String stringCustomerPositionData) {
        this.stringCustomerPositionData = stringCustomerPositionData;
    }

    public Map<List<String>, Room> getMapRoomBookingData() {
        return mapRoomBookingData;
    }

    public void setMapRoomBookingData(Map<List<String>, Room> mapRoomBookingData) {
        this.mapRoomBookingData = mapRoomBookingData;
    }

    public Customer getCustomerData() {
        return customerData;
    }

    public void setCustomerData(Customer customerData) {
        this.customerData = customerData;
    }
}
