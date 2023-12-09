// Programmer Name: Mr. Sim Sau Yang
// Program Name: Table View for Check Out and View Booking
// First Written on: 16 May 2023
// Edited on: Sunday, 18 June 2023

package com.hotelbooking.utils;

/**
 * As a temporary object to set up the table view
 */
public class TableViewCheckOutAndViewBooking {

    private String roomId, daysOfStay, checkInDate, checkOutDate, receiptID, name;
    private Double pricePerNight, amount;

    public TableViewCheckOutAndViewBooking() {
    }

    public TableViewCheckOutAndViewBooking(String roomId, Double pricePerNight, String daysOfStay, String checkInDate
            , String checkOutDate, Double amount, String receiptID, String name) {
        this.roomId = roomId;
        this.daysOfStay = daysOfStay;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.pricePerNight = pricePerNight;
        this.amount = amount;
        this.receiptID = receiptID;
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getDaysOfStay() {
        return daysOfStay;
    }

    public void setDaysOfStay(String daysOfStay) {
        this.daysOfStay = daysOfStay;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReceiptID() {
        return receiptID;
    }

    public void setReceiptID(String receiptID) {
        this.receiptID = receiptID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
