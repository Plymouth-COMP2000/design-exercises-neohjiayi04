package com.example.dineo.reservation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReservationDetailsActivity extends AppCompatActivity {

    private ImageView ivBack, ivCall, ivEmail;
    private TextView tvCustomerName, tvStatus, tvGuests, tvDateTime, tvTable, tvSpecialRequests, tvPhone, tvEmailAddress;
    private View statusIndicator;
    private MaterialButton btnConfirm, btnCancel;
    private MaterialCardView cardSpecialRequests;

    private DatabaseHelper databaseHelper;
    private Reservation reservation;
    private int reservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details);

        reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);
        if (reservationId == -1) {
            Toast.makeText(this, "Error loading reservation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadReservationDetails();
        setupListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        ivCall = findViewById(R.id.iv_call);
        ivEmail = findViewById(R.id.iv_email);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvStatus = findViewById(R.id.tv_status);
        tvGuests = findViewById(R.id.tv_guests);
        tvDateTime = findViewById(R.id.tv_date_time);
        tvTable = findViewById(R.id.tv_table);
        tvSpecialRequests = findViewById(R.id.tv_special_requests);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmailAddress = findViewById(R.id.tv_email);
        statusIndicator = findViewById(R.id.status_indicator);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        cardSpecialRequests = findViewById(R.id.card_special_requests);

        databaseHelper = new DatabaseHelper(this);
    }

    private void loadReservationDetails() {
        reservation = databaseHelper.getReservationById(reservationId);

        if (reservation == null) {
            Toast.makeText(this, "Reservation not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set customer name
        tvCustomerName.setText(reservation.getCustomerName());

        // Set status
        tvStatus.setText(reservation.getStatus());
        int statusColor = getStatusColor(reservation.getStatus());
        tvStatus.setTextColor(statusColor);
        statusIndicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(statusColor));

        // Set guests
        tvGuests.setText(reservation.getNumberOfPax() + " people");

        // Set date and time
        tvDateTime.setText(formatDateTime(reservation.getDate(), reservation.getTime()));

        // Set table
        tvTable.setText(reservation.getTableNumber() != null ? reservation.getTableNumber() : "Not assigned");

        // Set special requests
        if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) {
            tvSpecialRequests.setText(reservation.getSpecialRequests());
            cardSpecialRequests.setVisibility(View.VISIBLE);
        } else {
            cardSpecialRequests.setVisibility(View.GONE);
        }

        // Set contact info
        tvPhone.setText(reservation.getPhoneNumber() != null ? reservation.getPhoneNumber() : "N/A");
        tvEmailAddress.setText(reservation.getEmail() != null ? reservation.getEmail() : "N/A");

        // Update button states based on status
        updateButtonStates();
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivCall.setOnClickListener(v -> {
            if (reservation.getPhoneNumber() != null && !reservation.getPhoneNumber().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + reservation.getPhoneNumber()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show();
            }
        });

        ivEmail.setOnClickListener(v -> {
            if (reservation.getEmail() != null && !reservation.getEmail().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + reservation.getEmail()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No email available", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(v -> confirmReservation());
        btnCancel.setOnClickListener(v -> showCancelDialog());
    }

    private void confirmReservation() {
        int result = databaseHelper.updateReservationStatus(reservationId, "Confirmed");
        if (result > 0) {
            Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();
            loadReservationDetails();
        } else {
            Toast.makeText(this, "Failed to confirm reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    int result = databaseHelper.updateReservationStatus(reservationId, "Cancelled");
                    if (result > 0) {
                        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void updateButtonStates() {
        String status = reservation.getStatus().toLowerCase();

        if (status.equals("confirmed") || status.equals("seated") || status.equals("completed")) {
            btnConfirm.setEnabled(false);
            btnConfirm.setAlpha(0.5f);
        }

        if (status.equals("cancelled") || status.equals("no-show") || status.equals("completed")) {
            btnCancel.setEnabled(false);
            btnCancel.setAlpha(0.5f);
        }
    }

    private int getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed":
            case "seated":
                return ContextCompat.getColor(this, R.color.success_green);
            case "upcoming":
                return ContextCompat.getColor(this, R.color.orange_accent);
            case "cancelled":
            case "no-show":
                return ContextCompat.getColor(this, R.color.error_red);
            default:
                return ContextCompat.getColor(this, R.color.text_secondary);
        }
    }

    private String formatDateTime(String date, String time) {
        // Convert "2024-10-05" to "Saturday, Oct 5, 2024"
        // And format time
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault());
            java.util.Date dateObj = inputFormat.parse(date);
            String formattedDate = outputFormat.format(dateObj);

            // Get formatted time from reservation object
            String formattedTime = reservation.getFormattedTime();

            return formattedDate + " at " + formattedTime;
        } catch (Exception e) {
            return date + " at " + time;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}