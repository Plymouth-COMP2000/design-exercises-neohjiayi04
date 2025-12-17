package com.example.dineo.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.ReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvMenuCount, tvReservationCount;
    private RecyclerView rvReservations;
    private MaterialCardView notificationButton;
    private MaterialCardView cardAddMenu, cardManageTable, cardTodayReservation;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private ReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupRecyclerView();
        setupListeners();
        loadDashboardData();
    }

    private void initializeViews() {
        tvMenuCount = findViewById(R.id.tv_menu_count);
        tvReservationCount = findViewById(R.id.tv_reservation_count);
        rvReservations = findViewById(R.id.rv_reservations);
        notificationButton = findViewById(R.id.notification_button);
        cardAddMenu = findViewById(R.id.card_add_menu);
        cardManageTable = findViewById(R.id.card_manage_table);
        cardTodayReservation = findViewById(R.id.card_today_reservation);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        databaseHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        reservationAdapter = new ReservationAdapter(this, null);
        rvReservations.setAdapter(reservationAdapter);

        reservationAdapter.setOnReservationClickListener(reservation -> {
            // TODO: Navigate to reservation details
            Toast.makeText(this, "Reservation: " + reservation.getCustomerName(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        // Notification button
        notificationButton.setOnClickListener(v -> {
            // TODO: Navigate to notifications
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
        });

        // Quick Action: Add Menu Item
        cardAddMenu.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddMenuItemActivity.class);
            startActivity(intent);
        });

        // Quick Action: Manage Table
        cardManageTable.setOnClickListener(v -> {
            // TODO: Navigate to table management
            Toast.makeText(this, "Manage Table - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Quick Action: View Today's Reservation
        cardTodayReservation.setOnClickListener(v -> {
            // TODO: Navigate to today's reservations
            Toast.makeText(this, "Today's Reservations - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                // Already here
                return true;
            } else if (itemId == R.id.nav_menu) {
                // Navigate to Staff Menu
                Intent intent = new Intent(DashboardActivity.this, StaffMenuActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_reservations) {
                // TODO: Navigate to reservations
                Toast.makeText(this, "Reservations - Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // TODO: Navigate to profile
                Toast.makeText(this, "Profile - Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void loadDashboardData() {
        // Load menu item count
        int menuCount = databaseHelper.getAllMenuItems().size();
        tvMenuCount.setText(String.valueOf(menuCount));

        // Load upcoming reservations count and list
        loadUpcomingReservations();
    }

    private void loadUpcomingReservations() {
        // Get today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());

        // Get upcoming reservations from database
        List<Reservation> upcomingReservations = databaseHelper.getUpcomingReservations(today);

        // Update count
        tvReservationCount.setText(String.valueOf(upcomingReservations.size()));

        // Update list (show only first 3 for dashboard)
        int displayCount = Math.min(3, upcomingReservations.size());
        List<Reservation> displayReservations = upcomingReservations.subList(0, displayCount);
        reservationAdapter.updateReservations(displayReservations);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to dashboard
        loadDashboardData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}