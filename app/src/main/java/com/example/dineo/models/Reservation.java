package com.example.dineo.models;

<<<<<<< HEAD
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

=======
import java.util.Locale;

public class Reservation {
    private int id;
    private String customerName;
    private String date;
    private String time;
    private int numberOfPax;
    private String status;
    private String phoneNumber;
    private String email;
    private String specialRequests;
    private String tableNumber;

    // Constructor without ID (for new reservations)
    public Reservation(String customerName, String date, String time, int numberOfPax,
                       String status, String phoneNumber, String email,
                       String specialRequests, String tableNumber) {
        this.customerName = customerName;
        this.date = date;
        this.time = time;
        this.numberOfPax = numberOfPax;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.specialRequests = specialRequests;
        this.tableNumber = tableNumber;
    }

    // Constructor with ID (for existing reservations)
    public Reservation(int id, String customerName, String date, String time,
                       int numberOfPax, String status, String phoneNumber,
                       String email, String specialRequests, String tableNumber) {
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        this.id = id;
        this.customerName = customerName;
        this.date = date;
        this.time = time;
<<<<<<< HEAD
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
=======
        this.numberOfPax = numberOfPax;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.specialRequests = specialRequests;
        this.tableNumber = tableNumber;
    }

    // Getters
    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getNumberOfPax() { return numberOfPax; }
    public String getStatus() { return status; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getSpecialRequests() { return specialRequests; }
    public String getTableNumber() { return tableNumber; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setNumberOfPax(int numberOfPax) { this.numberOfPax = numberOfPax; }
    public void setStatus(String status) { this.status = status; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    // Helper method to format time from 24hr to 12hr format
    public String getFormattedTime() {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            String period = hour >= 12 ? "PM" : "AM";
            int displayHour = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);

            return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, period);
        } catch (Exception e) {
            return time;
        }
    }

    // Helper method to get hour part for UI display
    public String getTimeHour() {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int displayHour = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);
            return String.valueOf(displayHour);
        } catch (Exception e) {
            return "12";
        }
    }

    // Helper method to get AM/PM period
    public String getTimePeriod() {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            return hour >= 12 ? "PM" : "AM";
        } catch (Exception e) {
            return "PM";
        }
    }
}
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
