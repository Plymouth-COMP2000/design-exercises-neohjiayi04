package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;
import com.example.dineo.database.DatabaseHelper;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView textRegister;

    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        textRegister = findViewById(R.id.textRegister);

        progressBar.setVisibility(View.GONE);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        btnLogin.setOnClickListener(v -> loginUser());
        textRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

    }

    private void loginUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Input validation
        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        new Thread(() -> {
            try {
                // STEP 1: Check if user exists in local database
                boolean userExists = dbHelper.isUserExists(username);

                if (!userExists) {
                    // User not found - show specific error
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this,
                                "Account not found. Please register first.",
                                Toast.LENGTH_LONG).show();
                        editTextUsername.setError("No account with this username");
                        editTextUsername.requestFocus();
                    });
                    return;
                }

                // STEP 2: User exists, now check password
                boolean correctPassword = dbHelper.checkUser(username, password);

                if (!correctPassword) {
                    // Wrong password - show specific error
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this,
                                "Incorrect password. Please try again.",
                                Toast.LENGTH_LONG).show();
                        editTextPassword.setError("Wrong password");
                        editTextPassword.requestFocus();
                        // Clear password field for security
                        editTextPassword.setText("");
                    });
                    return;
                }

                // STEP 3: Try API login for additional data
                String response = ApiHelper.loginUser(username, password);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (response.startsWith("Error")) {
                        // API failed but local login succeeded - proceed anyway
                        proceedToApp(username, "guest");
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response);
                        String role = json.getString("usertype");

                        // Save user data
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.putString("userEmail", username);
                        editor.putString("userRole", role);
                        editor.putString("user_json", response);
                        editor.apply();

                        Toast.makeText(LoginActivity.this,
                                "Welcome back, " + dbHelper.getUserName(username) + "!",
                                Toast.LENGTH_SHORT).show();

                        // Navigate based on role
                        if ("Staff".equalsIgnoreCase(role)) {
                            startActivity(new Intent(LoginActivity.this, StaffDashboardActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, GuestMenuActivity.class));
                        }
                        finish();

                    } catch (Exception e) {
                        // JSON parsing failed - use local data
                        proceedToApp(username, "guest");
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this,
                            "Connection error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * Proceed to appropriate dashboard
     */
    private void proceedToApp(String username, String role) {
        // Save login state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.putString("userEmail", username);
        editor.putString("userRole", role);
        editor.apply();

        String userName = dbHelper.getUserName(username);
        Toast.makeText(LoginActivity.this,
                "Welcome back" + (userName.isEmpty() ? "!" : ", " + userName + "!"),
                Toast.LENGTH_SHORT).show();

        // Navigate based on role
        if ("Staff".equalsIgnoreCase(role)) {
            startActivity(new Intent(LoginActivity.this, StaffDashboardActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, GuestMenuActivity.class));
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (btnLogin != null) {
            btnLogin.setEnabled(true);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}