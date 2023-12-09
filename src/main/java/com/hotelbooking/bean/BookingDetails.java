// Programmer Name: Mr. Sim Sau Yang
// Program Name: Booking Details Class
// First Written on: 15 May 2023
// Edited on:

package com.hotelbooking.bean;

import java.util.List;
import java.util.Map;

/**
 * Booking details:
 * 1. Customer data
 * 2. Room Data
 * 3. Days of Stay
 * 4. Charges
 */
public class BookingDetails {
    private String receiptId, customerIcNumber, customerPassportNumber, customerName, bookingConfirmDateTime, paymentMethod;
    private Map<List<String>, String> roomDetails;
    private double subTotal, serviceTax, tourismTax, totalPrice;

    public BookingDetails() {
    }

    public BookingDetails(String receiptId, String customerIcNumber, String customerPassportNumber, String customerName
            , Map<List<String>, String> roomDetails, double subTotal, double serviceTax, double tourismTax, double totalPrice
            , String receiptGeneratedTime, String paymentMethod) {
        this.receiptId = receiptId;
        this.customerIcNumber = customerIcNumber;
        this.customerPassportNumber = customerPassportNumber;
        this.customerName = customerName;
        this.roomDetails = roomDetails;
        this.subTotal = subTotal;
        this.serviceTax = serviceTax;
        this.tourismTax = tourismTax;
        this.totalPrice = totalPrice;
        this.bookingConfirmDateTime = receiptGeneratedTime;
        this.paymentMethod = paymentMethod;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getCustomerIcNumber() {
        return customerIcNumber;
    }

    public void setCustomerIcNumber(String customerIcNumber) {
        this.customerIcNumber = customerIcNumber;
    }

    public String getCustomerPassportNumber() {
        return customerPassportNumber;
    }

    public void setCustomerPassportNumber(String customerPassportNumber) {
        this.customerPassportNumber = customerPassportNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Map<List<String>, String> getRoomDetails() {
        return roomDetails;
    }

    public void setRoomDetails(Map<List<String>, String> roomDetails) {
        this.roomDetails = roomDetails;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(double serviceTax) {
        this.serviceTax = serviceTax;
    }

    public double getTourismTax() {
        return tourismTax;
    }

    public void setTourismTax(double tourismTax) {
        this.tourismTax = tourismTax;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBookingConfirmDateTime() {
        return bookingConfirmDateTime;
    }

    public void setBookingConfirmDateTime(String bookingConfirmDateTime) {
        this.bookingConfirmDateTime = bookingConfirmDateTime;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "BookingDetails{" +
                "receiptId='" + receiptId + '\'' +
                ", customerIcNumber='" + customerIcNumber + '\'' +
                ", customerPassportNumber='" + customerPassportNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", receiptGeneratedTime='" + bookingConfirmDateTime + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", roomDetails=" + roomDetails +
                ", subTotal=" + subTotal +
                ", serviceTax=" + serviceTax +
                ", tourismTax=" + tourismTax +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
