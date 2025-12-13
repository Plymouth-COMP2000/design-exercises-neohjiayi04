package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.dineo.api.ApiService;
import com.example.dineo.guest.GuestMenuActivity;
import com.example.dineo.guest.ReservationActivity;
import com.example.dineo.utils.SessionManager;
import org.json.JSONObject;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageView profileImage, editProfileImage;
    private EditText editFullName, editEmail, editPhone;
    private CardView saveButton;
    private TextView cancelButton;
    private LinearLayout navMenu, navReservation, navProfile;

    private SessionManager sessionManager;
    private boolean isSaving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        sessionManager = new SessionManager(this);

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        profileImage = findViewById(R.id.profileImage);
        editProfileImage = findViewById(R.id.editProfileImage);
        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);
    }

    private void loadUserData() {
        editFullName.setText(sessionManager.getFullName());
        editEmail.setText(sessionManager.getEmail());
        editPhone.setText(sessionManager.getContact());
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        editProfileImage.setOnClickListener(v -> selectProfileImage());

        saveButton.setOnClickListener(v -> {
            if (!isSaving) {
                saveProfile();
            }
        });

        cancelButton.setOnClickListener(v -> finish());

        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, GuestMenuActivity.class);
            startActivity(intent);
        });

        navReservation.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, ReservationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> finish());
    }

    private void selectProfileImage() {
        Toast.makeText(this, "Profile image upload coming soon", Toast.LENGTH_SHORT).show();
    }

    private void saveProfile() {
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        // Split full name into first and last name
        String[] nameParts = fullName.split(" ", 2);
        String firstname = nameParts[0];
        String lastname = nameParts.length > 1 ? nameParts[1] : "";

        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            editFullName.setError("Full name is required");
            editFullName.requestFocus();
            return;
        }

        if (fullName.length() < 3) {
            editFullName.setError("Name must be at least 3 characters");
            editFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email");
            editEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Phone number is required");
            editPhone.requestFocus();
            return;
        }

        if (phone.length() < 8) {
            editPhone.setError("Please enter a valid phone number");
            editPhone.requestFocus();
            return;
        }

        // Disable button and show loading
        isSaving = true;
        saveButton.setEnabled(false);
        Toast.makeText(this, "Updating profile...", Toast.LENGTH_SHORT).show();

        // Get user data from session
        String username = sessionManager.getUsername();
        String password = sessionManager.getPassword();
        String usertype = sessionManager.getUserType();

        // Call API to update profile
        ApiService.updateUserProfile(this, username, firstname, lastname, email, phone,
                password, usertype, new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        isSaving = false;
                        saveButton.setEnabled(true);

                        // Update session with new data
                        sessionManager.updateUserProfile(firstname, lastname, email, phone);

                        Toast.makeText(ProfileEditActivity.this,
                                "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        // Go back to profile page
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        isSaving = false;
                        saveButton.setEnabled(true);

                        Toast.makeText(ProfileEditActivity.this,
                                "Failed to update profile: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}