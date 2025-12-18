package com.example.dineo.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Reservation Activity - Book and manage reservations
 * Student ID: BSSE2506008
 */
public class ReservationActivity extends AppCompatActivity implements ReservationAdapter.OnReservationClickListener {

    private EditText editTextDate, editTextTime, editTextGuests, editTextTable, editTextRequests;
    private Button btnReserve;
    private RecyclerView recyclerViewReservations;
    private TabLayout tabLayout;
    private ImageView imageViewNotification;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private ReservationAdapter reservationAdapter;
    private Calendar calendar;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Initialize views
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextGuests = findViewById(R.id.editTextGuests);
        editTextTable = findViewById(R.id.editTextTable);
        editTextRequests = findViewById(R.id.editTextRequests);
        btnReserve = findViewById(R.id.btnReserve);
        recyclerViewReservations = findViewById(R.id.recyclerViewReservations);
        tabLayout = findViewById(R.id.tabLayout);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize database and preferences
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("userEmail", "");
        calendar = Calendar.getInstance();

        // Setup RecyclerView
        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(this));

        // Setup date and time pickers
        setupDateTimePickers();

        // Setup reserve button
        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReservation();
            }
        });

        // Setup tabs
        setupTabs();

        // Setup notification bell
        imageViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        // Setup bottom navigation
        setupBottomNavigation();

        // Load reservations
        loadReservations("upcoming");
    }

    private void setupDateTimePickers() {
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    editTextDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    editTextTime.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void createReservation() {
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String guestsStr = editTextGuests.getText().toString().trim();
        String tableStr = editTextTable.getText().toString().trim();
        String requests = editTextRequests.getText().toString().trim();

        // Validate inputs
        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (guestsStr.isEmpty()) {
            Toast.makeText(this, "Please enter number of guests", Toast.LENGTH_SHORT).show();
            return;
        }

        int guests = Integer.parseInt(guestsStr);
        int tableNumber = tableStr.isEmpty() ? 0 : Integer.parseInt(tableStr);
        String customerName = sharedPreferences.getString("userName", "Guest");

        // Create reservation
        long reservationId = databaseHelper.addReservationWithEmail(
                customerName, date, time, tableNumber, guests, "Confirmed", userEmail
        );

        if (reservationId > 0) {
            Toast.makeText(this, "Reservation created successfully!", Toast.LENGTH_SHORT).show();

            // Create notification
            createNotification("Reservation Confirmed",
                    "Your table is confirmed at " + time + " on " + date + ".");

            // Clear fields
            clearFields();

            // Reload reservations
            loadReservations("upcoming");
        } else {
            Toast.makeText(this, "Failed to create reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextDate.setText("");
        editTextTime.setText("");
        editTextGuests.setText("");
        editTextTable.setText("");
        editTextRequests.setText("");
    }

    private void setupTabs() {
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

    private void loadReservations(String type) {
        List<Reservation> reservations = databaseHelper.getUserReservationsByEmail(userEmail);

        // Filter by type (you can add status filtering logic here)
        reservationAdapter = new ReservationAdapter(this, reservations, this);
        recyclerViewReservations.setAdapter(reservationAdapter);
    }

    @Override
    public void onEditClick(Reservation reservation) {
        Intent intent = new Intent(this, EditReservationActivity.class);
        intent.putExtra("RESERVATION_ID", reservation.getId());
        startActivity(intent);
    }

    @Override
    public void onCancelClick(Reservation reservation) {
        databaseHelper.cancelReservation(reservation.getId());

        // Create notification
        createNotification("Reservation Cancelled",
                "Your reservation for " + reservation.getDate() + " has been cancelled.");

        loadReservations(tabLayout.getSelectedTabPosition() == 0 ? "upcoming" : "finished");
        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
    }

    private void createNotification(String title, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        String timestamp = sdf.format(Calendar.getInstance().getTime());

        databaseHelper.addNotification(title, message, timestamp, "reservation_confirmed", userEmail);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_menu) {
                startActivity(new Intent(ReservationActivity.this, MenuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_reservation) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(ReservationActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);
        loadReservations(tabLayout.getSelectedTabPosition() == 0 ? "upcoming" : "finished");
    }
}