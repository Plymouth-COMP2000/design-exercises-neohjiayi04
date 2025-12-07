package com.example.dineo;

import com.example.dineo.guest.MenuActivity;
import com.example.dineo.guest.ReservationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName, userEmail, userPhone;
    private ImageButton btnNotification;
    private RelativeLayout editProfileOption, passwordOption;
    private Switch switchConfirmation, switchModification, switchCancellation;
    private CardView logoutButton;
    private LinearLayout navMenu, navReservation, navProfile;
    private String userType; // "customer", "staff"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get user type from intent
        userType = getIntent().getStringExtra("USER_TYPE");
        if (userType == null) {
            userType = "customer"; // default
        }

        // Initialize views
        initializeViews();

        // Load user data
        loadUserData();

        // Set click listeners
        setupClickListeners();

        // Setup notification switches
        setupNotificationSwitches();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        btnNotification = findViewById(R.id.btnNotification);
        editProfileOption = findViewById(R.id.editProfileOption);
        passwordOption = findViewById(R.id.passwordOption);
        switchConfirmation = findViewById(R.id.switchConfirmation);
        switchModification = findViewById(R.id.switchModification);
        switchCancellation = findViewById(R.id.switchCancellation);
        logoutButton = findViewById(R.id.logoutButton);
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);
    }

    private void loadUserData() {
        // TODO: Load actual user data from database/preferences
        // For now, using placeholder data
        userName.setText("John Doe");
        userEmail.setText("john.doe@example.com");
        userPhone.setText("011-3338887");
    }

    private void setupClickListeners() {
        // Notification button
        btnNotification.setOnClickListener(v -> openNotificationPage());

        // Edit Profile
        editProfileOption.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            intent.putExtra("USER_TYPE", userType);
            startActivity(intent);
        });

        // Reset Password
        passwordOption.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ResetPasswordActivity.class);
            intent.putExtra("USER_TYPE", userType);
            startActivity(intent);
        });

        // Logout
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Bottom Navigation
        navMenu.setOnClickListener(v -> {
            // Navigate to Menu activity
            Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        navReservation.setOnClickListener(v -> {
            // Navigate to Reservation activity
            Intent intent = new Intent(ProfileActivity.this, ReservationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            // Already on profile page
        });
    }

    private void setupNotificationSwitches() {
        // Save notification preferences when switches are toggled
        switchConfirmation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save preference
            saveNotificationPreference("confirmation", isChecked);
        });

        switchModification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save preference
            saveNotificationPreference("modification", isChecked);
        });

        switchCancellation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save preference
            saveNotificationPreference("cancellation", isChecked);
        });
    }

    public void openNotificationPage() {
        // Navigate to notification activity
        Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    private void saveNotificationPreference(String type, boolean enabled) {
        // TODO: Save to SharedPreferences or database
        SharedPreferences prefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean(type, enabled).apply();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        // TODO: Clear user session, tokens, etc.
        // Navigate back to launch/login screen
        Intent intent = new Intent(ProfileActivity.this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}