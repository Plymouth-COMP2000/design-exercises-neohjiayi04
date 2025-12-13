package com.example.dineo.staff;

public class StaffReservation {
    private String id;
    private String time;
    private String guestName;
    private int guests;
    private String status;
    private String date;
    private String table;
    private String phone;
    private String email;
    private String specialRequests;

    // Constructor for list view (minimal data)
    public StaffReservation(String id, String time, String guestName, int guests, String status) {
        this.id = id;
        this.time = time;
        this.guestName = guestName;
        this.guests = guests;
        this.status = status;
    }

    // Full constructor for detail view
    public StaffReservation(String id, String time, String guestName, int guests,
                            String status, String date, String table, String phone,
                            String email, String specialRequests) {
        this.id = id;
        this.time = time;
        this.guestName = guestName;
        this.guests = guests;
        this.status = status;
        this.date = date;
        this.table = table;
        this.phone = phone;
        this.email = email;
        this.specialRequests = specialRequests;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
}