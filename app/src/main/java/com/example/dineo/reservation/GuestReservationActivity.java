package com.example.dineo.reservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.GuestReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.example.dineo.menu.GuestMenuActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GuestReservationActivity extends AppCompatActivity {

    private TextView tvDate, tvTime, tvGuests, tvTable;
    private EditText etRequests;
    private MaterialButton btnReserve, btnUpcoming, btnFinished;
    private RecyclerView rvReservations;
    private MaterialCardView notificationButton;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private GuestReservationAdapter adapter;

    private Calendar selectedDate;
    private int selectedHour = 19;
    private int selectedMinute = 30;
    private int selectedGuests = 4;
    private String selectedTable = "Any";
    private boolean showingUpcoming = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_reservation);

        selectedDate = Calendar.getInstance();

        initializeViews();
        setupRecyclerView();
        setupListeners();
        loadReservations();
    }

    private void initializeViews() {
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvGuests = findViewById(R.id.tv_guests);
        tvTable = findViewById(R.id.tv_table);
        etRequests = findViewById(R.id.et_requests);
        btnReserve = findViewById(R.id.btn_reserve);
        btnUpcoming = findViewById(R.id.btn_upcoming);
        btnFinished = findViewById(R.id.btn_finished);
        rvReservations = findViewById(R.id.rv_reservations);
        notificationButton = findViewById(R.id.notification_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        databaseHelper = new DatabaseHelper(this);

        updateDateDisplay();
        updateTimeDisplay();
        tvGuests.setText(String.valueOf(selectedGuests));
        tvTable.setText(selectedTable);
    }

    private void setupRecyclerView() {
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuestReservationAdapter(this, null);
        rvReservations.setAdapter(adapter);

        adapter.setOnReservationActionListener(new GuestReservationAdapter.OnReservationActionListener() {
            @Override
            public void onEditClick(Reservation reservation) {
                Intent intent = new Intent(GuestReservationActivity.this, EditGuestReservationActivity.class);
                intent.putExtra("RESERVATION_ID", reservation.getId());
                startActivity(intent);
            }

            @Override
            public void onCancelClick(Reservation reservation) {
                showCancelDialog(reservation);
            }
        });
    }

    private void setupListeners() {
        // Date picker
        tvDate.setOnClickListener(v -> showDatePicker());

        // Time picker
        tvTime.setOnClickListener(v -> showTimePicker());

        // Guests picker
        tvGuests.setOnClickListener(v -> showGuestsPicker());

        // Table picker
        tvTable.setOnClickListener(v -> showTablePicker());

        // Reserve button
        btnReserve.setOnClickListener(v -> createReservation());

        // Tab buttons
        btnUpcoming.setOnClickListener(v -> {
            showingUpcoming = true;
            updateTabButtons();
            loadReservations();
        });

        btnFinished.setOnClickListener(v -> {
            showingUpcoming = false;
            updateTabButtons();
            loadReservations();
        });

        // Notification button
        notificationButton.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
        });

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_menu) {
                startActivity(new Intent(this, GuestMenuActivity.class));
                return true;
            } else if (itemId == R.id.nav_reservation) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateDisplay();
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
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    updateTimeDisplay();
                },
                selectedHour,
                selectedMinute,
                false
        );
        timePickerDialog.show();
    }

    private void showGuestsPicker() {
        String[] options = {"1", "2", "3", "4", "5", "6", "7", "8"};
        new AlertDialog.Builder(this)
                .setTitle("Number of Guests")
                .setItems(options, (dialog, which) -> {
                    selectedGuests = which + 1;
                    tvGuests.setText(String.valueOf(selectedGuests));
                })
                .show();
    }

    private void showTablePicker() {
        String[] options = {"Any", "Window Seat", "Patio", "Private Room", "Bar Area"};
        new AlertDialog.Builder(this)
                .setTitle("Table Preference")
                .setItems(options, (dialog, which) -> {
                    selectedTable = options[which];
                    tvTable.setText(selectedTable);
                })
                .show();
    }

    private void createReservation() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(selectedDate.getTime());
        String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
        String requests = etRequests.getText().toString().trim();

        // TODO: Get customer name from logged-in user
        String customerName = "Guest User";

        Reservation reservation = new Reservation(
                0,
                customerName,
                date,
                time,
                selectedGuests,
                "Upcoming",
                null, // phone
                null, // email
                requests.isEmpty() ? null : requests,
                selectedTable.equals("Any") ? "TBD" : selectedTable
        );

        long result = databaseHelper.addReservation(reservation);

        if (result != -1) {
            showSuccessDialog();
            clearForm();
            loadReservations();
        } else {
            Toast.makeText(this, "Failed to create reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success!")
                .setMessage("Your reservation has been successfully booked.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showCancelDialog(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int result = databaseHelper.updateReservationStatus(reservation.getId(), "Cancelled");
                    if (result > 0) {
                        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                        loadReservations();
                    } else {
                        Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clearForm() {
        selectedDate = Calendar.getInstance();
        selectedHour = 19;
        selectedMinute = 30;
        selectedGuests = 4;
        selectedTable = "Any";
        etRequests.setText("");

        updateDateDisplay();
        updateTimeDisplay();
        tvGuests.setText(String.valueOf(selectedGuests));
        tvTable.setText(selectedTable);
    }

    private void updateDateDisplay() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        tvDate.setText(displayFormat.format(selectedDate.getTime()));
    }

    private void updateTimeDisplay() {
        String period = selectedHour >= 12 ? "PM" : "AM";
        int displayHour = selectedHour > 12 ? selectedHour - 12 : (selectedHour == 0 ? 12 : selectedHour);
        tvTime.setText(String.format(Locale.getDefault(), "%d:%02d %s", displayHour, selectedMinute, period));
    }

    private void updateTabButtons() {
        if (showingUpcoming) {
            btnUpcoming.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.orange_primary, null)));
            btnUpcoming.setTextColor(getResources().getColor(R.color.white, null));

            btnFinished.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.white, null)));
            btnFinished.setTextColor(getResources().getColor(R.color.text_secondary, null));
        } else {
            btnFinished.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.orange_primary, null)));
            btnFinished.setTextColor(getResources().getColor(R.color.white, null));

            btnUpcoming.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.white, null)));
            btnUpcoming.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }
    }

    private void loadReservations() {
        // TODO: Filter by logged-in user
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        List<Reservation> filteredReservations = new ArrayList<>();

        for (Reservation reservation : allReservations) {
            String status = reservation.getStatus().toLowerCase();

            if (showingUpcoming) {
                // Show upcoming: Upcoming, Confirmed, Seated
                if (status.equals("upcoming") || status.equals("confirmed") || status.equals("seated")) {
                    filteredReservations.add(reservation);
                }
            } else {
                // Show finished: Completed, Cancelled, No-Show
                if (status.equals("completed") || status.equals("cancelled") || status.equals("no-show")) {
                    filteredReservations.add(reservation);
                }
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