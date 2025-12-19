package com.example.dineo.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private TextInputEditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private TextInputLayout layoutCurrentPassword, layoutNewPassword, layoutConfirmPassword;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        imageViewBack = findViewById(R.id.imageViewBack);

        layoutCurrentPassword = findViewById(R.id.layoutCurrentPassword);
        layoutNewPassword = findViewById(R.id.layoutNewPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);

        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        btnResetPassword = findViewById(R.id.btnResetPassword);

        imageViewBack.setOnClickListener(v -> finish());
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        clearErrors();

        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty()) {
            layoutCurrentPassword.setError("Current password required");
            return;
        }

        if (newPassword.isEmpty()) {
            layoutNewPassword.setError("New password required");
            return;
        }

        if (newPassword.length() < 6) {
            layoutNewPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            layoutConfirmPassword.setError("Passwords do not match");
            return;
        }

        // TODO: API call
        Toast.makeText(this, "Password reset successful!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void clearErrors() {
        layoutCurrentPassword.setError(null);
        layoutNewPassword.setError(null);
        layoutConfirmPassword.setError(null);
    }
}
