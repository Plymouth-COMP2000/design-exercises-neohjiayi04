package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;

/**
 * Edit Profile Activity - FIXED NULL POINTER
 * Student ID: BSSE2506008
 */
public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private ImageView imageViewBack;
    private EditText editTextUsername, editTextFirstname, editTextLastname,
            editTextEmail, editTextContact;
    private Button btnSaveChanges;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize SharedPreferences first
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Initialize views with NULL CHECK
        try {
            imageViewBack = findViewById(R.id.imageViewBack);
            editTextUsername = findViewById(R.id.editTextUsername);
            editTextFirstname = findViewById(R.id.editTextFirstname);
            editTextLastname = findViewById(R.id.editTextLastname);
            editTextEmail = findViewById(R.id.editTextEmail);
            editTextContact = findViewById(R.id.editTextContact);
            btnSaveChanges = findViewById(R.id.btnSaveChanges);

            // Check if all views found
            if (editTextUsername == null || editTextFirstname == null ||
                    editTextLastname == null || editTextEmail == null ||
                    editTextContact == null) {

                Log.e(TAG, "ERROR: Some views are null! Check activity_edit_profile.xml");
                Toast.makeText(this, "Layout error - check XML", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Load user data
            loadUserData();

            // Setup click listeners
            if (imageViewBack != null) {
                imageViewBack.setOnClickListener(v -> finish());
            }

            if (btnSaveChanges != null) {
                btnSaveChanges.setOnClickListener(v -> saveChanges());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserData() {
        try {
            // Load data from SharedPreferences
            String username = sharedPreferences.getString("userName", "");
            String firstname = sharedPreferences.getString("firstName", "");
            String lastname = sharedPreferences.getString("lastName", "");
            String email = sharedPreferences.getString("userEmail", "");
            String contact = sharedPreferences.getString("userPhone", "");

            // Set to EditTexts with NULL CHECK
            if (editTextUsername != null) editTextUsername.setText(username);
            if (editTextFirstname != null) editTextFirstname.setText(firstname);
            if (editTextLastname != null) editTextLastname.setText(lastname);
            if (editTextEmail != null) editTextEmail.setText(email);
            if (editTextContact != null) editTextContact.setText(contact);

            Log.d(TAG, "User data loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        try {
            // Get values with NULL CHECK
            String username = editTextUsername != null ? editTextUsername.getText().toString().trim() : "";
            String firstname = editTextFirstname != null ? editTextFirstname.getText().toString().trim() : "";
            String lastname = editTextLastname != null ? editTextLastname.getText().toString().trim() : "";
            String email = editTextEmail != null ? editTextEmail.getText().toString().trim() : "";
            String contact = editTextContact != null ? editTextContact.getText().toString().trim() : "";

            // Validate
            if (username.isEmpty()) {
                if (editTextUsername != null) {
                    editTextUsername.setError("Username is required");
                    editTextUsername.requestFocus();
                }
                return;
            }

            if (email.isEmpty()) {
                if (editTextEmail != null) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                }
                return;
            }

            // Show loading
            if (btnSaveChanges != null) {
                btnSaveChanges.setEnabled(false);
                btnSaveChanges.setText("Saving...");
            }

            // Save to SharedPreferences (no API update for now)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", username);
            editor.putString("firstName", firstname);
            editor.putString("lastName", lastname);
            editor.putString("userEmail", email);
            editor.putString("userPhone", contact);
            editor.apply();

            // Reset button
            if (btnSaveChanges != null) {
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("Save Changes");
            }

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "Profile saved successfully");

            // Go back
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error saving changes: " + e.getMessage());
            e.printStackTrace();

            if (btnSaveChanges != null) {
                btnSaveChanges.setEnabled(true);
                btnSaveChanges.setText("Save Changes");
            }

            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}