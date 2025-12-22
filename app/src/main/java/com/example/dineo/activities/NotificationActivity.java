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
 * GUEST ONLY - Notification activity for guests
 * Extends BaseActivity to use guest bottom navigation
 */
public class NotificationActivity extends BaseActivity
        implements NotificationAdapter.OnNotificationActionListener {

    private DatabaseHelper db;
    private RecyclerView recyclerView;
    private TextView emptyState;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // SECURITY CHECK: Verify this is a Guest user
        prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        if (!isGuestUser()) {
            Toast.makeText(this, "Access denied. Staff use different notification page.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup guest bottom navigation (no active item since notification not in nav menu)
        setupBottomNavigation(R.id.nav_profile); // Use profile as closest nav item

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        emptyState = findViewById(R.id.textViewEmptyState);
        ImageView back = findViewById(R.id.imageViewBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        back.setOnClickListener(v -> finish());

        loadNotifications();
    }

    /**
     * Check if current user is Guest (not Staff)
     */
    private boolean isGuestUser() {
        String userJson = prefs.getString("user_json", null);
        if (userJson == null) return false;

        try {
            JSONObject json = new JSONObject(userJson);
            String userType = json.optString("usertype", "Guest");
            return "Guest".equalsIgnoreCase(userType);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadNotifications() {
        String email = getGuestEmail();
        if (email.isEmpty()) {
            showEmpty();
            return;
        }

        List<Notification> all = db.getUserNotifications(email);
        List<Notification> filtered = new ArrayList<>();

        // Filter based on user's notification preferences
        for (Notification n : all) {
            if (shouldShow(n.getType())) {
                filtered.add(n);
            }
        }

        if (filtered.isEmpty()) {
            showEmpty();
        } else {
            recyclerView.setAdapter(
                    new NotificationAdapter(this, filtered, this));
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    /**
     * Get guest email from SharedPreferences
     */
    private String getGuestEmail() {
        String email = prefs.getString("userEmail", "");

        // Fallback: try to get from user_json
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
     * Check if notification type should be shown based on user preferences
     */
    private boolean shouldShow(String type) {
        if (type == null) return true;

        switch (type) {
            case "reservation_confirmed":
                return prefs.getBoolean("notifyBooking", true);
            case "reservation_modified":
                return prefs.getBoolean("notifyModification", true);
            case "reservation_cancelled":
                return prefs.getBoolean("notifyCancellation", true);
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
            loadNotifications();
        }
    }

    @Override
    public void onDeleteClick(Notification n) {
        db.deleteNotification(n.getId());
        loadNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}