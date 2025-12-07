package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.dineo.guest.GuestMenuActivity;
import com.example.dineo.guest.ReservationActivity;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageView profileImage, editProfileImage;
    private EditText editFullName, editEmail, editPhone;
    private CardView saveButton;
    private TextView cancelButton;
    private LinearLayout navMenu, navReservation, navProfile;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Get user type from intent
        userType = getIntent().getStringExtra("USER_TYPE");

        // Initialize views
        initializeViews();

        // Load current user data
        loadUserData();

        // Set click listeners
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
        // TODO: Load actual user data from database/preferences
        // For now, using placeholder data
        editFullName.setText("John Doe");
        editEmail.setText("john.doe@example.com");
        editPhone.setText("011-3338887");
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Edit profile image
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileImage();
            }
        });

        // Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        // Cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Bottom Navigation
        navMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Menu activity
                Intent intent = new Intent(ProfileEditActivity.this, GuestMenuActivity.class);
                startActivity(intent);
            }
        });

        navReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Reservation activity
                Intent intent = new Intent(ProfileEditActivity.this, ReservationActivity.class);
                startActivity(intent);
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Profile activity
                finish();
            }
        });
    }

    private void selectProfileImage() {
        // TODO: Implement image picker
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // startActivityForResult(intent, REQUEST_IMAGE_PICK);
        Toast.makeText(this, "Profile image selection coming soon", Toast.LENGTH_SHORT).show();
    }

    private void saveProfile() {
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

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

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            editPhone.setError("Please enter a valid phone number");
            editPhone.requestFocus();
            return;
        }

        // TODO: Save data to database/server
        // For now, just show success message
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

        // Go back to profile page
        finish();
    }


}