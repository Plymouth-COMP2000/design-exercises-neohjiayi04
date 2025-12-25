package com.example.dineo.reservation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.ReservationListAdapter;
import com.example.dineo.dashboard.DashboardActivity;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.menu.StaffMenuActivity;
import com.example.dineo.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {

    private TextView tvDate;
    private ChipGroup chipGroupFilters;
    private Chip chipAll, chipUpcoming, chipSeated, chipNoShow;
    private RecyclerView rvReservations;
    private MaterialCardView notificationButton;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private ReservationListAdapter adapter;
    private String currentFilter = "All";
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        initializeViews();
        setupRecyclerView();
        setupFilters();
        setupListeners();
        loadReservations();
    }

    private void initializeViews() {
        tvDate = findViewById(R.id.tv_date);
        chipGroupFilters = findViewById(R.id.chip_group_filters);
        chipAll = findViewById(R.id.chip_all);
        chipUpcoming = findViewById(R.id.chip_upcoming);
        chipSeated = findViewById(R.id.chip_seated);
        chipNoShow = findViewById(R.id.chip_no_show);
        rvReservations = findViewById(R.id.rv_reservations);
        notificationButton = findViewById(R.id.notification_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        databaseHelper = new DatabaseHelper(this);

        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = sqlFormat.format(new Date());
        tvDate.setText(dateFormat.format(new Date()));
    }

    private void setupRecyclerView() {
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservationListAdapter(this, null);
        rvReservations.setAdapter(adapter);

        adapter.setOnReservationClickListener(reservation -> {
            Intent intent = new Intent(ReservationActivity.this, ReservationDetailsActivity.class);
            intent.putExtra("RESERVATION_ID", reservation.getId());
            startActivity(intent);
        });
    }

    private void setupFilters() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_all) {
                    currentFilter = "All";
                } else if (checkedId == R.id.chip_upcoming) {
                    currentFilter = "Upcoming";
                } else if (checkedId == R.id.chip_seated) {
                    currentFilter = "Seated";
                } else if (checkedId == R.id.chip_no_show) {
                    currentFilter = "No-Show";
                }
                loadReservations();
            }
        });
    }

    private void setupListeners() {
        // Date click - open calendar
        tvDate.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationActivity.this, CalendarReservationsActivity.class);
            startActivity(intent);
        });

        // Notification button
        notificationButton.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
        });

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_reservations);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(this, StaffMenuActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_reservations) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadReservations() {
        List<Reservation> allReservations = databaseHelper.getReservationsByDate(currentDate);
        List<Reservation> filteredReservations = new ArrayList<>();

        // Filter based on selected chip
        for (Reservation reservation : allReservations) {
            if (currentFilter.equals("All")) {
                filteredReservations.add(reservation);
            } else if (reservation.getStatus().equalsIgnoreCase(currentFilter)) {
                filteredReservations.add(reservation);
            }
        }

        adapter.updateReservations(filteredReservations);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}