package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dineo.api.ApiService;
import com.example.dineo.guest.GuestMenuActivity;
import com.example.dineo.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText emailInput;
    private TextInputEditText etPassword;
    private MaterialButton btnSignIn;
    private TextView tvLogin;
    private SessionManager sessionManager;
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sessionManager = new SessionManager(this);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        emailInput = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvLogin = findViewById(R.id.tvLogin);

        // Sign in button click
        btnSignIn.setOnClickListener(v -> {
            if (!isRegistering) {
                performSignIn();
            }
        });

        // Login link click
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void performSignIn() {
        String username = etUsername.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Check password strength
        if (!isPasswordStrong(password)) {
            etPassword.setError("Password should contain letters and numbers");
            etPassword.requestFocus();
            return;
        }

        // Disable button and show loading
        isRegistering = true;
        btnSignIn.setEnabled(false);
        btnSignIn.setText("Creating Account...");

        // Split username into firstname and lastname (or use username as firstname)
        String firstname = username;
        String lastname = "";

        // Call API to create user
        ApiService.createUser(this, username, password, firstname, lastname,
                email, "", "guest", new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        isRegistering = false;
                        btnSignIn.setEnabled(true);
                        btnSignIn.setText("Sign In");

                        Toast.makeText(SignInActivity.this,
                                "Account created successfully!", Toast.LENGTH_SHORT).show();

                        // Auto-login: Save session
                        sessionManager.createLoginSession(username, password, firstname,
                                lastname, email, "", "guest");

                        // Navigate to profile
                        Intent intent = new Intent(SignInActivity.this, GuestMenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        isRegistering = false;
                        btnSignIn.setEnabled(true);
                        btnSignIn.setText("Sign In");

                        if (error.contains("already exists") || error.contains("duplicate")) {
                            Toast.makeText(SignInActivity.this,
                                    "Username or email already exists. Please try another.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignInActivity.this,
                                    "Failed to create account: " + error, Toast.LENGTH_LONG).show();
                        }
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