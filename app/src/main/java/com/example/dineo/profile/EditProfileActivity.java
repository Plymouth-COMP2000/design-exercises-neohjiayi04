package com.example.dineo.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivProfilePicture;
    private MaterialCardView cardCamera;
    private TextInputEditText etFullName, etEmail, etPhone;
    private MaterialButton btnSave;
    private TextView tvCancel;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeViews();
        loadUserData();
        setupListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        cardCamera = findViewById(R.id.card_camera);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        btnSave = findViewById(R.id.btn_save);
        tvCancel = findViewById(R.id.tv_cancel);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
    }

    private void loadUserData() {
        // Get user data from SharedPreferences
        String userEmail = sharedPreferences.getString("user_email", "");
        String userPassword = sharedPreferences.getString("user_password", "");

        if (!userEmail.isEmpty() && !userPassword.isEmpty()) {
            currentUser = databaseHelper.loginUser(userEmail, userPassword);

            if (currentUser != null) {
                etFullName.setText(currentUser.getUsername());
                etEmail.setText(currentUser.getEmail());

                // Load phone from database
                String phone = databaseHelper.getUserPhone(currentUser.getId());
                if (phone != null && !phone.isEmpty()) {
                    etPhone.setText(phone);
                }
            }
        }
    }

    private void setupListeners() {
        // Back Button
        ivBack.setOnClickListener(v -> finish());

        // Camera Button (placeholder - image upload not implemented)
        cardCamera.setOnClickListener(v -> {
            Toast.makeText(this, "Profile picture upload coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Save Button
        btnSave.setOnClickListener(v -> saveChanges());

        // Cancel Button
        tvCancel.setOnClickListener(v -> finish());

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    private void saveChanges() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validation
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        // Check if email is already taken by another user
        if (!email.equals(currentUser.getEmail()) && databaseHelper.isEmailExists(email)) {
            etEmail.setError("Email already exists");
            etEmail.requestFocus();
            return;
        }

        // Update user in database
        int result = databaseHelper.updateUser(currentUser.getId(), fullName, email, phone);

        if (result > 0) {
            // Update SharedPreferences with new email if changed
            if (!email.equals(currentUser.getEmail())) {
                sharedPreferences.edit().putString("user_email", email).apply();
            }

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}