package com.example.dineo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.button.MaterialButton;

public class StaffReservationDetailActivity extends StaffBaseActivity {

    private ImageView imageViewBack, imageViewNotification;
    private TextView textViewGuestName, textViewReservationDate, textViewReservationTime,
            textViewTableInfo, textViewGuestCount, textViewSpecialRequests,
            textViewEmail, textViewPhone;
    private MaterialButton buttonConfirm, buttonMarkSeated, buttonCancel;

    private DatabaseHelper databaseHelper;
    private Reservation reservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_reservation_detail);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();

        // Setup bottom navigation with correct ID
        setupStaffBottomNavigation(R.id.nav_staff_reservations);

        setupClickListeners();
        loadReservationData();
    }

    private void initializeViews() {
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewNotification = findViewById(R.id.imageViewNotification);

        textViewGuestName = findViewById(R.id.textViewGuestName);
        textViewReservationDate = findViewById(R.id.textViewReservationDate);
        textViewReservationTime = findViewById(R.id.textViewReservationTime);
        textViewTableInfo = findViewById(R.id.textViewTableInfo);
        textViewGuestCount = findViewById(R.id.textViewGuestCount);
        textViewSpecialRequests = findViewById(R.id.textViewSpecialRequests);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPhone = findViewById(R.id.textViewPhone);

        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonMarkSeated = findViewById(R.id.buttonMarkSeated);
        buttonCancel = findViewById(R.id.buttonCancel);
    }

    private void setupClickListeners() {
        imageViewBack.setOnClickListener(v -> finish());

        imageViewNotification.setOnClickListener(v ->
                startActivity(new Intent(this, StaffNotificationActivity.class))
        );

        buttonConfirm.setOnClickListener(v -> updateReservationStatus("Confirmed"));
        buttonMarkSeated.setOnClickListener(v -> updateReservationStatus("Seated"));
        buttonCancel.setOnClickListener(v -> confirmCancellation());
    }

    private void loadReservationData() {
        int reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);
        if (reservationId == -1) {
            Toast.makeText(this, "Reservation not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reservation = databaseHelper.getReservationById(reservationId);
        if (reservation == null) {
            Toast.makeText(this, "Reservation not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textViewGuestName.setText(reservation.getCustomerName());
        textViewReservationDate.setText(reservation.getDate());
        textViewReservationTime.setText(reservation.getTime());
        textViewTableInfo.setText(reservation.getTableFormatted());
        textViewGuestCount.setText(reservation.getGuestsFormatted());
        textViewSpecialRequests.setText(reservation.getSpecialRequests());
        textViewEmail.setText(reservation.getUserEmail());
        // Phone number - adjust if you add this field to your DB
        textViewPhone.setText("N/A");

        updateStatusButtons();
    }

    private void updateStatusButtons() {
        String status = reservation.getStatus();
        switch (status) {
            case "Pending":
                buttonConfirm.setEnabled(true);
                buttonMarkSeated.setEnabled(false);
                buttonCancel.setEnabled(true);
                break;
            case "Confirmed":
                buttonConfirm.setEnabled(false);
                buttonMarkSeated.setEnabled(true);
                buttonCancel.setEnabled(true);
                break;
            case "Seated":
                buttonConfirm.setEnabled(false);
                buttonMarkSeated.setEnabled(false);
                buttonCancel.setEnabled(true);
                break;
            case "Cancelled":
                buttonConfirm.setEnabled(false);
                buttonMarkSeated.setEnabled(false);
                buttonCancel.setEnabled(false);
                break;
        }
    }

    private void updateReservationStatus(String newStatus) {
        reservation.setStatus(newStatus);
        int result = databaseHelper.updateReservation(reservation);
        if (result > 0) {
            Toast.makeText(this, "Reservation status updated to " + newStatus, Toast.LENGTH_SHORT).show();
            updateStatusButtons();

            // Create notification for guest
            String title = "Reservation " + newStatus;
            String message = "Your reservation on " + reservation.getDate() + " at " + reservation.getTime() +
                    " has been " + newStatus.toLowerCase() + ".";
            databaseHelper.addNotification(title, message, databaseHelper.getCurrentTimestamp(),
                    "reservation_status", reservation.getUserEmail());
        } else {
            Toast.makeText(this, "Failed to update reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmCancellation() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> updateReservationStatus("Cancelled"))
                .setNegativeButton("No", null)
                .show();
    }
}