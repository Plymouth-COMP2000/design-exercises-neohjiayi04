package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText editCurrentPassword, editNewPassword, editConfirmPassword;
    private ImageView toggleCurrentPassword, toggleNewPassword, toggleConfirmPassword;
    private CardView updatePasswordButton;
    private TextView cancelButton;
    private LinearLayout navMenu, navReservation, navProfile;
    private String userType;

    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_reset);

        // Get user type from intent
        userType = getIntent().getStringExtra("USER_TYPE");

        // Initialize views
        initializeViews();

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        editCurrentPassword = findViewById(R.id.editCurrentPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        toggleCurrentPassword = findViewById(R.id.toggleCurrentPassword);
        toggleNewPassword = findViewById(R.id.toggleNewPassword);
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        cancelButton = findViewById(R.id.cancelButton);
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Password visibility toggles
        toggleCurrentPassword.setOnClickListener(v -> togglePasswordVisibility(
                editCurrentPassword, toggleCurrentPassword, 0));

        toggleNewPassword.setOnClickListener(v -> togglePasswordVisibility(
                editNewPassword, toggleNewPassword, 1));

        toggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(
                editConfirmPassword, toggleConfirmPassword, 2));

        // Update password button
        updatePasswordButton.setOnClickListener(v -> updatePassword());

        // Cancel button
        cancelButton.setOnClickListener(v -> finish());

        // Bottom Navigation
        navMenu.setOnClickListener(v -> {
            // Navigate to Menu activity
        });

        navReservation.setOnClickListener(v -> {
            // Navigate to Reservation activity
        });

        navProfile.setOnClickListener(v -> {
            // Navigate back to Profile activity
            finish();
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon, int fieldIndex) {
        boolean isVisible;

        switch (fieldIndex) {
            case 0:
                isCurrentPasswordVisible = !isCurrentPasswordVisible;
                isVisible = isCurrentPasswordVisible;
                break;
            case 1:
                isNewPasswordVisible = !isNewPasswordVisible;
                isVisible = isNewPasswordVisible;
                break;
            case 2:
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                isVisible = isConfirmPasswordVisible;
                break;
            default:
                return;
        }

        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_on);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_off);
        }

        // Move cursor to end
        editText.setSelection(editText.getText().length());
    }

    private void updatePassword() {
        String currentPassword = editCurrentPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(currentPassword)) {
            editCurrentPassword.setError("Current password is required");
            editCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            editNewPassword.setError("New password is required");
            editNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            editNewPassword.setError("Password must be at least 6 characters");
            editNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editConfirmPassword.setError("Please confirm your password");
            editConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editConfirmPassword.setError("Passwords do not match");
            editConfirmPassword.requestFocus();
            return;
        }

        if (currentPassword.equals(newPassword)) {
            editNewPassword.setError("New password must be different from current password");
            editNewPassword.requestFocus();
            return;
        }

        // TODO: Verify current password and update to new password
        // For now, just show success message
        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();

        // Go back to profile page
        finish();
    }
}