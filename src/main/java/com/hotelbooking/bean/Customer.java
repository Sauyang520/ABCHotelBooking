// Programmer Name: Mr. Sim Sau Yang
// Program Name: Customer Class
// First Written on: 15 May 2023
// Edited on:

package com.hotelbooking.bean;

/**
 * Customer Details
 */
public class Customer {
    private String customerName;
    private String icNumber;
    private String passportNumber;
    private String contactNumber;
    private String email;

    public Customer() {
    }

    public Customer(String icNumber, String passportNumber, String customerName, String contactNumber, String email) {
        this.customerName = customerName;
        this.icNumber = icNumber;
        this.passportNumber = passportNumber;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerName='" + customerName + '\'' +
                ", icNumber='" + icNumber + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
