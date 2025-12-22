package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.NotificationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Notification;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * STAFF ONLY - Notification activity for staff members
 * Shows all reservation-related notifications
 */
public class StaffNotificationActivity extends StaffBaseActivity
        implements NotificationAdapter.OnNotificationActionListener {

    private DatabaseHelper db;
    private RecyclerView recyclerView;
    private TextView emptyState;
    private SharedPreferences prefs;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // SECURITY CHECK: Verify this is a Staff user
        if (!isStaffUser()) {
            Toast.makeText(this, "Access denied. Only staff can access this page.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, NotificationActivity.class));
            finish();
            return;
        }

        // Setup staff bottom navigation
        setupStaffBottomNavigation(R.id.nav_staff_profile);

        db = new DatabaseHelper(this);

        // Use the CORRECT ID from your layout
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        emptyState = findViewById(R.id.textViewEmptyState);
        ImageView back = findViewById(R.id.imageViewBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        back.setOnClickListener(v -> finish());

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationList, this);
        recyclerView.setAdapter(adapter);

        loadStaffNotifications();
    }

    /**
     * Check if current user is Staff
     */
    private boolean isStaffUser() {
        String userJson = prefs.getString("user_json", null);
        if (userJson == null) return false;

        try {
            JSONObject json = new JSONObject(userJson);
            String userType = json.optString("usertype", "Guest");
            return "Staff".equalsIgnoreCase(userType);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load staff-specific notifications
     * Staff sees ALL reservation notifications from all users
     */
    private void loadStaffNotifications() {
        // Get staff email
        String staffEmail = getStaffEmail();
        if (staffEmail.isEmpty()) {
            showEmpty();
            return;
        }

        // Staff sees all notifications (not filtered by user)
        List<Notification> all = db.getUserNotifications(staffEmail);
        notificationList.clear();

        // Filter for staff-relevant notification types
        for (Notification n : all) {
            if (isStaffRelevant(n.getType())) {
                notificationList.add(n);
            }
        }

        if (notificationList.isEmpty()) {
            showEmpty();
        } else {
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    /**
     * Get staff email from SharedPreferences
     */
    private String getStaffEmail() {
        String email = prefs.getString("userEmail", "");

        if (email.isEmpty()) {
            String userJson = prefs.getString("user_json", null);
            if (userJson != null) {
                try {
                    JSONObject json = new JSONObject(userJson);
                    email = json.optString("email", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return email;
    }

    /**
     * Check if notification type is relevant for staff
     */
    private boolean isStaffRelevant(String type) {
        if (type == null) return true;

        switch (type) {
            case "reservation_confirmed":
            case "reservation_status":
            case "reservation_modified":
            case "reservation_cancelled":
            case "new_reservation":
                return true;
            default:
                return true;
        }
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNotificationClick(Notification n) {
        if (!n.isRead()) {
            db.markNotificationAsRead(n.getId());
            loadStaffNotifications();
        }
    }

    @Override
    public void onDeleteClick(Notification n) {
        db.deleteNotification(n.getId());
        loadStaffNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStaffNotifications();
    }
}