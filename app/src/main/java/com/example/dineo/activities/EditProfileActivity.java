package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;

/**
 * Edit Profile Activity - FIXED NULL POINTER
 * Added Profile Picture Upload Feature
 * Student ID: BSSE2506008
 */
public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 100;

    private ImageView imageViewBack, imageViewProfile;
    private EditText editTextUsername, editTextFirstname, editTextLastname,
            editTextEmail, editTextContact;
    private Button btnSaveChanges;

    private SharedPreferences sharedPreferences;
    private Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize SharedPreferences first
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Initialize views with NULL CHECK
        try {
            imageViewBack = findViewById(R.id.imageViewBack);
            imageViewProfile = findViewById(R.id.imageViewProfile); // Profile Image
            editTextUsername = findViewById(R.id.editTextUsername);
            editTextFirstname = findViewById(R.id.editTextFirstname);
            editTextLastname = findViewById(R.id.editTextLastname);
            editTextEmail = findViewById(R.id.editTextEmail);
            editTextContact = findViewById(R.id.editTextContact);
            btnSaveChanges = findViewById(R.id.btnSaveChanges);

            // Check if all views found
            if (editTextUsername == null || editTextFirstname == null ||
                    editTextLastname == null || editTextEmail == null ||
                    editTextContact == null || imageViewProfile == null) {

                Log.e(TAG, "ERROR: Some views are null! Check activity_edit_profile.xml");
                Toast.makeText(this, "Layout error - check XML", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Load user data
            loadUserData();

            // Setup click listeners
            imageViewBack.setOnClickListener(v -> finish());

            btnSaveChanges.setOnClickListener(v -> saveChanges());

            // Profile picture click
            imageViewProfile.setOnClickListener(v -> openImagePicker());

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Load user data from SharedPreferences
     */
    private void loadUserData() {
        try {
            // Load text data
            String username = sharedPreferences.getString("userName", "");
            String firstname = sharedPreferences.getString("firstName", "");
            String lastname = sharedPreferences.getString("lastName", "");
            String email = sharedPreferences.getString("userEmail", "");
            String contact = sharedPreferences.getString("userPhone", "");
            String profileUriString = sharedPreferences.getString("profileImageUri", "");

            // Set to EditTexts
            editTextUsername.setText(username);
            editTextFirstname.setText(firstname);
            editTextLastname.setText(lastname);
            editTextEmail.setText(email);
            editTextContact.setText(contact);

            // Load profile picture
            if (!profileUriString.isEmpty()) {
                profileImageUri = Uri.parse(profileUriString);
                imageViewProfile.setImageURI(profileImageUri);
            }

            Log.d(TAG, "User data loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open Gallery to pick profile image
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.getData();
            if (profileImageUri != null) {
                imageViewProfile.setImageURI(profileImageUri);
            }
        }
    }

    /**
     * Save changes to SharedPreferences
     */
    private void saveChanges() {
        try {
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

            // Save to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", username);
            editor.putString("firstName", firstname);
            editor.putString("lastName", lastname);
            editor.putString("userEmail", email);
            editor.putString("userPhone", contact);

            // Save profile image URI
            if (profileImageUri != null) {
                editor.putString("profileImageUri", profileImageUri.toString());
            }

            editor.apply();

            btnSaveChanges.setEnabled(true);
            btnSaveChanges.setText("Save Changes");

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Profile saved successfully");

            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error saving changes: " + e.getMessage());
            e.printStackTrace();

            btnSaveChanges.setEnabled(true);
            btnSaveChanges.setText("Save Changes");

            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
