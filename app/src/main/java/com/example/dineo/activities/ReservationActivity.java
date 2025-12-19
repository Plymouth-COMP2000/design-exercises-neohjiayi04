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
 * ReservationActivity - Corrected Version
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
    private List<Reservation> reservationList; // Renamed for clarity

    private String userEmail = "";
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Get user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("userEmail", "");
        userName = prefs.getString("userName", "Guest");

        // Initialize all views from the layout
        initializeViews();

        // Set up listeners for buttons and other clickable views
        setupClickListeners();

        // Populate the table selection spinner
        setupTableSpinner();

        // Configure the "Upcoming" and "Finished" tabs
        setupTabs();

        // Configure the bottom navigation bar
        setupBottomNavigation();

        // Load initial data for the "upcoming" tab
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
        reservationList = new ArrayList<>();
    }

    private void setupClickListeners() {
        // Since imageViewNotification exists in your XML, this should no longer crash
        if (imageViewNotification != null) {
            imageViewNotification.setOnClickListener(v -> {
                // Navigate to a future notifications activity if you have one
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
            });
        }

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
        // Set the listener FIRST
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // If the user taps the icon for the current activity, do nothing.
            if (itemId == R.id.nav_reservation) {
                return true;
            }

            // Navigate to MenuActivity
            if (itemId == R.id.nav_menu) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Prevent creating new instances
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }

            // Navigate to ProfileActivity
            if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Prevent creating new instances
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }

            return false;
        });

        // Set the default selected item LAST, after the listener is ready
        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    editTextDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    editTextTime.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);

        timePickerDialog.show();
    }

    private void createReservation() {
        // --- FORM VALIDATION ---
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String guestsStr = editTextGuests.getText().toString().trim();
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
        // --- END VALIDATION ---

        String specialRequests = editTextSpecialRequests.getText().toString().trim();

        // **FIX for NumberFormatException**: Get the text of the selected item, not the position.
        // The database column for table should be TEXT/VARCHAR, not INTEGER.
        String tableSelection = spinnerTable.getSelectedItem().toString();

        Reservation reservation = new Reservation();
        reservation.setCustomerName(userName);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setNumberOfGuests(guests);
        reservation.setTableNumber(tableSelection); // Save the full text, e.g., "Indoor - Table 1"
        reservation.setSpecialRequests(specialRequests);
        reservation.setStatus("Pending");
        reservation.setUserEmail(userEmail);

        long result = databaseHelper.addReservation(reservation);

        if (result > 0) {
            // Success
            createNotification("Reservation Created", "Your reservation for " + date + " at " + time + " has been created.");
            showSuccessDialog();
            clearInputs();
            loadReservations("upcoming"); // Refresh the list
        } else {
            // Failure
            Toast.makeText(this, "Failed to create reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotification(String title, String message, String... extras) {
        String timestamp = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault()).format(new Date());
        String type = (extras.length > 0) ? extras[0] : "reservation";
        databaseHelper.addNotification(title, message, timestamp, type, userEmail);
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("✅ Booking Successful!")
                .setMessage("Your reservation has been created. We will inform you about the confirmation shortly.")
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
    }

    private void loadReservations(String type) {
        List<Reservation> allReservations = databaseHelper.getUserReservations(userEmail);
        reservationList.clear();
        for (Reservation res : allReservations) {
            String status = res.getStatus();
            if ("upcoming".equals(type)) {
                if ("Pending".equals(status) || "Confirmed".equals(status)) {
                    reservationList.add(res);
                }
            } else { // "finished"
                if ("Cancelled".equals(status) || "Completed".equals(status)) {
                    reservationList.add(res);
                }
            }
        }

        // Re-use the adapter if it exists, otherwise create a new one
        if (reservationAdapter == null) {
            reservationAdapter = new ReservationAdapter(this, reservationList, new ReservationAdapter.OnReservationClickListener() {
                @Override
                public void onCancelClick(Reservation reservation) {
                    cancelReservation(reservation);
                }

                @Override
                public void onEditClick(Reservation reservation) {
                    // This can be implemented in the future
                    Toast.makeText(ReservationActivity.this, "Edit feature is not available.", Toast.LENGTH_SHORT).show();
                }
            });
            recyclerViewReservations.setAdapter(reservationAdapter);
        } else {
            // Just notify the adapter that the data has changed
            reservationAdapter.notifyDataSetChanged();
        }
    }

    private void cancelReservation(final Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int result = databaseHelper.cancelReservation(reservation.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                        createNotification(
                                "Reservation Cancelled",
                                "Your reservation for " + reservation.getDate() + " has been cancelled.",
                                "cancellation"
                        );
                        // Refresh the currently visible list
                        String currentTab = (tabLayout.getSelectedTabPosition() == 0) ? "upcoming" : "finished";
                        loadReservations(currentTab);
                    } else {
                        Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
