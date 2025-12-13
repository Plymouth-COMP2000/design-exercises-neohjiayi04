package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dineo.api.ApiService;
import com.example.dineo.guest.GuestMenuActivity;
import com.example.dineo.utils.SessionManager;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private boolean isLoggingIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            navigateToProfile();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        editEmail = findViewById(R.id.editEmail);        // Changed from editUsername
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            if (!isLoggingIn) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String email = editEmail.getText().toString().trim();           // Changed
        String password = editPassword.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email is required");                    // Changed
            editEmail.requestFocus();
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email");           // Added
            editEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Password is required");
            editPassword.requestFocus();
            return;
        }

        isLoggingIn = true;
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Call API with email instead of username
        ApiService.loginUserByEmail(this, email, password, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                isLoggingIn = false;
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                try {
                    JSONObject user = response.getJSONObject("user");
                    String username = user.getString("username");
                    String password = user.getString("password");
                    String firstname = user.getString("firstname");
                    String lastname = user.getString("lastname");
                    String email = user.getString("email");
                    String contact = user.getString("contact");
                    String usertype = user.getString("usertype");

                    sessionManager.createLoginSession(username, password, firstname,
                            lastname, email, contact, usertype);

                    Toast.makeText(LoginActivity.this,
                            "Welcome " + firstname + "!", Toast.LENGTH_SHORT).show();

                    navigateToProfile();

                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this,
                            "Error processing login data", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                isLoggingIn = false;
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToProfile() {
        Intent intent = new Intent(LoginActivity.this, GuestMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}