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
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnUpdate;
    private TextView tvCancel;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initializeViews();
        loadUserData();
        setupListeners();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnUpdate = findViewById(R.id.btn_update);
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
        }
    }

    private void setupListeners() {
        // Back Button
        ivBack.setOnClickListener(v -> finish());

        // Update Button
        btnUpdate.setOnClickListener(v -> updatePassword());

        // Cancel Button
        tvCancel.setOnClickListener(v -> finish());

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    private void updatePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (currentPassword.isEmpty()) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Verify current password
        String userEmail = sharedPreferences.getString("user_email", "");
        User verifyUser = databaseHelper.loginUser(userEmail, currentPassword);

        if (verifyUser == null) {
            etCurrentPassword.setError("Current password is incorrect");
            etCurrentPassword.requestFocus();
            return;
        }

        // Update password in database
        int result = databaseHelper.updateUserPassword(currentUser.getId(), newPassword);

        if (result > 0) {
            // Update SharedPreferences with new password
            sharedPreferences.edit().putString("user_password", newPassword).apply();

            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();

            // Clear input fields
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");

            finish();
        } else {
            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
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