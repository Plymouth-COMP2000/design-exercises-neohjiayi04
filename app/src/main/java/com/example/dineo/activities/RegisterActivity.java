package com.example.dineo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        textLoginLink.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        final String username = editUsername.getText().toString().trim();
        final String password = editPassword.getText().toString().trim();
        final String firstName = editFirstName.getText().toString().trim();
        final String lastName = editLastName.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String contact = editContact.getText().toString().trim();
        final String userType = "Guest";

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                lastName.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Registering...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(() -> {
            try {
                String registerResponse = ApiHelper.createUser(username, password, firstName, lastName, email, contact, userType);

                if (registerResponse.startsWith("Error")) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                String loginResponse = ApiHelper.loginUser(username, password);
                if (loginResponse.startsWith("Error")) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Login failed after registration", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                sharedPreferences.edit().putString("user_json", loginResponse).apply();
                JSONObject json = new JSONObject(loginResponse);
                String role = json.getString("usertype");

                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Welcome " + firstName + "!", Toast.LENGTH_SHORT).show();
                    if ("Staff".equalsIgnoreCase(role)) {
                        startActivity(new Intent(this, StaffDashboardActivity.class));
                    } else {
                        startActivity(new Intent(this, GuestMenuActivity.class));
                    }
                    finishAffinity();
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
