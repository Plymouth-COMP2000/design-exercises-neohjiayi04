package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;

/**
 * Reset Password Activity - Using ApiHelper pattern
 * Student ID: BSSE2506008
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private Button btnUpdatePassword, btnCancel;

    private SharedPreferences sharedPreferences;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnCancel = findViewById(R.id.btnCancel);

        // Initialize
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        // Setup back button
        imageViewBack.setOnClickListener(v -> finish());

        // Setup update button
        btnUpdatePassword.setOnClickListener(v -> updatePassword());

        // Setup cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updatePassword() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validate
        if (currentPassword.isEmpty()) {
            editTextCurrentPassword.setError("Current password is required");
            return;
        }

        if (newPassword.isEmpty()) {
            editTextNewPassword.setError("New password is required");
            return;
        }

        if (newPassword.length() < 6) {
            editTextNewPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Show loading
        btnUpdatePassword.setEnabled(false);
        btnUpdatePassword.setText("Updating...");

        // Call API in background
        new UpdatePasswordTask().execute(currentPassword, newPassword);
    }

    private class UpdatePasswordTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String currentPassword = params[0];
            String newPassword = params[1];

            // First verify current password by trying to login
            String username = sharedPreferences.getString("userName", "");
            String verifyResult = ApiHelper.loginUser(username, currentPassword);

            if (verifyResult.startsWith("Error")) {
                return "Error: Current password is incorrect";
            }

            // Get user info
            String email = sharedPreferences.getString("userEmail", "");
            String phone = sharedPreferences.getString("userPhone", "");
            String firstname = sharedPreferences.getString("firstName", "");
            String lastname = sharedPreferences.getString("lastName", "");
            String usertype = sharedPreferences.getString("userRole", "GUEST");

            // Update with new password
            return ApiHelper.updateUser(
                    userId,
                    username,
                    newPassword,  // New password
                    firstname,
                    lastname,
                    email,
                    phone,
                    usertype
            );
        }

        @Override
        protected void onPostExecute(String result) {
            btnUpdatePassword.setEnabled(true);
            btnUpdatePassword.setText("Update Password");

            if (result.startsWith("Error")) {
                Toast.makeText(ResetPasswordActivity.this, result, Toast.LENGTH_SHORT).show();
            } else {
                // Update stored password
                String newPassword = editTextNewPassword.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userPassword", newPassword);
                editor.apply();

                Toast.makeText(ResetPasswordActivity.this,
                        "Password updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}