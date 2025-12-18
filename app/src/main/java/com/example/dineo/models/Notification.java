package com.example.dineo.models;

/**
 * Notification Model - Stored locally in SQLite
 * Student ID: BSSE2506008
 */
public class Notification {
    private int id;
    private String title;
    private String message;
    private String timestamp;
    private boolean isRead;
    private String type; // "reservation_confirmed", "reservation_modified", "reservation_cancelled"
    private String userEmail;

    // Constructors
    public Notification() {
        this.isRead = false;
    }

    public Notification(String title, String message, String timestamp, String type, String userEmail) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.userEmail = userEmail;
        this.isRead = false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getType() {
        return type;
    }

    public String getUserEmail() {
        return userEmail;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // Helper method
    public String getTimeAgo() {
        return timestamp;
    }
}