package com.example.dineo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
 * StaffReservationActivity - Staff manages all reservations
 * Features:
 * - View all reservations (Pending, Confirmed, Seated, Cancelled)
 * - Confirm pending reservations
 * - Mark reservations as seated
 * - Cancel reservations with confirmation dialog
 * - View reservation details
 */
public class StaffReservationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReservations;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private List<Reservation> filteredReservations;
    private StaffReservationAdapter staffReservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_reservation);

        // Initialize views
        recyclerViewReservations = findViewById(R.id.recyclerViewReservations);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);
        filteredReservations = new ArrayList<>();

        // Setup RecyclerView
        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(this));

        staffReservationAdapter = new StaffReservationAdapter(
                this,
                filteredReservations,
                new StaffReservationAdapter.OnReservationClickListener() {
                    @Override
                    public void onConfirm(Reservation reservation) {
                        confirmReservation(reservation);
                    }

                    @Override
                    public void onCancel(Reservation reservation) {
                        showCancelConfirmationDialog(reservation);
                    }
                }
        );

        recyclerViewReservations.setAdapter(staffReservationAdapter);

        // Setup tabs and navigation
        setupTabs();
        setupBottomNavigation();

        // Load pending reservations by default
        loadReservations("Pending");
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch(position) {
                    case 0: // Pending
                        loadReservations("Pending");
                        break;
                    case 1: // Confirmed
                        loadReservations("Confirmed");
                        break;
                    case 2: // Seated
                        loadReservations("Seated");
                        break;
                    case 3: // Cancelled
                        loadReservations("Cancelled");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    /**
     * Load reservations filtered by status
     */
    private void loadReservations(String status) {
        filteredReservations.clear();

        List<Reservation> allReservations = databaseHelper.getAllReservations();

        for (Reservation reservation : allReservations) {
            if (reservation.getStatus() != null && reservation.getStatus().equals(status)) {
                filteredReservations.add(reservation);
            }
        }

        if (filteredReservations.isEmpty()) {
            Toast.makeText(this, "No " + status.toLowerCase() + " reservations", Toast.LENGTH_SHORT).show();
        }

        staffReservationAdapter.notifyDataSetChanged();
    }

    /**
     * Confirm a pending reservation
     */
    private void confirmReservation(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Reservation")
                .setMessage("Confirm reservation for " + reservation.getCustomerName() + "?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    int result = databaseHelper.updateReservationStatus(reservation.getId(), "Confirmed");

                    if (result > 0) {
                        Toast.makeText(this, "Reservation confirmed!", Toast.LENGTH_SHORT).show();

                        // Send notification to customer
                        databaseHelper.addNotification(
                                "Reservation Confirmed",
                                "Your reservation for " + reservation.getDate() + " at " +
                                        reservation.getTime() + " has been confirmed!",
                                getCurrentTimestamp(),
                                "reservation",
                                reservation.getUserEmail()
                        );

                        // Reload current tab
                        loadReservations("Pending");
                    } else {
                        Toast.makeText(this, "Failed to confirm reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Show confirmation dialog before cancelling
     */
    private void showCancelConfirmationDialog(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?\n\n" +
                        "Guest: " + reservation.getCustomerName() + "\n" +
                        "Date: " + reservation.getDate() + "\n" +
                        "Time: " + reservation.getTime() + "\n\n" +
                        "This action cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    cancelReservation(reservation);
                })
                .setNegativeButton("No, Keep It", null)
                .setCancelable(true)
                .show();
    }

    /**
     * Cancel a reservation
     */
    private void cancelReservation(Reservation reservation) {
        int result = databaseHelper.updateReservationStatus(reservation.getId(), "Cancelled");

        if (result > 0) {
            Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();

            // Send notification to customer
            databaseHelper.addNotification(
                    "Reservation Cancelled",
                    "Your reservation for " + reservation.getDate() + " at " +
                            reservation.getTime() + " has been cancelled by the restaurant.",
                    getCurrentTimestamp(),
                    "reservation",
                    reservation.getUserEmail()
            );

            // Reload current tab
            int currentTab = tabLayout.getSelectedTabPosition();
            switch(currentTab) {
                case 0: loadReservations("Pending"); break;
                case 1: loadReservations("Confirmed"); break;
                case 2: loadReservations("Seated"); break;
                case 3: loadReservations("Cancelled"); break;
            }
        } else {
            Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get current timestamp for notifications
     */
    private String getCurrentTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "MMM dd, yyyy h:mm a",
                java.util.Locale.getDefault()
        );
        return sdf.format(new java.util.Date());
    }

    /**
     * Setup bottom navigation
     */
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_staff_dashboard);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_staff_dashboard) {
                return true;
            } else if (id == R.id.nav_menu) {
                startActivity(new Intent(this, StaffMenuActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload reservations when returning to activity
        int currentTab = tabLayout.getSelectedTabPosition();
        switch(currentTab) {
            case 0: loadReservations("Pending"); break;
            case 1: loadReservations("Confirmed"); break;
            case 2: loadReservations("Seated"); break;
            case 3: loadReservations("Cancelled"); break;
        }
    }
}