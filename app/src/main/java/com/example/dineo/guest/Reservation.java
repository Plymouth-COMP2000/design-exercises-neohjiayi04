package com.example.dineo.guest;

public class Reservation {
    private String id;
    private String date;
    private String time;
    private int guests;
    private String table;
    private String requests;
    private String status; // "Upcoming" or "Finished"

    public Reservation(String id, String date, String time, int guests,
                       String table, String requests, String status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.guests = guests;
        this.table = table;
        this.requests = requests;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getGuests() {
        return guests;
    }

    public String getTable() {
        return table;
    }

    public String getRequests() {
        return requests;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setRequests(String requests) {
        this.requests = requests;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}