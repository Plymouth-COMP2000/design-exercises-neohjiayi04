package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dineo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

/**
 * Profile Activity - Shows user profile and settings
 * Supports both GUEST and STAFF users with dynamic switches
 */
public class ProfileActivity extends BaseActivity {

    private SharedPreferences prefs;
    private ImageView imageProfile, imageViewBack, imageViewNotification;
    private TextView textViewName, textViewEmail, textViewPhone;
    private LinearLayout layoutEditProfile, layoutChangePassword;

    // Guest switches
    private Switch switchGuestConfirmation, switchGuestCancellation, switchGuestReminder;

    // Staff switches
    private Switch switchStaffNewBooking, switchStaffModification, switchStaffCancellation;

    private MaterialCardView btnLogout;

    private boolean isStaff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize SharedPreferences
        prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Determine user type
        checkUserType();

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (isStaff) {
            setupStaffBottomNavigation(R.id.nav_staff_profile);
        } else {
            setupBottomNavigation(R.id.nav_profile);
        }

        // Setup notification icon
        setupNotificationIcon();

        // Bind views
        bindViews();

        // Show only relevant switches based on user type
        configureSwitchVisibility();

        // Load user info and profile picture
        loadUserData();
        loadProfilePicture();

        // Load switch preferences
        loadNotificationPreferences();

        // Back button
        imageViewBack.setOnClickListener(v -> finish());

        // Edit profile
        layoutEditProfile.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        // Change password
        layoutChangePassword.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));

        // Switch toggles
        setupSwitchListeners();

        // Logout
        btnLogout.setOnClickListener(v -> logout());
    }

    /*** Setup staff bottom navigation*/
    private void setupStaffBottomNavigation(int activeItemId) {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                findViewById(R.id.bottomNavigationView);

        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(activeItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == activeItemId) return true;

            Intent intent = null;
            if (item.getItemId() == R.id.nav_staff_dashboard) {
                intent = new Intent(this, StaffDashboardActivity.class);
            } else if (item.getItemId() == R.id.nav_menu_staff) {
                intent = new Intent(this, StaffMenuActivity.class);
            } else if (item.getItemId() == R.id.nav_staff_reservations) {
                intent = new Intent(this, StaffReservationActivity.class);
            } else if (item.getItemId() == R.id.nav_staff_profile) {
                return true;
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });
    }


    private void bindViews() {
        imageProfile = findViewById(R.id.imageProfile);
        imageViewBack = findViewById(R.id.imageViewBack);
        textViewName = findViewById(R.id.textViewUserName);
        textViewEmail = findViewById(R.id.textViewUserEmail);
        textViewPhone = findViewById(R.id.textViewUserPhone);
        layoutEditProfile = findViewById(R.id.layoutEditProfile);
        layoutChangePassword = findViewById(R.id.layoutResetPassword);

        // Guest switches
        switchGuestConfirmation = findViewById(R.id.switchGuestConfirmation);
        switchGuestCancellation = findViewById(R.id.switchGuestCancellation);
        switchGuestReminder = findViewById(R.id.switchGuestReminder);

        // Staff switches
        switchStaffNewBooking = findViewById(R.id.switchStaffNewBooking);
        switchStaffModification = findViewById(R.id.switchStaffModification);
        switchStaffCancellation = findViewById(R.id.switchStaffCancellation);

        btnLogout = findViewById(R.id.layoutLogout);
    }

    private void configureSwitchVisibility() {
        if (isStaff) {
            // Staff sees only staff switches
            switchGuestConfirmation.setVisibility(Switch.GONE);
            switchGuestCancellation.setVisibility(Switch.GONE);
            switchGuestReminder.setVisibility(Switch.GONE);
        } else {
            // Guest sees only guest switches
            switchStaffNewBooking.setVisibility(Switch.GONE);
            switchStaffModification.setVisibility(Switch.GONE);
            switchStaffCancellation.setVisibility(Switch.GONE);
        }
    }

    private void setupSwitchListeners() {
        if (isStaff) {
            switchStaffNewBooking.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("notifyStaffNewBooking", isChecked).apply());

            switchStaffModification.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("notifyStaffModification", isChecked).apply());

            switchStaffCancellation.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("notifyStaffCancellation", isChecked).apply());
        } else {
            switchGuestConfirmation.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("notifyGuestConfirmation", isChecked).apply());

            switchGuestCancellation.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("notifyGuestCancellation", isChecked).apply());

            switchGuestReminder.setOnCheckedChangeListener((buttonView, isChecked) ->
                    prefs.edit().putBoolean("notifyGuestReminder", isChecked).apply());
        }
    }

    private void loadNotificationPreferences() {
        if (isStaff) {
            switchStaffNewBooking.setChecked(prefs.getBoolean("notifyStaffNewBooking", true));
            switchStaffModification.setChecked(prefs.getBoolean("notifyStaffModification", true));
            switchStaffCancellation.setChecked(prefs.getBoolean("notifyStaffCancellation", true));
        } else {
            switchGuestConfirmation.setChecked(prefs.getBoolean("notifyGuestConfirmation", true));
            switchGuestCancellation.setChecked(prefs.getBoolean("notifyGuestCancellation", true));
            switchGuestReminder.setChecked(prefs.getBoolean("notifyGuestReminder", true));
        }
    }

    private void setupNotificationIcon() {
        imageViewNotification = findViewById(R.id.imageViewNotification);
        if (imageViewNotification != null) {
            imageViewNotification.setOnClickListener(v -> {
                Intent intent;
                if (isStaff) {
                    intent = new Intent(this, StaffNotificationActivity.class);
                } else {
                    intent = new Intent(this, NotificationActivity.class);
                }
                startActivity(intent);
            });
        }
    }

    private void checkUserType() {
        try {
            String userJsonStr = prefs.getString("user_json", "");
            if (!userJsonStr.isEmpty()) {
                org.json.JSONObject json = new org.json.JSONObject(userJsonStr);
                String usertype = json.optString("usertype", "GUEST");
                isStaff = "STAFF".equalsIgnoreCase(usertype);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isStaff = false;
        }
    }

    private void loadUserData() {
        try {
            String userJsonStr = prefs.getString("user_json", "");
            if (!userJsonStr.isEmpty()) {
                org.json.JSONObject json = new org.json.JSONObject(userJsonStr);
                String fullName = (json.optString("firstname", "") + " " + json.optString("lastname", "")).trim();
                if (fullName.isEmpty()) fullName = json.optString("username", "User");

                textViewName.setText(fullName);
                textViewEmail.setText(json.optString("email", "Not available"));
                textViewPhone.setText(json.optString("contact", "Not set"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            textViewName.setText("User");
            textViewEmail.setText("Not available");
            textViewPhone.setText("Not set");
        }
    }

    private void loadProfilePicture() {
        String profileImageBase64 = prefs.getString("profile_image", "");
        if (!profileImageBase64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void logout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    prefs.edit().clear().apply();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        loadProfilePicture();
        loadNotificationPreferences();
    }
}
