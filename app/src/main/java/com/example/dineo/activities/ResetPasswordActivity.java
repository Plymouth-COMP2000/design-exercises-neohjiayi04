package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResetPasswordActivity extends BaseActivity {

    private ImageView imageViewBack;
    private TextInputEditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private MaterialButton btnResetPassword;

    private SharedPreferences sharedPreferences;
    private ExecutorService executor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        setupBottomNavigation(R.id.nav_profile);

        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        executor = Executors.newSingleThreadExecutor();

        imageViewBack = findViewById(R.id.imageViewBack);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        imageViewBack.setOnClickListener(v -> finish());

        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "New password and confirmation do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String userJsonStr = sharedPreferences.getString("user_json", "");
            if (userJsonStr.isEmpty()) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject userJson = new JSONObject(userJsonStr);
            String userId = userJson.getString("_id");

            btnResetPassword.setEnabled(false);

            executor.execute(() -> {
                String response = ApiHelper.resetPasswordById(userId, currentPassword, newPassword);

                runOnUiThread(() -> {
                    btnResetPassword.setEnabled(true);

                    if (response.startsWith("Error")) {
                        Toast.makeText(ResetPasswordActivity.this, "Reset failed: " + response, Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            JSONObject updatedJson = new JSONObject(response);
                            // Update password in SharedPreferences locally
                            updatedJson.put("password", newPassword);
                            sharedPreferences.edit().putString("user_json", updatedJson.toString()).apply();

                            Toast.makeText(ResetPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(ResetPasswordActivity.this, "Password reset, but failed to save locally", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error resetting password", Toast.LENGTH_SHORT).show();
        }
    }
}
