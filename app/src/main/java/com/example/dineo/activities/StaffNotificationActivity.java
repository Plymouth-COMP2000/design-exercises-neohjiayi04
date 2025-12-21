package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.NotificationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Staff Notification Activity
 * Shows notifications for staff members
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

        // Setup staff bottom navigation
        setupStaffBottomNavigation(R.id.nav_staff_profile);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        emptyState = findViewById(R.id.textViewEmptyState);
        ImageView back = findViewById(R.id.imageViewBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        back.setOnClickListener(v -> finish());

        notificationList = new ArrayList<>();

        // FIXED: Pass Context first, then list, then listener
        adapter = new NotificationAdapter(this, notificationList, this);
        recyclerView.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        String email = prefs.getString("userEmail", "");
        if (email.isEmpty()) {
            showEmpty();
            return;
        }

        List<Notification> all = db.getUserNotifications(email);
        notificationList.clear();

        for (Notification n : all) {
            if (shouldShow(n.getType())) {
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

    private boolean shouldShow(String type) {
        if (type == null) return true;

        switch (type) {
            case "reservation_confirmed":
            case "reservation_status":
            case "reservation_modified":
            case "reservation_cancelled":
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