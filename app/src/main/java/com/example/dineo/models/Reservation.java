package com.example.dineo.models;

import java.io.Serializable;

/**
 * Reservation Model
 * Safe, complete, and adapter-friendly
 * Student ID: BSSE2506008
 */
public class Reservation implements Serializable {

    // ==================== STATUS CONSTANTS ====================
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_SEATED = "Seated";
    public static final String STATUS_CANCELLED = "Cancelled";

    // ==================== FIELDS ====================
    private int id;
    private String customerName;
    private String date;              // yyyy-MM-dd
    private String time;              // HH:mm
    private int numberOfGuests;
    private String tableNumber;
    private String specialRequests;
    private String status;
    private String userEmail;

    // ==================== CONSTRUCTORS ====================
    public Reservation() {
        // Required empty constructor
    }

    public Reservation(int id,
                       String customerName,
                       String date,
                       String time,
                       int numberOfGuests,
                       String tableNumber,
                       String specialRequests,
                       String status,
                       String userEmail) {

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
        return customerName != null ? customerName : "Unknown Guest";
    }

    public String getDate() {
        return date != null ? date : "N/A";
    }

    public String getTime() {
        return time != null ? time : "N/A";
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public String getSpecialRequests() {
        return (specialRequests != null && !specialRequests.isEmpty())
                ? specialRequests
                : "No special requests";
    }

    public String getStatus() {
        return status != null ? status : STATUS_PENDING;
    }

    public String getUserEmail() {
        return userEmail != null ? userEmail : "N/A";
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

    // ==================== HELPER METHODS ====================

    /** Used in adapters */
    public String getDateTimeFormatted() {
        if (date != null && time != null) {
            return date + " • " + time;
        }
        return date != null ? date : "N/A";
    }

    /** Used in adapters */
    public String getGuestsFormatted() {
        return numberOfGuests + (numberOfGuests == 1 ? " Guest" : " Guests");
    }

    /** Used in adapters */
    public String getTableFormatted() {
        if (tableNumber == null || tableNumber.trim().isEmpty()
                || tableNumber.equalsIgnoreCase("Any Table")) {
            return "Any Table";
        }
        return tableNumber;
    }

    /** Used for UI badge coloring */
    public boolean isPending() {
        return STATUS_PENDING.equalsIgnoreCase(getStatus());
    }

    public boolean isConfirmed() {
        return STATUS_CONFIRMED.equalsIgnoreCase(getStatus());
    }

    public boolean isCancelled() {
        return STATUS_CANCELLED.equalsIgnoreCase(getStatus());
    }

    public boolean isSeated() {
        return STATUS_SEATED.equalsIgnoreCase(getStatus());
    }
}
