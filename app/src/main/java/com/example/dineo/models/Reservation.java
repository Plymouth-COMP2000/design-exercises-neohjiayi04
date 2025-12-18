package com.example.dineo.models;

/**
 * Reservation Model - Stored locally in SQLite
 * Student ID: BSSE2506008
 */
public class Reservation {
    private int id;
    private String customerName;
    private String date;
    private String time;
    private int tableNumber;
    private int numberOfGuests;
    private String status; // "Confirmed", "Pending", "Cancelled"
    private String userEmail;
    private String specialRequests;

    // Constructors
    public Reservation() {
        this.status = "Pending";
    }

    public Reservation(String customerName, String date, String time, int tableNumber,
                       int numberOfGuests, String status, String userEmail) {
        this.customerName = customerName;
        this.date = date;
        this.time = time;
        this.tableNumber = tableNumber;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.userEmail = userEmail;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public String getStatus() {
        return status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    // Helper methods
    public String getDateTimeFormatted() {
        return date + " at " + time;
    }

    public String getGuestsFormatted() {
        return numberOfGuests + " Pax";
    }

    public String getTableFormatted() {
        return "Table: " + tableNumber;
    }
}