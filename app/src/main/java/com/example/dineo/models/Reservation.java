package com.example.dineo.models;

/**
 * Reservation Model - Complete with helper methods
 * Student ID: BSSE2506008
 */
public class Reservation {

    private int id;
    private String customerName;
    private String date;
    private String time;
    private int numberOfGuests;
    private String tableNumber;
    private String specialRequests;
    private String status;
    private String userEmail;

    public Reservation() {
    }

    public Reservation(int id, String customerName, String date, String time,
                       int numberOfGuests, String tableNumber, String specialRequests,
                       String status, String userEmail) {
        this.id = id;
        this.customerName = customerName;
        this.date = date;
        this.time = time;
        this.numberOfGuests = numberOfGuests;
        this.tableNumber = tableNumber;
        this.specialRequests = specialRequests;
        this.status = status;
        this.userEmail = userEmail;
    }

    // ==================== GETTERS ====================

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

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public String getStatus() {
        return status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    // ==================== SETTERS ====================

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

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // ==================== HELPER METHODS (FOR ADAPTERS) ====================

    /**
     * Get formatted date and time
     * Used by StaffReservationAdapter
     */
    public String getDateTimeFormatted() {
        if (date != null && time != null) {
            return date + " at " + time;
        }
        return date != null ? date : "N/A";
    }

    /**
     * Get formatted guests count
     * Used by StaffReservationAdapter
     */
    public String getGuestsFormatted() {
        return numberOfGuests + (numberOfGuests == 1 ? " Guest" : " Guests");
    }

    /**
     * Get formatted table number
     * Used by StaffReservationAdapter
     */
    public String getTableFormatted() {
        if (tableNumber == null || tableNumber.isEmpty() || tableNumber.equals("Any Table")) {
            return "Any Table";
        }
        return tableNumber;
    }
}