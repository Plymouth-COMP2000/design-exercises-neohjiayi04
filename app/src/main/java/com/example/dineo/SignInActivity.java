package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.guest.GuestMenuActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText emailInput;
    private TextInputEditText etPassword;
    private MaterialButton btnSignIn;
    private TextView tvLogin;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Get user type from intent
        userType = getIntent().getStringExtra("USER_TYPE");
        if (userType == null) {
            userType = "customer"; // default
        }

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        emailInput = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvLogin = findViewById(R.id.tvLogin);

        // Sign in button click
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignIn();
            }
        });

        // Login link click
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Login activity
                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                intent.putExtra("USER_TYPE", userType);
                startActivity(intent);
                finish();
            }
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

        // TODO: Implement actual registration logic here
        // For now, just show a success message
        Toast.makeText(this, "Account created successfully as " + userType, Toast.LENGTH_SHORT).show();

        // Navigate to menu activity
        Intent intent = new Intent(SignInActivity.this, GuestMenuActivity.class);
        intent.putExtra("USER_TYPE", userType);
        startActivity(intent);
        finish();
    }
}