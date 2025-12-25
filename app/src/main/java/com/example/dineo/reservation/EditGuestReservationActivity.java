package com.example.dineo.reservation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditGuestReservationActivity extends AppCompatActivity {

    private ImageView ivBack, ivDecrease, ivIncrease;
    private TextView tvDate, tvTime, tvGuestsCount, tvTable, tvCancel;
    private EditText etSpecialRequests;
    private MaterialButton btnSave;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private Reservation reservation;
    private int reservationId;

    private Calendar selectedDate;
    private int selectedHour;
    private int selectedMinute;
    private int guestsCount;
    private String selectedTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_guest_reservation);

        reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);
        if (reservationId == -1) {
            Toast.makeText(this, "Error loading reservation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadReservationData();
        setupListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        ivDecrease = findViewById(R.id.iv_decrease);
        ivIncrease = findViewById(R.id.iv_increase);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvGuestsCount = findViewById(R.id.tv_guests_count);
        tvTable = findViewById(R.id.tv_table);
        tvCancel = findViewById(R.id.tv_cancel);
        etSpecialRequests = findViewById(R.id.et_special_requests);
        btnSave = findViewById(R.id.btn_save);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        databaseHelper = new DatabaseHelper(this);
        selectedDate = Calendar.getInstance();
    }

    private void loadReservationData() {
        reservation = databaseHelper.getReservationById(reservationId);

        if (reservation == null) {
            Toast.makeText(this, "Reservation not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Parse date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDate.setTime(sdf.parse(reservation.getDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parse time
        try {
            String[] timeParts = reservation.getTime().split(":");
            selectedHour = Integer.parseInt(timeParts[0]);
            selectedMinute = Integer.parseInt(timeParts[1]);
        } catch (Exception e) {
            selectedHour = 19;
            selectedMinute = 30;
        }

        guestsCount = reservation.getNumberOfPax();
        selectedTable = reservation.getTableNumber();

        // Update UI
        updateDateDisplay();
        updateTimeDisplay();
        tvGuestsCount.setText(String.valueOf(guestsCount));
        tvTable.setText(selectedTable);

        if (reservation.getSpecialRequests() != null) {
            etSpecialRequests.setText(reservation.getSpecialRequests());
        }
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        tvDate.setOnClickListener(v -> showDatePicker());
        tvTime.setOnClickListener(v -> showTimePicker());
        tvTable.setOnClickListener(v -> showTablePicker());

        ivDecrease.setOnClickListener(v -> {
            if (guestsCount > 1) {
                guestsCount--;
                tvGuestsCount.setText(String.valueOf(guestsCount));
            }
        });

        ivIncrease.setOnClickListener(v -> {
            if (guestsCount < 20) {
                guestsCount++;
                tvGuestsCount.setText(String.valueOf(guestsCount));
            }
        });

        btnSave.setOnClickListener(v -> saveChanges());
        tvCancel.setOnClickListener(v -> finish());

        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);
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

    private void saveChanges() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(selectedDate.getTime());
        String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
        String requests = etSpecialRequests.getText().toString().trim();

        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setNumberOfPax(guestsCount);
        reservation.setTableNumber(selectedTable);
        reservation.setSpecialRequests(requests.isEmpty() ? null : requests);

        int result = databaseHelper.updateReservation(reservation);

        if (result > 0) {
            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDateDisplay() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        tvDate.setText(displayFormat.format(selectedDate.getTime()));
    }

    private void updateTimeDisplay() {
        String period = selectedHour >= 12 ? "PM" : "AM";
        int displayHour = selectedHour > 12 ? selectedHour - 12 : (selectedHour == 0 ? 12 : selectedHour);
        tvTime.setText(String.format(Locale.getDefault(), "%d:%02d %s", displayHour, selectedMinute, period));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}