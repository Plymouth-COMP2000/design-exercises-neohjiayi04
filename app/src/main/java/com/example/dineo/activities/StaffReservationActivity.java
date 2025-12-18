package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.StaffReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Staff Reservation Activity - View and manage all reservations
 * Student ID: BSSE2506008
 */
public class StaffReservationActivity extends AppCompatActivity implements StaffReservationAdapter.OnReservationActionListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerViewReservations;
    private ImageView imageViewNotification;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private StaffReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_reservation);

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        recyclerViewReservations = findViewById(R.id.recyclerViewReservations);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(this));

        // Setup tabs
        setupTabs();

        // Load reservations
        loadReservations("all");

        // Setup notification bell
        imageViewNotification.setOnClickListener(v -> {
            Intent intent = new Intent(StaffReservationActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // All
                        loadReservations("all");
                        break;
                    case 1: // Confirmed
                        loadReservations("confirmed");
                        break;
                    case 2: // Pending
                        loadReservations("pending");
                        break;
                    case 3: // Cancelled
                        loadReservations("cancelled");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadReservations(String filter) {
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        List<Reservation> filteredReservations = new ArrayList<>();

        for (Reservation reservation : allReservations) {
            if (filter.equals("all")) {
                filteredReservations.add(reservation);
            } else if (filter.equals("confirmed") && "Confirmed".equals(reservation.getStatus())) {
                filteredReservations.add(reservation);
            } else if (filter.equals("pending") && "Pending".equals(reservation.getStatus())) {
                filteredReservations.add(reservation);
            } else if (filter.equals("cancelled") && "Cancelled".equals(reservation.getStatus())) {
                filteredReservations.add(reservation);
            }
        }

        reservationAdapter = new StaffReservationAdapter(this, filteredReservations, this);
        recyclerViewReservations.setAdapter(reservationAdapter);
    }

    @Override
    public void onConfirmClick(Reservation reservation) {
        reservation.setStatus("Confirmed");
        databaseHelper.updateReservation(reservation);

        // Create notification for user
        String timestamp = new java.text.SimpleDateFormat("MMM dd, yyyy h:mm a", java.util.Locale.getDefault())
                .format(java.util.Calendar.getInstance().getTime());
        databaseHelper.addNotification(
                "Reservation Confirmed",
                "Your table is confirmed for " + reservation.getDate() + " at " + reservation.getTime(),
                timestamp,
                "reservation_confirmed",
                reservation.getUserEmail()
        );

        loadReservations("all");
    }

    @Override
    public void onCancelClick(Reservation reservation) {
        databaseHelper.cancelReservation(reservation.getId());

        // Create notification for user
        String timestamp = new java.text.SimpleDateFormat("MMM dd, yyyy h:mm a", java.util.Locale.getDefault())
                .format(java.util.Calendar.getInstance().getTime());
        databaseHelper.addNotification(
                "Reservation Cancelled",
                "Your reservation for " + reservation.getDate() + " has been cancelled",
                timestamp,
                "reservation_cancelled",
                reservation.getUserEmail()
        );

        loadReservations("all");
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(StaffReservationActivity.this, StaffDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(StaffReservationActivity.this, StaffMenuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_reservation) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(StaffReservationActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations("all");
    }
}