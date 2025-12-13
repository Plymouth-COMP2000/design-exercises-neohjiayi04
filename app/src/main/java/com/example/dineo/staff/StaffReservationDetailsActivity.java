package com.example.dineo.staff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dineo.R;
import com.google.android.material.button.MaterialButton;

public class StaffReservationDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvGuestName, tvStatus, tvGuests, tvDateTime, tvTable, tvSpecialRequests;
    private TextView tvPhone, tvEmail;
    private MaterialButton btnConfirm, btnCancel;

    private String reservationId;
    private String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_reservation_details);

        initializeViews();
        loadReservationData();
        setupClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvGuestName = findViewById(R.id.tvGuestName);
        tvStatus = findViewById(R.id.tvStatus);
        tvGuests = findViewById(R.id.tvGuests);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvTable = findViewById(R.id.tvTable);
        tvSpecialRequests = findViewById(R.id.tvSpecialRequests);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadReservationData() {
        Intent intent = getIntent();
        reservationId = intent.getStringExtra("reservation_id");
        String time = intent.getStringExtra("time");
        String guestName = intent.getStringExtra("guest_name");
        int guests = intent.getIntExtra("guests", 0);
        currentStatus = intent.getStringExtra("status");

        // Set basic data from intent
        tvGuestName.setText(guestName);
        tvStatus.setText(currentStatus);
        tvGuests.setText(guests + " guests");
        tvDateTime.setText("Oct 4, 2024 at " + time + " PM");

        // Sample data - in real app, fetch from API
        tvTable.setText("Table #12");
        tvSpecialRequests.setText("Window seat preferred, celebrating anniversary");
        tvPhone.setText("+1 (555) 123-4567");
        tvEmail.setText("janedoe@email.com");

        // Set status badge color
        updateStatusBadge();

        // Show/hide buttons based on status
        if ("Seated".equals(currentStatus) || "No-Show".equals(currentStatus)) {
            btnConfirm.setVisibility(android.view.View.GONE);
            btnCancel.setVisibility(android.view.View.GONE);
        }
    }

    private void updateStatusBadge() {
        int statusColor;
        int statusBgColor;

        switch (currentStatus) {
            case "Upcoming":
                statusColor = getResources().getColor(R.color.status_upcoming_text);
                statusBgColor = getResources().getColor(R.color.status_upcoming_bg);
                break;
            case "Seated":
                statusColor = getResources().getColor(R.color.status_seated_text);
                statusBgColor = getResources().getColor(R.color.status_seated_bg);
                break;
            case "No-Show":
                statusColor = getResources().getColor(R.color.status_noshow_text);
                statusBgColor = getResources().getColor(R.color.status_noshow_bg);
                break;
            default:
                statusColor = getResources().getColor(R.color.text_secondary);
                statusBgColor = getResources().getColor(R.color.background);
                break;
        }

        tvStatus.setTextColor(statusColor);
        android.graphics.drawable.GradientDrawable badge = new android.graphics.drawable.GradientDrawable();
        badge.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        badge.setCornerRadius(40f);
        badge.setColor(statusBgColor);
        tvStatus.setBackground(badge);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> showConfirmDialog());

        btnCancel.setOnClickListener(v -> showCancelDialog());
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Reservation")
                .setMessage("Are you sure you want to confirm this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // TODO: Update reservation status in database
                    currentStatus = "Seated";
                    tvStatus.setText("Seated");
                    updateStatusBadge();
                    btnConfirm.setVisibility(android.view.View.GONE);
                    btnCancel.setVisibility(android.view.View.GONE);
                    Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation? This action cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    // TODO: Cancel reservation in database
                    Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}