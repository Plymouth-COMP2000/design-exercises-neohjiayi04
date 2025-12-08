package com.example.dineo.guest;
import com.example.dineo.NotificationActivity;
import com.example.dineo.R;
import com.example.dineo.Reservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.ProfileActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GuestReservationActivity extends AppCompatActivity {

    private ImageButton btnNotification;
    private TextInputEditText etDate, etTime, etGuests, etTable, etRequests;
    private MaterialButton btnReserve;
    private TextView tabUpcoming, tabFinished;
    private RecyclerView reservationRecyclerView;
    private LinearLayout navMenu, navReservation, navProfile;

    private Calendar selectedDate;
    private Calendar selectedTime;
    private ReservationAdapter adapter;
    private List<Reservation> allReservations;
    private List<Reservation> upcomingReservations;
    private List<Reservation> finishedReservations;
    private boolean isUpcomingTabSelected = true;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_reservation);

        selectedDate = Calendar.getInstance();
        selectedTime = Calendar.getInstance();
        gson = new Gson();
        sharedPreferences = getSharedPreferences("ReservationPrefs", MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        loadReservationsFromStorage();
        setupRecyclerView();
    }

    private void initializeViews() {
        btnNotification = findViewById(R.id.btnNotification);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etGuests = findViewById(R.id.etGuests);
        etTable = findViewById(R.id.etTable);
        etRequests = findViewById(R.id.etRequests);
        btnReserve = findViewById(R.id.btnReserve);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabFinished = findViewById(R.id.tabFinished);
        reservationRecyclerView = findViewById(R.id.reservationRecyclerView);
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupClickListeners() {
        btnNotification.setOnClickListener(v -> openNotificationPage());
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        etGuests.setOnClickListener(v -> showGuestsPicker());
        etTable.setOnClickListener(v -> showTablePicker());
        btnReserve.setOnClickListener(v -> makeReservation());
        tabUpcoming.setOnClickListener(v -> switchToUpcomingTab());
        tabFinished.setOnClickListener(v -> switchToFinishedTab());

        navMenu.setOnClickListener(v -> {
            // Navigate to Menu activity
        });

        navReservation.setOnClickListener(v -> {
            // Already on reservation page
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(GuestReservationActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    etDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    etTime.setText(timeFormat.format(selectedTime.getTime()));
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void showGuestsPicker() {
        String[] options = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10+"};

        new AlertDialog.Builder(this)
                .setTitle("Select Number of Guests")
                .setItems(options, (dialog, which) -> {
                    etGuests.setText(options[which]);
                })
                .show();
    }

    private void showTablePicker() {
        String[] options = {"Any", "Table 1", "Table 2", "Table 3", "Table 4", "Table 5",
                "Table 6", "Table 7", "Table 8", "Table 9", "Table 10"};

        new AlertDialog.Builder(this)
                .setTitle("Select Table")
                .setItems(options, (dialog, which) -> {
                    etTable.setText(options[which]);
                })
                .show();
    }

    private void makeReservation() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String guests = etGuests.getText().toString().trim();
        String table = etTable.getText().toString().trim();
        String requests = etRequests.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(guests)) {
            Toast.makeText(this, "Please select number of guests", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(table)) {
            Toast.makeText(this, "Please select a table", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new reservation from user input
        String dateTime = date + " at " + time;
        String paxInfo = guests + " Pax";
        String tableInfo = table.equals("Any") ? "Table: Any" : table;

        Reservation newReservation = new Reservation(dateTime, paxInfo, tableInfo, "Confirmed");
        newReservation.setRequests(requests); // Save optional requests

        // Add to list
        if (allReservations == null) {
            allReservations = new ArrayList<>();
        }
        allReservations.add(newReservation);

        // Save to storage
        saveReservationsToStorage();

        Toast.makeText(this, "Reservation confirmed!", Toast.LENGTH_SHORT).show();

        // Clear form
        etDate.setText("");
        etTime.setText("");
        etGuests.setText("");
        etTable.setText("");
        etRequests.setText("");

        // Refresh the list
        loadReservationsFromStorage();
        switchToUpcomingTab();
    }

    private void saveReservationsToStorage() {
        String json = gson.toJson(allReservations);
        sharedPreferences.edit().putString("reservations", json).apply();
    }

    private void loadReservationsFromStorage() {
        String json = sharedPreferences.getString("reservations", null);
        Type type = new TypeToken<ArrayList<Reservation>>() {
        }.getType();

        if (json != null) {
            allReservations = gson.fromJson(json, type);
        } else {
            allReservations = new ArrayList<>();
        }

        // Separate into upcoming and finished
        upcomingReservations = new ArrayList<>();
        finishedReservations = new ArrayList<>();

        for (Reservation reservation : allReservations) {
            if (reservation.getStatus().equals("Confirmed") || reservation.getStatus().equals("Pending")) {
                upcomingReservations.add(reservation);
            } else if (reservation.getStatus().equals("Completed") || reservation.getStatus().equals("Cancelled")) {
                finishedReservations.add(reservation);
            }
        }
    }

    private void setupRecyclerView() {
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservationAdapter(upcomingReservations, new ReservationAdapter.OnReservationActionListener() {
            @Override
            public void onEdit(Reservation reservation) {
                editReservation(reservation);
            }

            @Override
            public void onCancel(Reservation reservation) {
                showCancelConfirmation(reservation);
            }
        });
        reservationRecyclerView.setAdapter(adapter);
    }

    private void editReservation(Reservation reservation) {
        // Pre-fill the form with existing reservation data
        Toast.makeText(this, "Edit mode: Modify the details and click Reserve to update", Toast.LENGTH_LONG).show();

        // Parse and set the data back to form
        String[] dateTimeParts = reservation.getDateTime().split(" at ");
        if (dateTimeParts.length == 2) {
            etDate.setText(dateTimeParts[0]);
            etTime.setText(dateTimeParts[1]);
        }

        String pax = reservation.getPax().replace(" Pax", "");
        etGuests.setText(pax);

        String table = reservation.getTable().replace("Table: ", "");
        etTable.setText(table);

        if (reservation.getRequests() != null) {
            etRequests.setText(reservation.getRequests());
        }

        // Remove the old reservation so it can be updated
        allReservations.remove(reservation);
        saveReservationsToStorage();
    }

    private void switchToUpcomingTab() {
        isUpcomingTabSelected = true;
        tabUpcoming.setBackgroundResource(R.drawable.tab_selected);
        tabUpcoming.setTextColor(getResources().getColor(R.color.white));
        tabFinished.setBackgroundResource(android.R.color.transparent);
        tabFinished.setTextColor(getResources().getColor(R.color.text_secondary));

        adapter.updateReservations(upcomingReservations);
    }

    private void switchToFinishedTab() {
        isUpcomingTabSelected = false;
        tabFinished.setBackgroundResource(R.drawable.tab_selected);
        tabFinished.setTextColor(getResources().getColor(R.color.white));
        tabUpcoming.setBackgroundResource(android.R.color.transparent);
        tabUpcoming.setTextColor(getResources().getColor(R.color.text_secondary));

        adapter.updateReservations(finishedReservations);
    }

    private void showCancelConfirmation(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Update status to Cancelled
                    reservation.setStatus("Cancelled");

                    // Move from upcoming to finished
                    upcomingReservations.remove(reservation);
                    finishedReservations.add(reservation);

                    // Save changes
                    saveReservationsToStorage();

                    Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();

                    // Refresh display
                    if (isUpcomingTabSelected) {
                        adapter.updateReservations(upcomingReservations);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void openNotificationPage() {
        Intent intent = new Intent(GuestReservationActivity.this, NotificationActivity.class);
        startActivity(intent);
    }
}
