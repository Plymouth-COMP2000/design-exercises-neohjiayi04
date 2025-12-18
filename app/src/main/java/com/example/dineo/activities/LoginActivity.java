package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;

import org.json.JSONObject;

/**
 * Login Activity - Role-based navigation (GUEST or STAFF)
 * Student ID: BSSE2506008
 */
public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private TextView textViewSignIn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            navigateBasedOnRole();
            return;
        }

        // Set click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin() {
        String username = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty()) {
            editTextEmail.setError("Username is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        // Show loading state
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        // Call API in background thread
        new LoginTask().execute(username, password);
    }

    // AsyncTask to handle API call
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            // Call API
            return ApiHelper.loginUser(username, password);
        }

        @Override
        protected void onPostExecute(String result) {
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");

            if (result.startsWith("Error")) {
                // Login failed
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Login successful - parse user data
                    JSONObject user = new JSONObject(result);

                    // Save user session
                    saveUserSession(user);

                    // Show success message
                    String username = user.getString("username");
                    Toast.makeText(LoginActivity.this,
                            "Welcome back, " + username + "!",
                            Toast.LENGTH_SHORT).show();

                    // Navigate based on role
                    navigateBasedOnRole();

                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this,
                            "Error parsing user data",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveUserSession(JSONObject user) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putInt("userId", user.getInt("id"));
            editor.putString("userName", user.getString("username"));
            editor.putString("userEmail", user.getString("email"));
            editor.putString("userPhone", user.optString("contact", ""));
            editor.putString("userRole", user.getString("usertype")); // GUEST or STAFF
            editor.putString("firstName", user.optString("firstname", ""));
            editor.putString("lastName", user.optString("lastname", ""));
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void navigateBasedOnRole() {
        String userRole = sharedPreferences.getString("userRole", "GUEST");

        Intent intent;
        if ("STAFF".equalsIgnoreCase(userRole)) {
            // Staff → Go to Dashboard
            intent = new Intent(LoginActivity.this, StaffDashboardActivity.class);
        } else {
            // Guest → Go to Menu
            intent = new Intent(LoginActivity.this, MenuActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}