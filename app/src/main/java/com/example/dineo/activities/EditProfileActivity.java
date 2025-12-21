package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileActivity extends BaseActivity {

    private TextInputEditText editFirstname, editLastname, editEmail, editContact;
    private MaterialButton btnSaveChanges;
    private ExecutorService executor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);

        setupBottomNavigation(R.id.nav_profile);

        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        executor = Executors.newSingleThreadExecutor();

        editFirstname = findViewById(R.id.editTextFirstname);
        editLastname = findViewById(R.id.editTextLastname);
        editEmail = findViewById(R.id.editTextEmail);
        editContact = findViewById(R.id.editTextContact);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        findViewById(R.id.imageViewBack).setOnClickListener(v -> finish());

        loadUserData();

        btnSaveChanges.setOnClickListener(v -> updateProfile());
    }

    private void loadUserData() {
        try {
            String userJsonStr = sharedPreferences.getString("user_json", "");
            if (userJsonStr.isEmpty()) return;
            JSONObject json = new JSONObject(userJsonStr);

            editFirstname.setText(json.optString("firstname"));
            editLastname.setText(json.optString("lastname"));
            editEmail.setText(json.optString("email"));
            editContact.setText(json.optString("contact"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        String firstname = editFirstname.getText().toString().trim();
        String lastname = editLastname.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String contact = editContact.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String userJsonStr = sharedPreferences.getString("user_json", "");
            JSONObject json = new JSONObject(userJsonStr);
            String userId = json.getString("_id");
            String usertype = json.getString("usertype");

            btnSaveChanges.setEnabled(false);

            executor.execute(() -> {
                String response = ApiHelper.updateUserById(userId, firstname, lastname, email, contact, usertype);

                runOnUiThread(() -> {
                    btnSaveChanges.setEnabled(true);
                    if (response.startsWith("Error")) {
                        Toast.makeText(this, "Update failed: "+response, Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            JSONObject updatedJson = new JSONObject(response);
                            sharedPreferences.edit().putString("user_json", updatedJson.toString()).apply();
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Updated but failed to save locally", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }
}
