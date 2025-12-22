package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Edit Profile Activity - FIXED VERSION
 * - Profile picture upload working ✅
 * - Only editable fields: firstname, lastname, contact ✅
 * - Read-only: email (username identifier) ✅
 * - Syncs with API ✅
 */
public class EditProfileActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private ImageView imageProfile, imageViewProfile;
    private TextInputEditText editFirstname, editLastname, editEmail, editContact;
    private MaterialButton btnSaveChanges;
    private ProgressBar progressBar;

    // Data
    private ExecutorService executor;
    private SharedPreferences sharedPreferences;
    private String profileImageBase64 = "";
    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);

        // Setup bottom navigation
        setupBottomNavigation(R.id.nav_profile);

        // Initialize
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        executor = Executors.newSingleThreadExecutor();

        // Find views
        imageProfile = findViewById(R.id.imageProfile);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editFirstname = findViewById(R.id.editTextFirstname);
        editLastname = findViewById(R.id.editTextLastname);
        editEmail = findViewById(R.id.editTextEmail);
        editContact = findViewById(R.id.editTextContact);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        progressBar = findViewById(R.id.progressBar);

        // Back button
        findViewById(R.id.imageViewBack).setOnClickListener(v -> finish());

        // Load user data
        loadUserData();

        // Make email READ-ONLY (it's the username identifier)
        editEmail.setEnabled(false);
        editEmail.setFocusable(false);

        // Profile picture upload
        imageViewProfile.setOnClickListener(v -> selectProfileImage());

        // Save button
        btnSaveChanges.setOnClickListener(v -> updateProfile());
    }

    /**
     * Load user data from SharedPreferences
     */
    private void loadUserData() {
        try {
            String userJsonStr = sharedPreferences.getString("user_json", "");
            if (userJsonStr.isEmpty()) {
                Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject json = new JSONObject(userJsonStr);

            // Populate fields
            editFirstname.setText(json.optString("firstname"));
            editLastname.setText(json.optString("lastname"));
            editEmail.setText(json.optString("email"));
            editContact.setText(json.optString("contact"));

            // Load profile image if exists
            profileImageBase64 = sharedPreferences.getString("profile_image", "");
            if (!profileImageBase64.isEmpty()) {
                try {
                    byte[] imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT);
                    Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageProfile.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open image picker
     */
    private void selectProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handle image selection result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Compress and resize
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

                // Convert to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageBytes = baos.toByteArray();
                profileImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                // Display image
                imageProfile.setImageBitmap(resized);
                imageChanged = true;

                Toast.makeText(this, "Image selected! Click Save to update.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Update profile via API
     */
    private void updateProfile() {
        // Get values
        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String contact = editContact.getText().toString().trim();

        // Validation
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String userJsonStr = sharedPreferences.getString("user_json", "");
            JSONObject json = new JSONObject(userJsonStr);
            String userId = json.getString("_id");
            String usertype = json.getString("usertype");

            // Show progress
            btnSaveChanges.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            executor.execute(() -> {
                // Call API to update user
                String response = ApiHelper.updateUserById(userId, firstname, lastname, email, contact, usertype);

                runOnUiThread(() -> {
                    btnSaveChanges.setEnabled(true);
                    progressBar.setVisibility(View.GONE);

                    if (response.startsWith("Error")) {
                        Toast.makeText(this, "Update failed: " + response, Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            // Parse response
                            JSONObject updatedJson = new JSONObject(response);

                            // Save updated user data
                            sharedPreferences.edit()
                                    .putString("user_json", updatedJson.toString())
                                    .apply();

                            // Save profile image separately (not sent to API)
                            if (imageChanged) {
                                sharedPreferences.edit()
                                        .putString("profile_image", profileImageBase64)
                                        .apply();
                            }

                            Toast.makeText(this, "Profile updated successfully! ✅", Toast.LENGTH_SHORT).show();

                            // Return to profile
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Updated but failed to save locally", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
            btnSaveChanges.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}