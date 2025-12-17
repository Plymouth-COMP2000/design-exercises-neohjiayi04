package com.example.dineo.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.auth.LoginActivity;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.menu.GuestMenuActivity;
import com.example.dineo.models.User;
import com.example.dineo.reservation.GuestReservationActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private TextView tvUsername, tvEmail, tvPhone;
    private MaterialCardView cardEditProfile, cardResetPassword;
    private SwitchMaterial switchConfirmation, switchModification, switchCancellation;
    private MaterialButton btnLogout;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        loadUserData();
        loadNotificationPreferences();
        setupListeners();
    }

    private void initializeViews() {
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        cardEditProfile = findViewById(R.id.card_edit_profile);
        cardResetPassword = findViewById(R.id.card_reset_password);
        switchConfirmation = findViewById(R.id.switch_confirmation);
        switchModification = findViewById(R.id.switch_modification);
        switchCancellation = findViewById(R.id.switch_cancellation);
        btnLogout = findViewById(R.id.btn_logout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
    }

    private void loadUserData() {
        // Get user email from SharedPreferences
        String userEmail = sharedPreferences.getString("user_email", "");
        String userPassword = sharedPreferences.getString("user_password", "");

        if (!userEmail.isEmpty() && !userPassword.isEmpty()) {
            currentUser = databaseHelper.loginUser(userEmail, userPassword);

            if (currentUser != null) {
                tvUsername.setText(currentUser.getUsername());
                tvEmail.setText(currentUser.getEmail());

                // Load phone from database if available
                String phone = databaseHelper.getUserPhone(currentUser.getId());
                if (phone != null && !phone.isEmpty()) {
                    tvPhone.setText(phone);
                } else {
                    tvPhone.setText("No phone number");
                }
            }
        } else {
            // No user logged in, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void loadNotificationPreferences() {
        switchConfirmation.setChecked(sharedPreferences.getBoolean("notif_confirmation", true));
        switchModification.setChecked(sharedPreferences.getBoolean("notif_modification", true));
        switchCancellation.setChecked(sharedPreferences.getBoolean("notif_cancellation", true));
    }

    private void setupListeners() {
        // Edit Profile
        cardEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Reset Password
        cardResetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // Notification Switches
        switchConfirmation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notif_confirmation", isChecked).apply();
            Toast.makeText(this, "Confirmation notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        switchModification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notif_modification", isChecked).apply();
            Toast.makeText(this, "Modification notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        switchCancellation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notif_cancellation", isChecked).apply();
            Toast.makeText(this, "Cancellation notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        // Logout Button
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_menu) {
                startActivity(new Intent(this, GuestMenuActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_reservation) {
                startActivity(new Intent(this, GuestReservationActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear SharedPreferences
                    sharedPreferences.edit().clear().apply();

                    // Redirect to Login
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // Refresh user data when returning from edit profile
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}