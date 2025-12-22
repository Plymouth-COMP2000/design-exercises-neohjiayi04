package com.example.dineo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;
import com.example.dineo.database.DatabaseHelper;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText editUsername, editPassword, editFirstName, editLastName, editEmail, editContact;
    private Button btnRegister;
    private TextView textLoginLink;

    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editContact = findViewById(R.id.editContact);
        btnRegister = findViewById(R.id.btnRegister);
        textLoginLink = findViewById(R.id.textLoginLink);

        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        btnRegister.setOnClickListener(v -> registerUser());
        textLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        final String username = editUsername.getText().toString().trim();
        final String password = editPassword.getText().toString().trim();
        final String firstName = editFirstName.getText().toString().trim();
        final String lastName = editLastName.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String contact = editContact.getText().toString().trim();
        final String userType = "Guest";

        // Validation
        if (username.isEmpty()) {
            editUsername.setError("Username is required");
            editUsername.requestFocus();
            return;
        }

        if (username.length() < 4) {
            editUsername.setError("Username must be at least 4 characters");
            editUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email");
            editEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Password is required");
            editPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            editPassword.requestFocus();
            return;
        }

        if (firstName.isEmpty()) {
            editFirstName.setError("First name is required");
            editFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            editLastName.setError("Last name is required");
            editLastName.requestFocus();
            return;
        }

        if (contact.isEmpty()) {
            editContact.setError("Contact number is required");
            editContact.requestFocus();
            return;
        }

        if (contact.length() < 10) {
            editContact.setError("Please enter a valid contact number");
            editContact.requestFocus();
            return;
        }

        // Check if user already exists
        if (dbHelper.isUserExists(username)) {
            editUsername.setError("Username already taken");
            editUsername.requestFocus();
            Toast.makeText(this, "This username is already registered", Toast.LENGTH_LONG).show();
            return;
        }

        if (dbHelper.isUserExists(email)) {
            editEmail.setError("Email already registered");
            editEmail.requestFocus();
            Toast.makeText(this, "This email is already registered", Toast.LENGTH_LONG).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Creating your account...");
        dialog.setCancelable(false);
        dialog.show();

        btnRegister.setEnabled(false);

        new Thread(() -> {
            try {
                // Register to local database first
                String fullName = firstName + " " + lastName;
                long localUserId = dbHelper.addUser(username, password, fullName);

                if (localUserId == -1) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        btnRegister.setEnabled(true);
                        Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                // Try to register to API (optional)
                String registerResponse = ApiHelper.createUser(username, password, firstName, lastName, email, contact, userType);

                if (!registerResponse.startsWith("Error")) {
                    // API registration successful, try to login to get user data
                    String loginResponse = ApiHelper.loginUser(username, password);
                    if (!loginResponse.startsWith("Error")) {
                        sharedPreferences.edit().putString("user_json", loginResponse).apply();
                    }
                }

                // Save login session
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("username", username);
                editor.putString("userEmail", email);
                editor.putString("userRole", "Guest");
                editor.apply();

                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Welcome to Dineo, " + firstName + "!", Toast.LENGTH_LONG).show();

                    // Navigate to guest menu
                    Intent intent = new Intent(this, GuestMenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
            }
        }).start();
    }
}