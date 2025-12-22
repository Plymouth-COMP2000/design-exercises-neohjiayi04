package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResetPasswordActivity extends AppCompatActivity {

    // UI Components
    private TextInputLayout layoutCurrentPassword, layoutNewPassword, layoutConfirmPassword;
    private TextInputEditText editCurrentPassword, editNewPassword, editConfirmPassword;
    private MaterialButton btnUpdatePassword;
    private ProgressBar progressBar;
    private ImageView imageViewBack;

    // Data
    private ExecutorService executor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        executor = Executors.newSingleThreadExecutor();

        // Find views
        imageViewBack = findViewById(R.id.imageViewBack);
        layoutCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        layoutNewPassword = findViewById(R.id.editTextNewPassword);
        layoutConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editNewPassword = findViewById(R.id.editTextNewPassword);
        editConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progressBar);

        // Back button
        imageViewBack.setOnClickListener(v -> finish());

        // Update password button
        btnUpdatePassword.setOnClickListener(v -> validateAndUpdatePassword());
    }

    /**
     * Validate inputs and update password
     */
    private void validateAndUpdatePassword() {
        // Clear previous errors
        layoutCurrentPassword.setError(null);
        layoutNewPassword.setError(null);
        layoutConfirmPassword.setError(null);

        // Get values
        String currentPassword = editCurrentPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // Validation
        if (currentPassword.isEmpty()) {
            layoutCurrentPassword.setError("Current password is required");
            editCurrentPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            layoutNewPassword.setError("New password is required");
            editNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            layoutNewPassword.setError("Password must be at least 6 characters");
            editNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            layoutConfirmPassword.setError("Please confirm your new password");
            editConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            layoutConfirmPassword.setError("Passwords do not match");
            editConfirmPassword.requestFocus();
            return;
        }

        if (currentPassword.equals(newPassword)) {
            layoutNewPassword.setError("New password must be different from current password");
            editNewPassword.requestFocus();
            return;
        }

        // All validation passed - proceed with update
        updatePassword(currentPassword, newPassword);
    }

    /**
     * Update password via API
     */
    private void updatePassword(String currentPassword, String newPassword) {
        try {
            // Get user data
            String userJsonStr = sharedPreferences.getString("user_json", "");
            if (userJsonStr.isEmpty()) {
                Toast.makeText(this, "User data not found. Please login again.", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject json = new JSONObject(userJsonStr);
            String userId = json.getString("_id");
            String storedPassword = json.getString("password");

            // Verify current password matches stored password
            if (!storedPassword.equals(currentPassword)) {
                layoutCurrentPassword.setError("Current password is incorrect");
                editCurrentPassword.requestFocus();
                return;
            }

            // Show progress
            btnUpdatePassword.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            executor.execute(() -> {
                // Call API to reset password
                String response = ApiHelper.resetPasswordById(userId, currentPassword, newPassword);

                runOnUiThread(() -> {
                    btnUpdatePassword.setEnabled(true);
                    progressBar.setVisibility(View.GONE);

                    if (response.startsWith("Error")) {
                        Toast.makeText(this, "Update failed: " + response, Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            // Update password in local storage
                            json.put("password", newPassword);
                            sharedPreferences.edit()
                                    .putString("user_json", json.toString())
                                    .apply();

                            Toast.makeText(this, "Password updated successfully! ✅", Toast.LENGTH_SHORT).show();

                            // Clear fields
                            editCurrentPassword.setText("");
                            editNewPassword.setText("");
                            editConfirmPassword.setText("");

                            // Return to previous screen
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Password updated on server but failed to save locally", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
            btnUpdatePassword.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error updating password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}