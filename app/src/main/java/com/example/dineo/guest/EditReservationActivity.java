package com.example.dineo.guest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dineo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditReservationActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextInputEditText etDate, etTime, etGuests, etTable, etRequests;
    private MaterialButton btnSaveChanges, btnCancel;

    private String reservationId;
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedTime = Calendar.getInstance();
    private int selectedGuests = 4;
    private String selectedTable = "Any";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reservation);

        initializeViews();
        loadReservationData();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etGuests = findViewById(R.id.etGuests);
        etTable = findViewById(R.id.etTable);
        etRequests = findViewById(R.id.etRequests);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadReservationData() {
        Intent intent = getIntent();
        reservationId = intent.getStringExtra("reservation_id");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        selectedGuests = intent.getIntExtra("guests", 4);
        selectedTable = intent.getStringExtra("table");
        String requests = intent.getStringExtra("requests");

        etDate.setText(date);
        etTime.setText(time);
        etGuests.setText(String.valueOf(selectedGuests));
        etTable.setText(selectedTable);
        etRequests.setText(requests);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Time picker
        etTime.setOnClickListener(v -> showTimePicker());

        // Guests picker
        etGuests.setOnClickListener(v -> showGuestsPicker());

        // Table picker
        etTable.setOnClickListener(v -> showTablePicker());

        // Save button
        btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    etDate.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    etTime.setText(sdf.format(selectedTime.getTime()));
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void showGuestsPicker() {
        String[] guestsOptions = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Number of Guests");
        builder.setItems(guestsOptions, (dialog, which) -> {
            selectedGuests = which + 1;
            etGuests.setText(String.valueOf(selectedGuests));
        });
        builder.show();
    }

    private void showTablePicker() {
        String[] tableOptions = {"Any", "Window Seat", "Patio Seat #12", "Private Room", "Bar Counter"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Table Preference");
        builder.setItems(tableOptions, (dialog, which) -> {
            selectedTable = tableOptions[which];
            etTable.setText(selectedTable);
        });
        builder.show();
    }

    private void saveChanges() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String requests = etRequests.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Return updated data to previous activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("reservation_id", reservationId);
        resultIntent.putExtra("date", date);
        resultIntent.putExtra("time", time);
        resultIntent.putExtra("guests", selectedGuests);
        resultIntent.putExtra("table", selectedTable);
        resultIntent.putExtra("requests", requests);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}