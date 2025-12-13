package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class ProfileResetActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText editCurrentPassword, editNewPassword, editConfirmPassword;
    private ImageView toggleCurrentPassword, toggleNewPassword, toggleConfirmPassword;
    private CardView updatePasswordButton;
    private TextView cancelButton;
    private LinearLayout navMenu, navReservation, navProfile;

    private SessionManager sessionManager;
    private boolean isUpdating = false;

    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_reset);

        sessionManager = new SessionManager(this);

        initializeViews();
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
        backButton.setOnClickListener(v -> finish());

        toggleCurrentPassword.setOnClickListener(v ->
                togglePasswordVisibility(editCurrentPassword, toggleCurrentPassword, 0));

        toggleNewPassword.setOnClickListener(v ->
                togglePasswordVisibility(editNewPassword, toggleNewPassword, 1));

        toggleConfirmPassword.setOnClickListener(v ->
                togglePasswordVisibility(editConfirmPassword, toggleConfirmPassword, 2));

        updatePasswordButton.setOnClickListener(v -> {
            if (!isUpdating) {
                updatePassword();
            }
        });

        cancelButton.setOnClickListener(v -> finish());

        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileResetActivity.this, GuestMenuActivity.class);
            startActivity(intent);
        });

        navReservation.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileResetActivity.this, ReservationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> finish());
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

        // Verify current password matches stored password
        String storedPassword = sessionManager.getPassword();
        if (!currentPassword.equals(storedPassword)) {
            editCurrentPassword.setError("Current password is incorrect");
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

        if (!isPasswordStrong(newPassword)) {
            editNewPassword.setError("Password should contain letters and numbers");
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

        // Disable button and show loading
        isUpdating = true;
        updatePasswordButton.setEnabled(false);
        Toast.makeText(this, "Updating password...", Toast.LENGTH_SHORT).show();

        // Get user data from session
        String username = sessionManager.getUsername();
        String firstname = sessionManager.getFirstName();
        String lastname = sessionManager.getLastName();
        String email = sessionManager.getEmail();
        String contact = sessionManager.getContact();
        String usertype = sessionManager.getUserType();

        // Call API to update user (including password)
        ApiService.updateUserProfile(this, username, firstname, lastname, email, contact,
                newPassword, usertype, new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        isUpdating = false;
                        updatePasswordButton.setEnabled(true);

                        // Update password in session
                        sessionManager.updatePassword(newPassword);

                        Toast.makeText(ProfileResetActivity.this,
                                "Password updated successfully", Toast.LENGTH_SHORT).show();

                        // Clear password fields
                        editCurrentPassword.setText("");
                        editNewPassword.setText("");
                        editConfirmPassword.setText("");

                        // Go back to profile page
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        isUpdating = false;
                        updatePasswordButton.setEnabled(true);

                        Toast.makeText(ProfileResetActivity.this,
                                "Failed to update password: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isPasswordStrong(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (hasLetter && hasDigit) return true;
        }

        return hasLetter && hasDigit;
    }
}