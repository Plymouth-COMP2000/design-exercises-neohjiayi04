package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvLogin;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get user type from intent
        userType = getIntent().getStringExtra("USER_TYPE");
        if (userType == null) {
            userType = "customer"; // default
        }

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvLogin = findViewById(R.id.tvLogin);

        // Login button click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        // Sign up link click
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Sign In activity
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                intent.putExtra("USER_TYPE", userType);
                startActivity(intent);
            }
        });
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
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

        // TODO: Implement actual authentication logic here
        // For now, just show a success message
        Toast.makeText(this, "Login successful as " + userType, Toast.LENGTH_SHORT).show();

        // Navigate to main activity (you'll need to create this)
        // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // intent.putExtra("USER_TYPE", userType);
        // startActivity(intent);
        // finish();
    }
}