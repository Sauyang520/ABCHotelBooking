// Programmer Name: Mr. Sim Sau Yang
// Program Name: Room Class
// First Written on: 15 May 2023
// Edited on: 24 June 2023

package com.hotelbooking.bean;

/**
 * Room Details
 */
public class Room implements Comparable<Room> {
    private String floor;
    private String roomNumber;
    private String roomType;
    private double charges;
    private String roomID;

    public Room() {
    }

    public Room(String floor, String roomNumber, String roomType, double charges) {
        this.floor = floor;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.charges = charges;
        this.roomID = this.floor + "-" + this.roomNumber;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getCharges() {
        return charges;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomID='" + roomID + '\'' +
                '}';
    }

    @Override
    public int compareTo(Room o) {
        return this.roomID.compareTo(o.roomID);
    }
}