package com.example.dineo;
// 2. IMPORT ACTIVITIES FROM SUB-PACKAGES

import com.example.dineo.guest.GuestMenuActivity;
import com.example.dineo.staff.guest.ReservationActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";

    // Constants for user types
    public static final String EXTRA_USER_TYPE = "user_type";
    public static final String USER_TYPE_GUEST = "guest";
    public static final String USER_TYPE_ADMIN = "admin";

    // UI Components
    private ImageView backButton;
    private TextView clearAllButton;
    private LinearLayout notificationContainer;
    private LinearLayout navMenu, navReservation, navProfile;

    // Data
    private List<NotificationItem> notifications;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Get user type from intent
        userType = getIntent().getStringExtra(EXTRA_USER_TYPE);
        if (userType == null) {
            userType = USER_TYPE_GUEST; // Default to guest
        }

        // Initialize UI components
        initializeViews();

        // Initialize notification data based on user type
        initializeNotifications();

        // Set up click listeners
        setupClickListeners();

        // Display notifications
        displayNotifications();
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        notificationContainer = findViewById(R.id.notificationContainer);

        // Bottom Navigation
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);
    }

    /**
     * Initialize notification data based on user type
     */
    private void initializeNotifications() {
        notifications = new ArrayList<>();

        if (USER_TYPE_ADMIN.equals(userType)) {
            // Admin notifications
            loadAdminNotifications();
        } else {
            // Guest notifications
            loadGuestNotifications();
        }
    }

    /**
     * Load guest-specific notifications
     */
    private void loadGuestNotifications() {
        notifications.add(new NotificationItem(
                "Reservation Confirmed",
                "Your table is confirmed at T-Rex Grill for 7:00 PM on Friday.",
                "5m ago",
                R.drawable.ic_calendar,
                "#E8F5E9",
                R.color.success
        ));

        notifications.add(new NotificationItem(
                "Reservation Reminder",
                "Just a heads-up! Your reservation at The Salty Squid is tomorrow.",
                "1h ago",
                R.drawable.ic_clock,
                "#E3F2FD",
                R.color.colorPrimary
        ));

        notifications.add(new NotificationItem(
                "Special Offer",
                "Get 20% off your next reservation this weekend!",
                "2h ago",
                R.drawable.ic_notification,
                "#FFF3E0",
                R.color.colorAccent
        ));
    }

    /**
     * Load admin-specific notifications
     */
    private void loadAdminNotifications() {
        notifications.add(new NotificationItem(
                "New Reservation",
                "John Doe has made a reservation for 4 people at 7:30 PM tonight.",
                "5m ago",
                R.drawable.ic_calendar,
                "#E8F5E9",
                R.color.success
        ));

        notifications.add(new NotificationItem(
                "Cancellation Alert",
                "Reservation #1234 has been cancelled by the customer.",
                "15m ago",
                R.drawable.ic_cancel,
                "#FFEBEE",
                R.color.colorError
        ));

        notifications.add(new NotificationItem(
                "Table Update",
                "Table 12 is now available for booking.",
                "1h ago",
                R.drawable.ic_table,
                "#E3F2FD",
                R.color.colorPrimary
        ));

        notifications.add(new NotificationItem(
                "Peak Hours Alert",
                "High booking volume expected tonight. Prepare staff accordingly.",
                "2h ago",
                R.drawable.ic_clock,
                "#FFF3E0",
                R.color.colorAccent
        ));
    }

    /**
     * Set up all click listeners
     */
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Clear all notifications
        clearAllButton.setOnClickListener(v -> clearAllNotifications());

        // Bottom Navigation
        navMenu.setOnClickListener(v -> navigateToMenu());
        navReservation.setOnClickListener(v -> navigateToReservation());
        navProfile.setOnClickListener(v -> navigateToProfile());
    }

    /**
     * Display all notifications dynamically
     */
    private void displayNotifications() {
        notificationContainer.removeAllViews();

        if (notifications.isEmpty()) {
            showEmptyState();
            return;
        }

        for (int i = 0; i < notifications.size(); i++) {
            NotificationItem notification = notifications.get(i);
            View notificationView = createNotificationView(notification, i);
            notificationContainer.addView(notificationView);
        }
    }

    /**
     * Create a notification view
     */
    private View createNotificationView(NotificationItem notification, int position) {
        // Ensure "notification_item" layout exists in res/layout
        View view = getLayoutInflater().inflate(R.layout.notification_item, notificationContainer, false);

        // Find views
        CardView cardView = view.findViewById(R.id.notificationCard);
        LinearLayout contentLayout = view.findViewById(R.id.notificationContent);
        ImageView iconView = view.findViewById(R.id.notificationIcon);
        TextView titleView = view.findViewById(R.id.notificationTitle);
        TextView messageView = view.findViewById(R.id.notificationMessage);
        TextView timeView = view.findViewById(R.id.notificationTime);
        ImageView closeButton = view.findViewById(R.id.closeButton);

        // Set data
        iconView.setImageResource(notification.getIconRes());
        titleView.setText(notification.getTitle());
        messageView.setText(notification.getMessage());
        timeView.setText(notification.getTime());

        // Set background color
        try {
            contentLayout.setBackgroundColor(android.graphics.Color.parseColor(notification.getBackgroundColor()));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing background color: " + notification.getBackgroundColor(), e);
        }

        // Set icon tint
        iconView.setColorFilter(getResources().getColor(notification.getIconColorRes(), getTheme()));

        // Close button click listener
        closeButton.setOnClickListener(v -> removeNotification(position));

        // Card click listener (optional - navigate to details)
        cardView.setOnClickListener(v ->
                Toast.makeText(this, "Notification: " + notification.getTitle(), Toast.LENGTH_SHORT).show()
        );

        return view;
    }

    /**
     * Remove a specific notification
     */
    private void removeNotification(int position) {
        if (position >= 0 && position < notifications.size()) {
            notifications.remove(position);
            displayNotifications();
            Toast.makeText(this, "Notification removed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Clear all notifications
     */
    private void clearAllNotifications() {
        if (notifications.isEmpty()) {
            Toast.makeText(this, "No notifications to clear", Toast.LENGTH_SHORT).show();
            return;
        }

        notifications.clear();
        displayNotifications();
        Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
    }

    /**
     * Show empty state when no notifications
     */
    private void showEmptyState() {
        // Ensure "notification_empty_state" layout exists in res/layout
        View emptyView = getLayoutInflater().inflate(R.layout.notification_empty_state, notificationContainer, false);
        notificationContainer.addView(emptyView);
    }

    /**Navigate to Menu screen*/
    private void navigateToMenu() {
        Intent intent = new Intent(NotificationActivity.this, GuestMenuActivity.class);
        intent.putExtra(EXTRA_USER_TYPE, userType); // Pass user type
        startActivity(intent);
        finish();
    }

    /**Navigate to Reservation screen */
    private void navigateToReservation() {
        Intent intent = new Intent(NotificationActivity.this, ReservationActivity.class);
        intent.putExtra(EXTRA_USER_TYPE, userType); // Pass user type
        startActivity(intent);
        finish();
    }

    /**
     * Navigate to Profile screen
     */
    private void navigateToProfile() {
        Intent intent = new Intent(NotificationActivity.this, ProfileActivity.class);
        intent.putExtra(EXTRA_USER_TYPE, userType); // Pass user type
        startActivity(intent);
        finish();
    }

    /**
     * Notification data model
     */
    public static class NotificationItem {
        private final String title;
        private final String message;
        private final String time;
        private final int iconRes;
        private final String backgroundColor;
        private final int iconColorRes;

        public NotificationItem(String title, String message, String time,
                                int iconRes, String backgroundColor, int iconColorRes) {
            this.title = title;
            this.message = message;
            this.time = time;
            this.iconRes = iconRes;
            this.backgroundColor = backgroundColor;
            this.iconColorRes = iconColorRes;
        }

        // Getters
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getTime() { return time; }
        public int getIconRes() { return iconRes; }
        public String getBackgroundColor() { return backgroundColor; }
        public int getIconColorRes() { return iconColorRes; }
    }
}