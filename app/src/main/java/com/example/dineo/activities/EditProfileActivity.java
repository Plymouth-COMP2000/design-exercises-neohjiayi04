package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;

/**
 * Edit Profile Activity - Update user information
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

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextFirstname = findViewById(R.id.editTextFirstname);
        editTextLastname = findViewById(R.id.editTextLastname);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContact = findViewById(R.id.editTextContact);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Load current user data
        loadUserData();

        // Setup click listeners
        imageViewBack.setOnClickListener(v -> finish());

        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        // Load data from SharedPreferences
        String username = sharedPreferences.getString("userName", "");
        String firstname = sharedPreferences.getString("firstName", "");
        String lastname = sharedPreferences.getString("lastName", "");
        String email = sharedPreferences.getString("userEmail", "");
        String contact = sharedPreferences.getString("userPhone", "");

        // Set to EditTexts
        editTextUsername.setText(username);
        editTextFirstname.setText(firstname);
        editTextLastname.setText(lastname);
        editTextEmail.setText(email);
        editTextContact.setText(contact);
    }

    private void saveChanges() {
        // Get values
        String username = editTextUsername.getText().toString().trim();
        String firstname = editTextFirstname.getText().toString().trim();
        String lastname = editTextLastname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();

        // Validate
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

        // Show loading
        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("Saving...");

        // Get user ID and other data
        int userId = sharedPreferences.getInt("userId", -1);
        String password = sharedPreferences.getString("userPassword", ""); // If saved
        String usertype = sharedPreferences.getString("userRole", "GUEST");

        // If password not saved, use a placeholder (API requires it)
        if (password.isEmpty()) {
            password = "unchanged"; // The API might not allow empty password
        }

        Log.d(TAG, "Updating user ID: " + userId);

        // Call API
        new UpdateProfileTask().execute(
                String.valueOf(userId),  // Convert int to String ← FIX!
                username,
                password,
                firstname,
                lastname,
                email,
                contact,
                usertype
        );
    }

    private class UpdateProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            String username = params[1];
            String password = params[2];
            String firstname = params[3];
            String lastname = params[4];
            String email = params[5];
            String contact = params[6];
            String usertype = params[7];

            try {
                return ApiHelper.updateUser(userId, username, password,
                        firstname, lastname, email,
                        contact, usertype);
            } catch (Exception e) {
                Log.e(TAG, "Error updating profile: " + e.getMessage());
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            btnSaveChanges.setEnabled(true);
            btnSaveChanges.setText("Save Changes");

            Log.d(TAG, "Update result: " + result);

            if (result.startsWith("Error")) {
                Toast.makeText(EditProfileActivity.this,
                        result,
                        Toast.LENGTH_LONG).show();
            } else {
                // Update SharedPreferences with new data
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", editTextUsername.getText().toString());
                editor.putString("firstName", editTextFirstname.getText().toString());
                editor.putString("lastName", editTextLastname.getText().toString());
                editor.putString("userEmail", editTextEmail.getText().toString());
                editor.putString("userPhone", editTextContact.getText().toString());
                editor.apply();

                Toast.makeText(EditProfileActivity.this,
                        "Profile updated successfully!",
                        Toast.LENGTH_SHORT).show();

                finish(); // Go back to profile
            }
        }
    }
}