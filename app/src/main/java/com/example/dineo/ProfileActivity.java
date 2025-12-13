package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.dineo.api.ApiService;
import com.example.dineo.guest.GuestMenuActivity;
import com.example.dineo.guest.ReservationActivity;
import com.example.dineo.utils.SessionManager;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName, userEmail, userPhone;
    private ImageButton btnNotification;
    private RelativeLayout editProfileOption, passwordOption;
    private Switch switchConfirmation, switchModification, switchCancellation;
    private CardView logoutButton;
    private LinearLayout navMenu, navReservation, navProfile;

    private SessionManager sessionManager;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        userType = sessionManager.getUserType();

        initializeViews();
        loadUserData();
        setupClickListeners();
        setupNotificationSwitches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            loadUserDataFromSession();
        }
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
        loadUserDataFromSession();

        String username = sessionManager.getUsername();
        if (username != null) {
            ApiService.getUserProfile(this, username, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONObject user = response.getJSONObject("user");
                        String firstname = user.getString("firstname");
                        String lastname = user.getString("lastname");
                        String email = user.getString("email");
                        String contact = user.getString("contact");

                        sessionManager.updateUserProfile(firstname, lastname, email, contact);

                        userName.setText(firstname + " " + lastname);
                        userEmail.setText(email);
                        userPhone.setText(contact);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    // Keep using session data if API fails
                }
            });
        }
    }

    private void loadUserDataFromSession() {
        userName.setText(sessionManager.getFullName());
        userEmail.setText(sessionManager.getEmail());
        userPhone.setText(sessionManager.getContact());

        switchConfirmation.setChecked(sessionManager.getNotificationPreference("confirmation"));
        switchModification.setChecked(sessionManager.getNotificationPreference("modification"));
        switchCancellation.setChecked(sessionManager.getNotificationPreference("cancellation"));
    }

    private void setupClickListeners() {
        btnNotification.setOnClickListener(v -> openNotificationPage());

        editProfileOption.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            startActivity(intent);
        });

        passwordOption.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileResetActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> showLogoutDialog());

        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, GuestMenuActivity.class);
            startActivity(intent);
        });

        navReservation.setOnClickListener(v -> {
            if ("staff".equals(userType) || "admin".equals(userType)) {
                Toast.makeText(this, "Staff reservation management coming soon", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ProfileActivity.this, ReservationActivity.class);
                startActivity(intent);
            }
        });

        navProfile.setOnClickListener(v -> {
            // Already on profile page
        });
    }

    private void setupNotificationSwitches() {
        switchConfirmation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setNotificationPreference("confirmation", isChecked);
            Toast.makeText(this,
                    "Confirmation notifications " + (isChecked ? "enabled" : "disabled"),
                    Toast.LENGTH_SHORT).show();
        });

        switchModification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setNotificationPreference("modification", isChecked);
            Toast.makeText(this,
                    "Modification notifications " + (isChecked ? "enabled" : "disabled"),
                    Toast.LENGTH_SHORT).show();
        });

        switchCancellation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setNotificationPreference("cancellation", isChecked);
            Toast.makeText(this,
                    "Cancellation notifications " + (isChecked ? "enabled" : "disabled"),
                    Toast.LENGTH_SHORT).show();
        });
    }

    public void openNotificationPage() {
        Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
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
        sessionManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}