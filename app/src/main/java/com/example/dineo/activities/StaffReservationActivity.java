package com.example.dineo.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.StaffReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StaffReservationActivity extends StaffBaseActivity
        implements StaffReservationAdapter.OnReservationClickListener {

    private RecyclerView recyclerView;
    private StaffReservationAdapter adapter;
    private List<Reservation> allReservations;

    private TabLayout tabLayout;
    private TextView textViewDateFilter;
    private ImageView imageViewNotification;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_reservation);

        dbHelper = new DatabaseHelper(this);

        // Notification icon
        imageViewNotification = findViewById(R.id.imageViewNotification);
        imageViewNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, StaffNotificationActivity.class));
        });

        // Date filter - FIXED ID
        textViewDateFilter = findViewById(R.id.textViewDateFilter);
        textViewDateFilter.setOnClickListener(v -> showDatePicker());

        // Tabs
        tabLayout = findViewById(R.id.tabLayoutStatus);
        setupTabs();

        // RecyclerView - FIXED ID
        recyclerView = findViewById(R.id.recyclerViewReservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allReservations = new ArrayList<>();

        // Pass 'this' as the listener since this activity implements the interface
        adapter = new StaffReservationAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Setup bottom navigation
        setupStaffBottomNavigation(R.id.nav_staff_reservations);

        loadReservations();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Confirmed"));
        tabLayout.addTab(tabLayout.newTab().setText("Seated"));
        tabLayout.addTab(tabLayout.newTab().setText("Cancelled"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterReservations();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    textViewDateFilter.setText(formattedDate);
                    filterReservations();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadReservations() {
        allReservations.clear();
        allReservations.addAll(dbHelper.getAllReservations());
        filterReservations();
    }

    private void filterReservations() {
        if (allReservations == null) return;

        String selectedStatus = tabLayout.getSelectedTabPosition() == 0 ? "All" :
                tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();

        String selectedDate = textViewDateFilter.getText().toString().trim();
        if (selectedDate.equals("Select Date")) selectedDate = "";

        List<Reservation> filtered = new ArrayList<>();
        for (Reservation res : allReservations) {
            boolean matchStatus = selectedStatus.equals("All") || res.getStatus().equalsIgnoreCase(selectedStatus);
            boolean matchDate = selectedDate.isEmpty() || res.getDate().equals(selectedDate);

            if (matchStatus && (selectedDate.isEmpty() || matchDate)) {
                filtered.add(res);
            }
        }
        adapter.setReservations(filtered);
    }

    // ============== INTERFACE IMPLEMENTATIONS ==============

    @Override
    public void onReservationClick(Reservation reservation) {
        // Navigate to detail view
        Intent intent = new Intent(this, StaffReservationDetailActivity.class);
        intent.putExtra("RESERVATION_ID", reservation.getId());
        startActivity(intent);
    }

    @Override
    public void onConfirm(Reservation reservation) {
        int result = dbHelper.updateReservationStatus(reservation.getId(), "Confirmed");
        if (result > 0) {
            Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();

            // Send notification to user
            String title = "Reservation Confirmed";
            String message = "Your reservation on " + reservation.getDate() +
                    " at " + reservation.getTime() + " has been confirmed.";
            dbHelper.addNotification(title, message, dbHelper.getCurrentTimestamp(),
                    "reservation_confirmed", reservation.getUserEmail());

            loadReservations();
        } else {
            Toast.makeText(this, "Failed to confirm reservation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancel(Reservation reservation) {
        int result = dbHelper.updateReservationStatus(reservation.getId(), "Cancelled");
        if (result > 0) {
            Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();

            // Send notification to user
            String title = "Reservation Cancelled";
            String message = "Your reservation on " + reservation.getDate() +
                    " at " + reservation.getTime() + " has been cancelled.";
            dbHelper.addNotification(title, message, dbHelper.getCurrentTimestamp(),
                    "reservation_cancelled", reservation.getUserEmail());

            loadReservations();
        } else {
            Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }
}