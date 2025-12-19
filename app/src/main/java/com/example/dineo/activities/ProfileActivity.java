package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Profile Activity - Works for both GUEST and STAFF
 * Student ID: BSSE2506008
 */
public class ProfileActivity extends AppCompatActivity {

    private TextView textViewUserName, textViewUserEmail, textViewUserPhone;
    private LinearLayout layoutEditProfile, layoutResetPassword, layoutLogout;
    private LinearLayout layoutNotificationSettings; // Notification preferences section
    private Switch switchReservationBooking, switchReservationModification, switchReservationCancellation;
    private Switch switchNewReservationAlert; // For staff only
    private BottomNavigationView bottomNavigationView;

    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        textViewUserPhone = findViewById(R.id.textViewUserPhone);
        layoutEditProfile = findViewById(R.id.layoutEditProfile);
        layoutResetPassword = findViewById(R.id.layoutResetPassword);
        layoutLogout = findViewById(R.id.layoutLogout);
        layoutNotificationSettings = findViewById(R.id.layoutNotificationSettings);

        // Notification switches
        switchReservationBooking = findViewById(R.id.switchReservationBooking);
        switchReservationModification = findViewById(R.id.switchReservationModification);
        switchReservationCancellation = findViewById(R.id.switchReservationCancellation);
        switchNewReservationAlert = findViewById(R.id.switchNewReservationAlert);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(this);
        userRole = sharedPreferences.getString("userRole", "GUEST");

        // Load user data
        loadUserData();

        // Load notification preferences
        loadNotificationPreferences();

        // Setup notification switches
        setupNotificationSwitches();

        // Setup click listeners
        layoutEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        layoutResetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        layoutLogout.setOnClickListener(v -> confirmLogout());

        // Setup bottom navigation based on role
        setupBottomNavigation();
    }

    private void loadUserData() {
        String userName = sharedPreferences.getString("userName", "User");
        String userEmail = sharedPreferences.getString("userEmail", "user@email.com");
        String userPhone = sharedPreferences.getString("userPhone", "");

        textViewUserName.setText(userName);
        textViewUserEmail.setText(userEmail);

        if (!userPhone.isEmpty()) {
            textViewUserPhone.setText(userPhone);
        } else {
            textViewUserPhone.setText("No phone number");
        }
    }

    private void loadNotificationPreferences() {
        // Load from SharedPreferences
        boolean reservationBooking = sharedPreferences.getBoolean("notif_reservation_booking", true);
        boolean reservationModification = sharedPreferences.getBoolean("notif_reservation_modification", true);
        boolean reservationCancellation = sharedPreferences.getBoolean("notif_reservation_cancellation", true);
        boolean newReservationAlert = sharedPreferences.getBoolean("notif_new_reservation_alert", true);

        switchReservationBooking.setChecked(reservationBooking);
        switchReservationModification.setChecked(reservationModification);
        switchReservationCancellation.setChecked(reservationCancellation);
        switchNewReservationAlert.setChecked(newReservationAlert);

        // Show/hide staff-specific notification
        if ("STAFF".equalsIgnoreCase(userRole)) {
            switchNewReservationAlert.setVisibility(View.VISIBLE);
            findViewById(R.id.textViewNewReservationAlert).setVisibility(View.VISIBLE);
            findViewById(R.id.textViewNewReservationAlertDesc).setVisibility(View.VISIBLE);
        } else {
            switchNewReservationAlert.setVisibility(View.GONE);
            findViewById(R.id.textViewNewReservationAlert).setVisibility(View.GONE);
            findViewById(R.id.textViewNewReservationAlertDesc).setVisibility(View.GONE);
        }
    }

    private void setupNotificationSwitches() {
        // Reservation Booking
        switchReservationBooking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveNotificationPreference("notif_reservation_booking", isChecked);
            }
        });

        // Reservation Modification
        switchReservationModification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveNotificationPreference("notif_reservation_modification", isChecked);
            }
        });

        // Reservation Cancellation
        switchReservationCancellation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveNotificationPreference("notif_reservation_cancellation", isChecked);
            }
        });

        // New Reservation Alert (Staff only)
        switchNewReservationAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveNotificationPreference("notif_new_reservation_alert", isChecked);
            }
        });
    }

    private void saveNotificationPreference(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

        Toast.makeText(this, "Notification preference updated", Toast.LENGTH_SHORT).show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        // Clear all saved data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Go to login
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                return true;
            }

            // Role-based navigation
            if ("STAFF".equalsIgnoreCase(userRole)) {
                // Staff navigation
                if (itemId == R.id.nav_staff_dashboard) {
                    startActivity(new Intent(ProfileActivity.this, StaffDashboardActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_menu) {
                    startActivity(new Intent(ProfileActivity.this, StaffMenuActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_reservation) {
                    startActivity(new Intent(ProfileActivity.this, StaffReservationActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
            } else {
                // Guest navigation
                if (itemId == R.id.nav_menu) {
                    startActivity(new Intent(ProfileActivity.this, MenuActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_reservation) {
                    startActivity(new Intent(ProfileActivity.this, ReservationActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }
}