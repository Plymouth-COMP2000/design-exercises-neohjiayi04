package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.DashboardReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class StaffDashboardActivity extends StaffBaseActivity {

    private TextView textMenuItemsCount, textReservationsCount;
    private RecyclerView recyclerViewUpcomingReservations;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        dbHelper = new DatabaseHelper(this);

        textMenuItemsCount = findViewById(R.id.textMenuItemsCount);
        textReservationsCount = findViewById(R.id.textReservationsCount);
        recyclerViewUpcomingReservations = findViewById(R.id.recyclerViewUpcoming);

        recyclerViewUpcomingReservations.setLayoutManager(new LinearLayoutManager(this));

        // Quick actions
        MaterialButton btnAddMenuItem = findViewById(R.id.btnAddMenuItem);
        MaterialButton btnManageTable = findViewById(R.id.btnManageTable);
        MaterialButton btnViewReservations = findViewById(R.id.btnViewReservation);

        btnAddMenuItem.setOnClickListener(v -> startActivity(new Intent(this, com.example.dineo.staff.AddMenuItemActivity.class)));
        btnManageTable.setOnClickListener(v -> startActivity(new Intent(this, com.example.dineo.staff.StaffMenuActivity.class)));
        btnViewReservations.setOnClickListener(v -> startActivity(new Intent(this, StaffReservationActivity.class)));

        // Notification icon
        ImageView imageViewNotification = findViewById(R.id.imageViewNotification);
        imageViewNotification.setOnClickListener(v -> startActivity(new Intent(this, StaffNotificationActivity.class)));

        // Setup bottom navigation
        setupStaffBottomNavigation(R.id.nav_staff_dashboard);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Load counts
        int menuCount = dbHelper.getMenuItemsCount();
        int reservationCount = dbHelper.getUpcomingReservationsCount();

        textMenuItemsCount.setText(String.valueOf(menuCount));
        textReservationsCount.setText(String.valueOf(reservationCount));

        // Load upcoming reservations
        List<Reservation> upcomingReservations = dbHelper.getUpcomingReservations(3);
        DashboardReservationAdapter adapter = new DashboardReservationAdapter(upcomingReservations);
        recyclerViewUpcomingReservations.setAdapter(adapter);
    }
}
