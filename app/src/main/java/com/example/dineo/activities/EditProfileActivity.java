package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;

/**
 * Edit Profile Activity - Using ApiHelper pattern
 * Student ID: BSSE2506008
 */
public class EditProfileActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private EditText editTextFullName, editTextEmail, editTextPhone;
    private Button btnSaveChanges, btnCancel;

    private SharedPreferences sharedPreferences;
    private int userId;
    private String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);

        // Initialize
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Load current data
        loadUserData();

        // Setup back button
        imageViewBack.setOnClickListener(v -> finish());

        // Setup save button
        btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Setup cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        userId = sharedPreferences.getInt("userId", 0);
        String userName = sharedPreferences.getString("userName", "");
        String userEmail = sharedPreferences.getString("userEmail", "");
        String userPhone = sharedPreferences.getString("userPhone", "");

        editTextFullName.setText(userName);
        editTextEmail.setText(userEmail);
        editTextPhone.setText(userPhone);

        // Disable email editing
        editTextEmail.setEnabled(false);
    }

    private void saveChanges() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (fullName.isEmpty()) {
            editTextFullName.setError("Name is required");
            return;
        }

        // Show loading
        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("Saving...");

        // Call API in background
        new UpdateProfileTask().execute(fullName, email, phone);
    }

    private class UpdateProfileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String fullName = params[0];
            String email = params[1];
            String phone = params[2];

            // Split name
            String firstname = fullName;
            String lastname = "";
            if (fullName.contains(" ")) {
                String[] parts = fullName.split(" ", 2);
                firstname = parts[0];
                lastname = parts[1];
            }

            // Get stored info
            String password = sharedPreferences.getString("userPassword", "");
            String usertype = sharedPreferences.getString("userRole", "GUEST");
            String username = sharedPreferences.getString("userName", "");

            // Call API
            return ApiHelper.updateUser(
                    userId,
                    username,
                    password,  // Keep same password
                    firstname,
                    lastname,
                    email,
                    phone,
                    usertype
            );
        }

        @Override
        protected void onPostExecute(String result) {
            btnSaveChanges.setEnabled(true);
            btnSaveChanges.setText("Save Changes");

            if (result.startsWith("Error")) {
                Toast.makeText(EditProfileActivity.this, result, Toast.LENGTH_SHORT).show();
            } else {
                // Update local storage
                String fullName = editTextFullName.getText().toString();
                String phone = editTextPhone.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", fullName);
                editor.putString("userPhone", phone);
                editor.apply();

                Toast.makeText(EditProfileActivity.this,
                        "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}