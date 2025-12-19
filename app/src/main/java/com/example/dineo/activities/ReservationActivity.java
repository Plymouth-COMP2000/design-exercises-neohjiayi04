package com.example.dineo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.ReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ReservationActivity - Complete Version with BottomNavigationView
 * Student ID: BSSE2506008
 */
public class ReservationActivity extends AppCompatActivity {

    private EditText editTextDate, editTextTime, editTextGuests, editTextSpecialRequests;
    private Spinner spinnerTable;
    private Button btnReserve;
    private ImageView imageViewNotification;
    private RecyclerView recyclerViewReservations;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigationView;

    private ReservationAdapter reservationAdapter;
    private DatabaseHelper databaseHelper;
    private List<Reservation> reservations;

    private String selectedDate = "";
    private String selectedTime = "";
    private String userEmail = "";
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Get user info
        SharedPreferences prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("userEmail", "");
        userName = prefs.getString("userName", "Guest");

        // Initialize views
        initializeViews();

        // Setup listeners
        setupClickListeners();

        // Setup table spinner
        setupTableSpinner();

        // Setup tabs
        setupTabs();

        // Setup bottom navigation
        setupBottomNavigation();

        // Load reservations
        loadReservations("upcoming");
    }

    private void initializeViews() {
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextGuests = findViewById(R.id.editTextGuests);
        editTextSpecialRequests = findViewById(R.id.editTextSpecialRequests);
        spinnerTable = findViewById(R.id.spinnerTable);
        btnReserve = findViewById(R.id.btnReserve);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        recyclerViewReservations = findViewById(R.id.recyclerViewReservations);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setup RecyclerView
        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(this));
        reservations = new ArrayList<>();
    }

    private void setupClickListeners() {
        imageViewNotification.setOnClickListener(v -> {
            // Navigate to notifications
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
        });

        btnReserve.setOnClickListener(v -> createReservation());
        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());
    }

    private void setupTableSpinner() {
        String[] tables = {
                "Any Table",
                "Indoor - Table 1 (2 seats)",
                "Indoor - Table 2 (4 seats)",
                "Indoor - Table 3 (4 seats)",
                "Outdoor - Table 4 (6 seats)",
                "Outdoor - Table 5 (6 seats)",
                "Outdoor - Table 6 (8 seats)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tables);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTable.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("UPCOMING"));
        tabLayout.addTab(tabLayout.newTab().setText("FINISHED"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadReservations("upcoming");
                } else {
                    loadReservations("finished");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_reservation) {
                return true;
            } else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(this, MenuActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        // Highlight the reservation item by default
        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    selectedDate = sdf.format(calendar.getTime());
                    editTextDate.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    selectedTime = sdf.format(calendar.getTime());
                    editTextTime.setText(selectedTime);
                }, hour, minute, false);

        timePickerDialog.show();
    }

    private void createReservation() {
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String guestsStr = editTextGuests.getText().toString().trim();
        String tableSelection = spinnerTable.getSelectedItem().toString();
        String specialRequests = editTextSpecialRequests.getText().toString().trim();

        if (date.isEmpty()) { editTextDate.setError("Please select a date"); return; }
        if (time.isEmpty()) { editTextTime.setError("Please select a time"); return; }
        if (guestsStr.isEmpty()) { editTextGuests.setError("Please enter number of guests"); return; }

        int guests;
        try {
            guests = Integer.parseInt(guestsStr);
            if (guests <= 0) { editTextGuests.setError("Number of guests must be positive"); return; }
        } catch (NumberFormatException e) {
            editTextGuests.setError("Please enter a valid number"); return;
        }

        Reservation reservation = new Reservation();
        reservation.setCustomerName(userName);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setNumberOfGuests(guests);
        reservation.setTableNumber(tableSelection);
        reservation.setSpecialRequests(specialRequests);
        reservation.setStatus("Pending");
        reservation.setUserEmail(userEmail);

        long result = databaseHelper.addReservation(reservation);

        if (result > 0) {
            createNotification();
            showSuccessDialog();
            clearInputs();
            loadReservations("upcoming");
        } else {
            Toast.makeText(this, "Failed to create reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotification() {
        String timestamp = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
                .format(new Date());
        databaseHelper.addNotification(
                "Reservation Created",
                "Your reservation for " + selectedDate + " at " + selectedTime + " has been created successfully.",
                timestamp,
                "reservation",
                userEmail
        );
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("✅ Booking Successful!")
                .setMessage("Your reservation has been created successfully. We will inform you about the confirmation shortly.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void clearInputs() {
        editTextDate.setText("");
        editTextTime.setText("");
        editTextGuests.setText("");
        editTextSpecialRequests.setText("");
        spinnerTable.setSelection(0);
        selectedDate = "";
        selectedTime = "";
    }

    private void loadReservations(String type) {
        List<Reservation> allReservations = databaseHelper.getUserReservations(userEmail);
        reservations.clear();
        for (Reservation res : allReservations) {
            String status = res.getStatus();
            if (type.equals("upcoming")) {
                if (status.equals("Pending") || status.equals("Confirmed")) reservations.add(res);
            } else {
                if (status.equals("Cancelled") || status.equals("Completed")) reservations.add(res);
            }
        }

        reservationAdapter = new ReservationAdapter(this, reservations, new ReservationAdapter.OnReservationClickListener() {
            @Override
            public void onCancelClick(Reservation reservation) { cancelReservation(reservation); }

            @Override
            public void onEditClick(Reservation reservation) {
                Toast.makeText(ReservationActivity.this, "Edit not available", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerViewReservations.setAdapter(reservationAdapter);
    }

    private void cancelReservation(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int result = databaseHelper.cancelReservation(reservation.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                        String timestamp = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault()).format(new Date());
                        databaseHelper.addNotification(
                                "Reservation Cancelled",
                                "Your reservation for " + reservation.getDate() + " at " + reservation.getTime() + " has been cancelled.",
                                timestamp,
                                "reservation",
                                userEmail
                        );
                        loadReservations("upcoming");
                    } else {
                        Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
