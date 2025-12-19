package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
 * Complete Login Activity with Tab Selection and Navigation
 * Student ID: BSSE2506008
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private Button btnStaffTab, btnGuestTab;
    private TextView textViewSignIn, textViewRoleInfo;
    private SharedPreferences sharedPreferences;

    private String selectedRole = "GUEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_login);
            Log.d(TAG, "✓ Layout set successfully");

            // Initialize SharedPreferences
            sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

            // Initialize views
            editTextEmail = findViewById(R.id.editTextEmail);
            editTextPassword = findViewById(R.id.editTextPassword);
            btnLogin = findViewById(R.id.btnLogin);
            btnStaffTab = findViewById(R.id.btnStaffTab);
            btnGuestTab = findViewById(R.id.btnGuestTab);
            textViewSignIn = findViewById(R.id.textViewSignIn);
            textViewRoleInfo = findViewById(R.id.textViewRoleInfo);

            Log.d(TAG, "✓ All views initialized");

            // Check if user is already logged in
            if (isUserLoggedIn()) {
                Log.d(TAG, "User already logged in, navigating...");
                navigateBasedOnRole();
                return;
            }

            // Set up tab listeners
            if (btnStaffTab != null) {
                btnStaffTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectStaffTab();
                    }
                });
            }

            if (btnGuestTab != null) {
                btnGuestTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectGuestTab();
                    }
                });
            }

            // Login button
            if (btnLogin != null) {
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performLogin();
                    }
                });
            }

            // Register link
            if (textViewSignIn != null) {
                textViewSignIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this,
                                    "Register screen not available yet",
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "RegisterActivity not found: " + e.getMessage());
                        }
                    }
                });
            }

            // Set default tab
            selectGuestTab();
            Log.d(TAG, "✓ onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in onCreate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectStaffTab() {
        try {
            selectedRole = "STAFF";

            if (btnStaffTab != null && btnGuestTab != null) {
                btnStaffTab.setBackgroundColor(Color.parseColor("#FF6B35"));
                btnStaffTab.setTextColor(Color.WHITE);
                btnGuestTab.setBackgroundColor(Color.WHITE);
                btnGuestTab.setTextColor(Color.parseColor("#999999"));
            }

            if (textViewRoleInfo != null) {
                textViewRoleInfo.setText("Selected Role: Staff");
            }

            if (editTextEmail != null) {
                editTextEmail.setHint("Staff Email (@rosewood.gmail.com)");
            }

            Log.d(TAG, "Staff tab selected");
        } catch (Exception e) {
            Log.e(TAG, "Error in selectStaffTab: " + e.getMessage());
        }
    }

    private void selectGuestTab() {
        try {
            selectedRole = "GUEST";

            if (btnGuestTab != null && btnStaffTab != null) {
                btnGuestTab.setBackgroundColor(Color.parseColor("#FF6B35"));
                btnGuestTab.setTextColor(Color.WHITE);
                btnStaffTab.setBackgroundColor(Color.WHITE);
                btnStaffTab.setTextColor(Color.parseColor("#999999"));
            }

            if (textViewRoleInfo != null) {
                textViewRoleInfo.setText("Selected Role: Guest");
            }

            if (editTextEmail != null) {
                editTextEmail.setHint("Email");
            }

            Log.d(TAG, "Guest tab selected");
        } catch (Exception e) {
            Log.e(TAG, "Error in selectGuestTab: " + e.getMessage());
        }
    }

    private void performLogin() {
        Log.d(TAG, "→ Login button clicked");

        if (editTextEmail == null || editTextPassword == null) {
            Toast.makeText(this, "Error: Form not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        Log.d(TAG, "Attempting login - Email: " + email + ", Role: " + selectedRole);

        // Validate inputs
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        // Validate staff email pattern
        if ("STAFF".equals(selectedRole)) {
            if (!email.toLowerCase().contains("@rosewood.gmail.com")) {
                Toast.makeText(this,
                        "Staff accounts must use @rosewood.gmail.com email",
                        Toast.LENGTH_LONG).show();
                editTextEmail.setError("Invalid staff email");
                editTextEmail.requestFocus();
                return;
            }
        }

        // Show loading state
        if (btnLogin != null) {
            btnLogin.setEnabled(false);
            btnLogin.setText("Logging in...");
        }

        // Call API in background
        new LoginTask().execute(email, password);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            Log.d(TAG, "Calling API...");

            try {
                String result = ApiHelper.loginUser(email, password);
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
            // Reset button
            if (btnLogin != null) {
                btnLogin.setEnabled(true);
                btnLogin.setText("LOGIN");
            }

            Log.d(TAG, "Processing API result...");

            if (result == null || result.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Error: No response from server",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (result.startsWith("Error")) {
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Login error: " + result);
            } else {
                try {
                    // Parse user data
                    JSONObject user = new JSONObject(result);

                    // Verify role matches
                    String apiUserType = user.getString("usertype").toUpperCase();

                    if (!apiUserType.equals(selectedRole)) {
                        Toast.makeText(LoginActivity.this,
                                "Please select the correct role tab (" + apiUserType + ")",
                                Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Role mismatch: Selected " + selectedRole +
                                " but API returned " + apiUserType);
                        return;
                    }

                    // Save session
                    saveUserSession(user);

                    String username = user.optString("username", "User");
                    Toast.makeText(LoginActivity.this,
                            "Welcome back, " + username + "!",
                            Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "✓ Login successful - Navigating to " + selectedRole + " screen");

                    // Navigate
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

            if (user.has("id")) {
                editor.putInt("userId", user.getInt("id"));
            }
            editor.putString("userName", user.optString("username", ""));
            editor.putString("userEmail", user.optString("email", ""));
            editor.putString("userPhone", user.optString("contact", ""));
            editor.putString("firstName", user.optString("firstname", ""));
            editor.putString("lastName", user.optString("lastname", ""));
            editor.putString("userRole", selectedRole);

            editor.apply();
            Log.d(TAG, "✓ Session saved - Role: " + selectedRole);
        } catch (Exception e) {
            Log.e(TAG, "Error saving session: " + e.getMessage());
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void navigateBasedOnRole() {
        try {
            String userRole = sharedPreferences.getString("userRole", "GUEST");
            Log.d(TAG, "Navigating based on role: " + userRole);

            Intent intent;

            if ("STAFF".equalsIgnoreCase(userRole)) {
                // Try to go to Staff Dashboard
                try {
                    intent = new Intent(LoginActivity.this, StaffDashboardActivity.class);
                    Log.d(TAG, "→ Going to StaffDashboardActivity");
                } catch (Exception e) {
                    Log.e(TAG, "StaffDashboardActivity not found, using MenuActivity");
                    intent = new Intent(LoginActivity.this, MenuActivity.class);
                }
            } else {
                // Try to go to Menu
                try {
                    intent = new Intent(LoginActivity.this, MenuActivity.class);
                    Log.d(TAG, "→ Going to MenuActivity");
                } catch (Exception e) {
                    Log.e(TAG, "MenuActivity not found!");
                    Toast.makeText(this,
                            "Error: Main screen not available yet",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Clear activity stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "❌ Navigation error: " + e.getMessage());
            Toast.makeText(this,
                    "Error navigating: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}