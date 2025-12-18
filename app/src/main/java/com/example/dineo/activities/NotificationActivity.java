package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.NotificationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Notification;

import java.util.List;

/**
 * Notification Activity - Shows all notifications for user
 * Student ID: BSSE2506008
 */
public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationActionListener {

    private ImageView imageViewBack;
    private TextView textViewClearAll, textViewEmptyState;
    private RecyclerView recyclerViewNotifications;

    private DatabaseHelper databaseHelper;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notifications;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        textViewClearAll = findViewById(R.id.textViewClearAll);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);

        // Initialize
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Setup RecyclerView
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        // Load notifications
        loadNotifications();

        // Setup click listeners
        imageViewBack.setOnClickListener(v -> finish());

        textViewClearAll.setOnClickListener(v -> confirmClearAll());
    }

    private void loadNotifications() {
        String userEmail = sharedPreferences.getString("userEmail", "");

        if (userEmail.isEmpty()) {
            showEmptyState();
            return;
        }

        notifications = databaseHelper.getUserNotifications(userEmail);

        if (notifications.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            notificationAdapter = new NotificationAdapter(this, notifications, this);
            recyclerViewNotifications.setAdapter(notificationAdapter);
        }
    }

    private void showEmptyState() {
        recyclerViewNotifications.setVisibility(View.GONE);
        textViewEmptyState.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        recyclerViewNotifications.setVisibility(View.VISIBLE);
        textViewEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void onNotificationClick(Notification notification) {
        // Mark as read
        if (!notification.isRead()) {
            databaseHelper.markNotificationAsRead(notification.getId());
            loadNotifications(); // Refresh list
        }
    }

    @Override
    public void onDeleteClick(Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteNotification(notification.getId());
                    Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();
                    loadNotifications(); // Refresh list
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmClearAll() {
        if (notifications == null || notifications.isEmpty()) {
            Toast.makeText(this, "No notifications to clear", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Clear All Notifications")
                .setMessage("Are you sure you want to delete all notifications?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    String userEmail = sharedPreferences.getString("userEmail", "");

                    // Delete all notifications for this user
                    for (Notification notification : notifications) {
                        databaseHelper.deleteNotification(notification.getId());
                    }

                    Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show();
                    loadNotifications(); // Refresh list
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications(); // Refresh when returning to this activity
    }
}