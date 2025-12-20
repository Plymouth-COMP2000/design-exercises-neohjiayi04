package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextUsername, editTextPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        textRegister = findViewById(R.id.textRegister);

        progressBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(v -> loginUser());

        // Navigate to Register
        textRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        new Thread(() -> {
            String response;
            try {
                response = ApiHelper.loginUser(username, password);
                Log.d(TAG, "Login response: " + response);
            } catch (Exception e) {
                Log.e(TAG, "Login error", e);
                response = "Error: " + e.getMessage();
            }

            final String finalResponse = response;

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (finalResponse.startsWith("Error")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject json = new JSONObject(finalResponse);
                    String userType = json.getString("usertype");

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    if ("Staff".equalsIgnoreCase(userType)) {
                        startActivity(new Intent(LoginActivity.this, StaffDashboardActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, GuestMenuActivity.class));
                    }
                    finish();

                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Invalid server response", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "JSON parsing error", e);
                }
            });

        }).start();
    }
}
