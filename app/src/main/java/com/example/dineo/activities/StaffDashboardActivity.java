package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.DashboardReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;

import java.util.List;

/**
 * Staff Dashboard Activity - Overview of restaurant operations
 * Shows statistics, quick actions, and recent activity
 */
public class StaffDashboardActivity extends StaffBaseActivity {

    private TextView textMenuItemsCount, textReservationsCount;
    private RecyclerView recyclerViewUpcomingReservations;
    private CardView cardAddMenuItem, cardManageMenu, cardViewReservations;
    private DatabaseHelper dbHelper;
    private ImageView imageViewNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Setup notification icon
        setupNotificationIcon();

        // Setup bottom navigation
        setupStaffBottomNavigation(R.id.nav_staff_dashboard);
    }

    /**
     * Initialize all views
     */
    private void initializeViews() {
        // Statistics
        textMenuItemsCount = findViewById(R.id.textMenuItemsCount);
        textReservationsCount = findViewById(R.id.textReservationsCount);

        // Quick action cards
        cardAddMenuItem = findViewById(R.id.cardAddMenuItem);
        cardManageMenu = findViewById(R.id.cardManageMenu);
        cardViewReservations = findViewById(R.id.cardViewReservations);

        // RecyclerView for recent reservations
        recyclerViewUpcomingReservations = findViewById(R.id.recyclerViewUpcoming);
        recyclerViewUpcomingReservations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUpcomingReservations.setNestedScrollingEnabled(false);
    }

    /**
     * Setup click listeners for quick action cards
     */
    private void setupClickListeners() {
        // Add Menu Item
        cardAddMenuItem.setOnClickListener(v ->
                startActivity(new Intent(this, AddMenuItemActivity.class))
        );

        // View/Manage All Menus
        cardManageMenu.setOnClickListener(v ->
                startActivity(new Intent(this, StaffMenuActivity.class))
        );

        // View All Reservations
        cardViewReservations.setOnClickListener(v ->
                startActivity(new Intent(this, StaffReservationActivity.class))
        );
    }

    /**
     * Setup notification icon click listener for Staff
     */
    private void setupNotificationIcon() {
        imageViewNotification = findViewById(R.id.imageViewNotification);
        if (imageViewNotification != null) {
            imageViewNotification.setOnClickListener(v ->
                    startActivity(new Intent(this, StaffNotificationActivity.class))
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    /**
     * Load all dashboard data including statistics and recent activity
     */
    private void loadDashboardData() {
        // Load statistics counts
        int menuCount = dbHelper.getMenuItemsCount();
        int reservationCount = dbHelper.getUpcomingReservationsCount();

        textMenuItemsCount.setText(String.valueOf(menuCount));
        textReservationsCount.setText(String.valueOf(reservationCount));

        // Load recent/upcoming reservations (max 3 for dashboard preview)
        List<Reservation> upcomingReservations = dbHelper.getUpcomingReservations(3);
        DashboardReservationAdapter adapter = new DashboardReservationAdapter(upcomingReservations);
        recyclerViewUpcomingReservations.setAdapter(adapter);
    }
}