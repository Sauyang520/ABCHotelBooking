// Programmer Name: Mr. Sim Sau Yang
// Program Name: Staff Class
// First Written on: 15 May 2023
// Edited on:

package com.hotelbooking.bean;

/**
 * Staff Account
 */
public class Staff {
    private String username;
    private String password;

    public Staff() {
    }

    public Staff(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
