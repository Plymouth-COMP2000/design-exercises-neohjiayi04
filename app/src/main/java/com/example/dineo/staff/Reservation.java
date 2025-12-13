package com.example.dineo.staff;


public class Reservation {
    private String dateTime;
    private String pax;
    private String table;
    private String status;
    private String requests;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String reservationId;

    // Constructor
    public Reservation(String dateTime, String pax, String table, String status) {
        this.dateTime = dateTime;
        this.pax = pax;
        this.table = table;
        this.status = status;
        this.requests = "";
        this.guestName = "";
        this.guestEmail = "";
        this.guestPhone = "";
        this.reservationId = String.valueOf(System.currentTimeMillis());
    }

    // Getters
    public String getDateTime() {
        return dateTime;
    }

    public String getPax() {
        return pax;
    }

    public String getTable() {
        return table;
    }

    public String getStatus() {
        return status;
    }

    public String getRequests() {
        return requests;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public String getReservationId() {
        return reservationId;
    }

    // Setters
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setPax(String pax) {
        this.pax = pax;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRequests(String requests) {
        this.requests = requests;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
}