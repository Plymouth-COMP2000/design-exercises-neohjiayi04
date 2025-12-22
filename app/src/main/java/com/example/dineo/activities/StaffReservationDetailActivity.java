package com.example.dineo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Notification;
import com.example.dineo.models.Reservation;

/**
 * Staff Reservation Detail Activity
 * Shows full details of a reservation for staff members
 * Allows staff to modify status or cancel
 * NOW WITH STAFF NAVIGATION! ✅
 */
public class StaffReservationDetailActivity extends StaffBaseActivity {

    private DatabaseHelper dbHelper;
    private Reservation reservation;
    private int reservationId;

    private TextView textViewGuestName, textViewGuestEmail, textViewGuestPhone;
    private TextView textViewDate, textViewTime, textViewGuests, textViewTable, textViewStatus;
    private TextView textViewSpecialRequests;
    private Button btnConfirm, btnCancel;
    private ImageView imageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_reservation_detail);

        // ✅ FIXED: Added staff bottom navigation
        setupStaffBottomNavigation(R.id.nav_staff_reservations);

        dbHelper = new DatabaseHelper(this);

        // Get reservation ID from intent
        reservationId = getIntent().getIntExtra("reservation_id", -1);
        if (reservationId == -1) {
            Toast.makeText(this, "Error loading reservation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        textViewGuestName = findViewById(R.id.textViewGuestName);
        textViewGuestEmail = findViewById(R.id.textViewEmail);
        textViewGuestPhone = findViewById(R.id.textViewPhone);
        textViewDate = findViewById(R.id.textViewReservationDate);
        textViewTime = findViewById(R.id.textViewReservationTime);
        textViewGuests = findViewById(R.id.textViewGuests);
        textViewTable = findViewById(R.id.textViewTable);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewSpecialRequests = findViewById(R.id.textViewSpecialRequests);
        btnConfirm = findViewById(R.id.buttonConfirm);
        btnCancel = findViewById(R.id.btnCancel);

        // Load reservation data
        loadReservationDetails();

        // Back button
        imageViewBack.setOnClickListener(v -> finish());

        // Confirm button
        btnConfirm.setOnClickListener(v -> confirmReservation());

        // Cancel button
        btnCancel.setOnClickListener(v -> showCancelDialog());
    }

    private void loadReservationDetails() {
        reservation = dbHelper.getReservationById(reservationId);

        if (reservation == null) {
            Toast.makeText(this, "Reservation not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate guest info
        textViewGuestName.setText(reservation.customerName());
        textViewGuestEmail.setText(reservation.getUserEmail());
        textViewGuestPhone.setText(reservation.numberOfGuests());

        // Populate reservation info
        textViewDate.setText(reservation.getDate());
        textViewTime.setText(reservation.getTime());
        textViewGuests.setText(reservation.getGuestsFormatted());
        textViewTable.setText(reservation.getTableFormatted());
        textViewStatus.setText(reservation.getStatus());

        // Special requests
        String specialRequests = reservation.getSpecialRequests();
        if (specialRequests != null && !specialRequests.isEmpty()) {
            textViewSpecialRequests.setText(specialRequests);
        } else {
            textViewSpecialRequests.setText("None");
        }

        // Update button visibility based on status
        String status = reservation.getStatus();
        if ("Cancelled".equals(status)) {
            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
        } else if ("Confirmed".equals(status)) {
            btnConfirm.setEnabled(false);
        }

        // Set status color
        if ("Confirmed".equals(status)) {
            textViewStatus.setBackgroundResource(R.drawable.bg_status_confirmed);
        } else if ("Cancelled".equals(status)) {
            textViewStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
        } else {
            textViewStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }
    }

    private void confirmReservation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Reservation")
                .setMessage("Are you sure you want to confirm this reservation?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // Update status in database
                    int result = dbHelper.updateReservationStatus(reservationId, Reservation.STATUS_CONFIRMED);

                    if (result > 0) {
                        // Create notification for guest
                        Notification notification = new Notification(
                                0,
                                reservation.getUserEmail(),
                                "Reservation Confirmed",
                                "Your reservation for " + reservation.getDate() + " at " + reservation.getTime() + " has been confirmed!",
                                System.currentTimeMillis(),
                                false,
                                "reservation_confirmed"
                        );
                        dbHelper.addNotification(notification);

                        Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();
                        loadReservationDetails(); // Refresh
                    } else {
                        Toast.makeText(this, "Error confirming reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation? This action cannot be undone.")
                .setPositiveButton("Cancel Reservation", (dialog, which) -> cancelReservation())
                .setNegativeButton("Go Back", null)
                .show();
    }

    private void cancelReservation() {
        // Update status in database
        int result = dbHelper.updateReservationStatus(reservationId, Reservation.STATUS_CANCELLED);

        if (result > 0) {
            // Create notification for guest
            Notification notification = new Notification(
                    0,
                    reservation.getUserEmail(),
                    "Reservation Cancelled",
                    "Your reservation for " + reservation.getDate() + " at " + reservation.getTime() + " has been cancelled by staff.",
                    System.currentTimeMillis(),
                    false,
                    "reservation_cancelled"
            );
            dbHelper.addNotification(notification);

            Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error cancelling reservation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservationDetails();
    }
}