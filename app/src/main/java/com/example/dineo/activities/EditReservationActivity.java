package com.example.dineo.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Edit Reservation Activity - SECURED
 * Only allows users to edit their own reservations
 */
public class EditReservationActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private EditText editTextDate, editTextTime, editTextGuests, editTextTable, editTextRequests;
    private Button btnSaveChanges, btnCancel;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private Reservation reservation;
    private Calendar calendar;
    private int reservationId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reservation);

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextGuests = findViewById(R.id.editTextGuests);
        editTextTable = findViewById(R.id.editTextTable);
        editTextRequests = findViewById(R.id.editTextRequests);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);

        // Initialize
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        calendar = Calendar.getInstance();

        // Get user email from SharedPreferences
        userEmail = getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get reservation ID from intent
        reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);
        if (reservationId == -1) {
            Toast.makeText(this, "Invalid reservation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // SECURITY: Load reservation with ownership verification
        loadReservation();

        // Setup date and time pickers
        setupDateTimePickers();

        // Setup back button
        imageViewBack.setOnClickListener(v -> finish());

        // Setup save button
        btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Setup cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Get user email from SharedPreferences
     */
    private String getUserEmail() {
        String email = sharedPreferences.getString("userEmail", "");

        // Fallback: Try to get from user_json
        if (email.isEmpty()) {
            String userJson = sharedPreferences.getString("user_json", null);
            if (userJson != null) {
                try {
                    JSONObject json = new JSONObject(userJson);
                    email = json.optString("email", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return email;
    }

    /**
     * SECURITY: Load reservation from database WITH ownership verification
     */
    private void loadReservation() {
        // Use the secure method that verifies ownership
        reservation = databaseHelper.getReservationByIdAndEmail(reservationId, userEmail);

        if (reservation == null) {
            // Reservation not found OR doesn't belong to this user
            Toast.makeText(this,
                    "Unauthorized: You can only edit your own reservations",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Check if reservation can be edited
        String status = reservation.getStatus();
        if ("Cancelled".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) {
            Toast.makeText(this,
                    "Cannot edit " + status.toLowerCase() + " reservation",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Populate fields with reservation data
        editTextDate.setText(reservation.getDate());
        editTextTime.setText(reservation.getTime());
        editTextGuests.setText(String.valueOf(reservation.getNumberOfGuests()));
        editTextTable.setText(reservation.getTableNumber());
        editTextRequests.setText(reservation.getSpecialRequests());
    }

    private void setupDateTimePickers() {
        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());
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

        // Can only select future dates
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

    private void saveChanges() {
        // Validation
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String guestsStr = editTextGuests.getText().toString().trim();
        String table = editTextTable.getText().toString().trim();
        String requests = editTextRequests.getText().toString().trim();

        if (date.isEmpty()) {
            editTextDate.setError("Please select a date");
            return;
        }

        if (time.isEmpty()) {
            editTextTime.setError("Please select a time");
            return;
        }

        if (guestsStr.isEmpty()) {
            editTextGuests.setError("Please enter number of guests");
            return;
        }

        int guests;
        try {
            guests = Integer.parseInt(guestsStr);
            if (guests <= 0) {
                editTextGuests.setError("Number of guests must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            editTextGuests.setError("Please enter a valid number");
            return;
        }

        // SECURITY: Verify ownership one more time before updating
        Reservation verifyOwnership = databaseHelper.getReservationByIdAndEmail(reservationId, userEmail);
        if (verifyOwnership == null) {
            Toast.makeText(this,
                    "Cannot save: You do not own this reservation",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Update reservation object
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setNumberOfGuests(guests);
        reservation.setTableNumber(table);
        reservation.setSpecialRequests(requests);
        // Keep status as is (only staff can change status)

        // Save to database
        int result = databaseHelper.updateReservation(reservation);

        if (result > 0) {
            // Create notification
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
            String timestamp = sdf.format(Calendar.getInstance().getTime());

            databaseHelper.addNotification(
                    "Reservation Modified",
                    "Your reservation has been updated to " + date + " at " + time,
                    timestamp,
                    "reservation_modified",
                    userEmail
            );

            Toast.makeText(this, "Reservation updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Notify caller that changes were made
            finish();
        } else {
            Toast.makeText(this, "Failed to update reservation", Toast.LENGTH_SHORT).show();
        }
    }
}