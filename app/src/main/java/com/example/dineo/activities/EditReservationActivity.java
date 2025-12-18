package com.example.dineo.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Edit Reservation Activity
 * Student ID: BSSE2506008
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

        // Get reservation ID
        reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);

        // Load reservation data
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

    private void loadReservation() {
        // In a real app, you'd query by ID
        // For now, we'll just initialize with data
        if (reservationId != -1) {
            // Load from database
            // For demonstration, we'll use placeholder data
            editTextDate.setText("May 28, 2024");
            editTextTime.setText("7:30 PM");
            editTextGuests.setText("4");
            editTextTable.setText("Patio Seat #12");
            editTextRequests.setText("Celebrating an anniversary.");
        }
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
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String guestsStr = editTextGuests.getText().toString().trim();
        String table = editTextTable.getText().toString().trim();
        String requests = editTextRequests.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || guestsStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update reservation
        Reservation updatedReservation = new Reservation();
        updatedReservation.setId(reservationId);
        updatedReservation.setDate(date);
        updatedReservation.setTime(time);
        updatedReservation.setNumberOfGuests(Integer.parseInt(guestsStr));
        updatedReservation.setSpecialRequests(requests);
        updatedReservation.setStatus("Confirmed");

        int result = databaseHelper.updateReservation(updatedReservation);

        if (result > 0) {
            // Create notification
            String userEmail = sharedPreferences.getString("userEmail", "");
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
            finish();
        } else {
            Toast.makeText(this, "Failed to update reservation", Toast.LENGTH_SHORT).show();
        }
    }
}