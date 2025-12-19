package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Staff Dashboard Activity - Shows statistics and upcoming reservations
 * Student ID: BSSE2506008
 */
public class StaffDashboardActivity extends AppCompatActivity {

    private TextView textViewMenuCount, textViewReservationCount;
    private View btnAddMenuItem, btnManageTable, btnViewReservations;
    private RecyclerView recyclerViewUpcomingReservations;
    private ImageView imageViewNotification;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        // Initialize views
        textViewMenuCount = findViewById(R.id.textViewMenuCount);
        textViewReservationCount = findViewById(R.id.textViewReservationCount);
        btnAddMenuItem = findViewById(R.id.btnAddMenuItem);
        btnManageTable = findViewById(R.id.btnManageTable);
        btnViewReservations = findViewById(R.id.btnViewReservations);
        recyclerViewUpcomingReservations = findViewById(R.id.recyclerViewUpcomingReservations);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Setup RecyclerView
        recyclerViewUpcomingReservations.setLayoutManager(new LinearLayoutManager(this));

        // Load data
        loadDashboardData();

        // Setup click listeners
        btnAddMenuItem.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, AddMenuItemActivity.class);
            startActivity(intent);
        });

        btnManageTable.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, StaffMenuActivity.class);
            startActivity(intent);
        });

        btnViewReservations.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, StaffReservationActivity.class);
            startActivity(intent);
        });

        imageViewNotification.setOnClickListener(v -> {
            Intent intent = new Intent(StaffDashboardActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void loadDashboardData() {
        // Get menu item count
        int menuCount = databaseHelper.getAllMenuItems().size();
        textViewMenuCount.setText(String.valueOf(menuCount));

        // Get upcoming reservation count
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        List<Reservation> upcomingReservations = new ArrayList<>();

        for (Reservation reservation : allReservations) {
            if ("Confirmed".equals(reservation.getStatus()) || "Pending".equals(reservation.getStatus())) {
                upcomingReservations.add(reservation);
            }
        }

        textViewReservationCount.setText(String.valueOf(upcomingReservations.size()));

        // Show upcoming reservations (limit to 3)
        List<Reservation> displayReservations = upcomingReservations.size() > 3
                ? upcomingReservations.subList(0, 3)
                : upcomingReservations;

        // You'll need to create a simple adapter for this
        // For now, we'll skip the RecyclerView implementation
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_staff_dashboard);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_staff_dashboard) {
                return true;
            } else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(StaffDashboardActivity.this, StaffMenuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_reservation) {
                startActivity(new Intent(StaffDashboardActivity.this, StaffReservationActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(StaffDashboardActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
}