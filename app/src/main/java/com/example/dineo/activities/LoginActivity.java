package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
 * Login Activity with Role-Based Navigation
 * Student ID: BSSE2506008
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private TextView textViewSignIn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "LoginActivity started");

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            Log.d(TAG, "User already logged in, navigating...");
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

        Log.d(TAG, "Attempting login for: " + username);

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

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            Log.d(TAG, "Calling API...");

            try {
                String result = ApiHelper.loginUser(username, password);
                Log.d(TAG, "API result: " + result);
                return result;
            } catch (Exception e) {
                Log.e(TAG, "API call failed: " + e.getMessage());
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");

            Log.d(TAG, "Processing result...");

            if (result == null || result.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Error: No response from server",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (result.startsWith("Error")) {
                // Login failed
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Login error: " + result);
            } else {
                try {
                    // Parse user data
                    JSONObject user = new JSONObject(result);

                    // Save user session
                    saveUserSession(user);

                    // Get user type for welcome message
                    String usertype = user.getString("usertype");
                    String username = user.getString("username");

                    Toast.makeText(LoginActivity.this,
                            "Welcome back, " + username + "!",
                            Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "Login successful - User type: " + usertype);

                    // Navigate based on role
                    navigateBasedOnRole();

                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this,
                            "Error parsing user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                }
            }
        }
    }

    private void saveUserSession(JSONObject user) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);

            // Save all user data
            if (user.has("id")) {
                editor.putInt("userId", user.getInt("id"));
            }
            editor.putString("userName", user.getString("username"));
            editor.putString("userEmail", user.getString("email"));
            editor.putString("userPhone", user.optString("contact", ""));
            editor.putString("firstName", user.optString("firstname", ""));
            editor.putString("lastName", user.optString("lastname", ""));

            // IMPORTANT: Save usertype for role-based navigation
            String usertype = user.getString("usertype");
            editor.putString("userRole", usertype); // "STAFF" or "GUEST"

            editor.apply();

            Log.d(TAG, "Session saved - Role: " + usertype);
        } catch (Exception e) {
            Log.e(TAG, "Error saving session: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    /**
     * Navigate based on user role
     * STAFF → Dashboard
     * GUEST → Menu
     */
    private void navigateBasedOnRole() {
        String userRole = sharedPreferences.getString("userRole", "GUEST");

        Log.d(TAG, "Navigating based on role: " + userRole);

        Intent intent;

        if ("STAFF".equalsIgnoreCase(userRole)) {
            // Staff user → Go to Dashboard
            intent = new Intent(LoginActivity.this, StaffDashboardActivity.class);
            Log.d(TAG, "→ Going to StaffDashboardActivity");
        } else {
            // Guest user → Go to Menu
            intent = new Intent(LoginActivity.this, MenuActivity.class);
            Log.d(TAG, "→ Going to MenuActivity");
        }

        // Clear activity stack so user can't go back to login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}