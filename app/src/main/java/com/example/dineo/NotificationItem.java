// NotificationItem.java
package com.example.dineo;

public class NotificationItem {
    private int id;
    private String title;
    private String message;
    private String timeAgo;
    private String type; // "confirmation", "reminder", "cancellation"
    private boolean isRead;
    private long timestamp;

    // Constructor, getters, setters
}

// UserPreferences.java
package com.example.dineo.models;

public class UserPreferences {
    private boolean notifyConfirmation;
    private boolean notifyModification;
    private boolean notifyCancellation;

    // Constructor, getters, setters
}