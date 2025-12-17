package com.example.dineo.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.dashboard.DashboardActivity;
import com.example.dineo.menu.GuestMenuActivity;
import com.example.dineo.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignUp;
    private String userRole;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRole = getIntent().getStringExtra("USER_ROLE");
        if (userRole == null) userRole = "customer";

        db = new DatabaseHelper(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);

        btnLogin.setOnClickListener(v -> handleLogin());
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.putExtra("USER_ROLE", userRole);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etEmail.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = db.loginUser(email, password);

        if (user == null) {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();

        Intent intent;
        if (user.getRole().equals("admin")) {
            intent = new Intent(this, DashboardActivity.class);
        } else {
            intent = new Intent(this, GuestMenuActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // After successful login:
        SharedPreferences sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.putString("user_password", password);
        editor.apply();
    }
}