package com.example.dineo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;

/**
 * Register Activity with Role Selection
 * Student ID: BSSE2506008
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText editTextUsername, editTextPassword, editTextFirstname,
            editTextLastname, editTextEmail, editTextContact;
    private RadioGroup radioGroupUserType;
    private RadioButton radioGuest, radioStaff;
    private Button btnRegister;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFirstname = findViewById(R.id.editTextFirstname);
        editTextLastname = findViewById(R.id.editTextLastname);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContact = findViewById(R.id.editTextContact);
        radioGroupUserType = findViewById(R.id.radioGroupUserType);
        radioGuest = findViewById(R.id.radioGuest);
        radioStaff = findViewById(R.id.radioStaff);
        btnRegister = findViewById(R.id.btnRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Set default to Guest
        radioGuest.setChecked(true);

        // Set click listeners
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login
            }
        });
    }

    private void performRegistration() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String firstname = editTextFirstname.getText().toString().trim();
        String lastname = editTextLastname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();

        // Get selected user type
        String usertype = radioGuest.isChecked() ? "GUEST" : "STAFF";

        Log.d(TAG, "Registering as: " + usertype);

        // Validate inputs
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

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        // Show loading state
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        // Call API in background
        new RegisterTask().execute(username, password, firstname, lastname,
                email, contact, usertype);
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String firstname = params[2];
            String lastname = params[3];
            String email = params[4];
            String contact = params[5];
            String usertype = params[6];

            Log.d(TAG, "Calling API to create user...");

            try {
                return ApiHelper.createUser(username, password, firstname,
                        lastname, email, contact, usertype);
            } catch (Exception e) {
                Log.e(TAG, "API call failed: " + e.getMessage());
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            btnRegister.setEnabled(true);
            btnRegister.setText("Register");

            Log.d(TAG, "Registration result: " + result);

            if (result.startsWith("Error")) {
                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(RegisterActivity.this,
                        "Account created successfully! Please login.",
                        Toast.LENGTH_LONG).show();
                finish(); // Go back to login
            }
        }
    }
}