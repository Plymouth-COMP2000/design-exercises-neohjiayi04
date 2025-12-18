package com.example.dineo.activities;

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

/**
 * Register Activity - Using ApiHelper pattern
 * Student ID: BSSE2506008
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button btnSignIn;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Set click listeners
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegister();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void performRegister() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
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

        // Show loading state
        btnSignIn.setEnabled(false);
        btnSignIn.setText("Creating account...");

        // Call API in background thread
        new RegisterTask().execute(username, password, email);
    }

    // AsyncTask to handle API call
    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            // Split username into firstname and lastname (if possible)
            String firstname = username;
            String lastname = "";
            if (username.contains(" ")) {
                String[] parts = username.split(" ", 2);
                firstname = parts[0];
                lastname = parts[1];
            }

            // Call API
            // createUser(username, password, firstname, lastname, email, contact, usertype)
            return ApiHelper.createUser(
                    username,
                    password,
                    firstname,
                    lastname,
                    email,
                    "",  // contact (empty for now)
                    "GUEST"  // default role
            );
        }

        @Override
        protected void onPostExecute(String result) {
            btnSignIn.setEnabled(true);
            btnSignIn.setText("Sign In");

            if (result.startsWith("Error")) {
                // Registration failed
                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
            } else {
                // Registration successful
                Toast.makeText(RegisterActivity.this,
                        "Registration successful! Please login.",
                        Toast.LENGTH_SHORT).show();

                // Go back to login
                finish();
            }
        }
    }
}