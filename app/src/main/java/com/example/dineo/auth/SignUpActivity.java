package com.example.dineo.auth;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword;
    private MaterialButton btnSignUp;
    private TextView tvLogin;
    private String userRole;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userRole = getIntent().getStringExtra("USER_ROLE");
        if (userRole == null) userRole = "customer";

        db = new DatabaseHelper(this);

        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvLogin = findViewById(R.id.tv_login);

        btnSignUp.setOnClickListener(v -> handleSignUp());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void handleSignUp() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etEmail.getText().toString().trim() : "";

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.isEmailExists(email)) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(username, email, password, userRole);
        boolean success = db.registerUser(user);

        if (success) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}