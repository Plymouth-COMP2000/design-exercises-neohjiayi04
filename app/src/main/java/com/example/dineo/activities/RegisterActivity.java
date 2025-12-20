package com.example.dineo.activities;

import android.content.Intent;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText editUsername, editPassword, editFirstName, editLastName, editEmail, editContact;
    private Button btnRegister;
    private TextView textLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Bind views
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editContact = findViewById(R.id.editContact);
        btnRegister = findViewById(R.id.btnRegister);
        textLoginLink = findViewById(R.id.textLoginLink);

        // Button listeners
        btnRegister.setOnClickListener(v -> registerUser());
        textLoginLink.setOnClickListener(v -> finish()); // go back to login
    }

    private void registerUser() {
        final String username = editUsername.getText().toString().trim();
        final String password = editPassword.getText().toString().trim();
        final String firstName = editFirstName.getText().toString().trim();
        final String lastName = editLastName.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String contact = editContact.getText().toString().trim();
        final String userType = "Guest"; // default role

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                lastName.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading dialog
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Registering...");
        dialog.setCancelable(false);
        dialog.show();

        // Background thread for network requests
        new Thread(() -> {
            try {
                // 1️⃣ Register user
                String registerResponse = ApiHelper.createUser(
                        username, password, firstName, lastName, email, contact, userType
                );

                if (registerResponse.startsWith("Error")) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Registration failed: " + registerResponse, Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                // 2️⃣ Auto-login using username
                String loginResponse = ApiHelper.loginUser(username, password);

                JSONObject json = new JSONObject(loginResponse);
                String role = json.getString("usertype");

                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();

                    // Navigate to correct dashboard
                    if (role.equalsIgnoreCase("Staff")) {
                        startActivity(new Intent(this, StaffDashboardActivity.class));
                    } else {
                        startActivity(new Intent(this, GuestMenuActivity.class));
                    }

                    finishAffinity(); // close register + login activities
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
