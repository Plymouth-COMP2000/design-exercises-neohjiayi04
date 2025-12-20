package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_REQUEST = 101;

    private TextView textViewUserName, textViewUserEmail, textViewUserPhone;
    private ImageView imageViewProfile, imageViewNotification;
    private Switch switchReservationBooking, switchReservationModification,
            switchReservationCancellation, switchNewReservationAlert;
    private LinearLayout layoutEditProfile, layoutResetPassword, layoutLogout;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Bind views
        imageViewProfile = findViewById(R.id.imageViewProfile);
        imageViewNotification = findViewById(R.id.imageViewNotification);

        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        textViewUserPhone = findViewById(R.id.textViewUserPhone);

        switchReservationBooking = findViewById(R.id.switchReservationBooking);
        switchReservationModification = findViewById(R.id.switchReservationModification);
        switchReservationCancellation = findViewById(R.id.switchReservationCancellation);
        switchNewReservationAlert = findViewById(R.id.switchNewReservationAlert);

        layoutEditProfile = findViewById(R.id.layoutEditProfile);
        layoutResetPassword = findViewById(R.id.layoutResetPassword);
        layoutLogout = findViewById(R.id.layoutLogout);

        layoutEditProfile.setOnClickListener(v ->
                startActivityForResult(new Intent(this, EditProfileActivity.class), EDIT_PROFILE_REQUEST)
        );

        layoutResetPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ResetPasswordActivity.class))
        );

        layoutLogout.setOnClickListener(v -> logout());

        imageViewNotification.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class))
        );

        loadUserData();
        setupNotificationSwitches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // refresh profile info
    }

    private void loadUserData() {
        String userJson = sharedPreferences.getString("user_json", null);
        if (userJson == null) {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        try {
            JSONObject json = new JSONObject(userJson);

            String firstName = json.optString("firstname", "");
            String lastName = json.optString("lastname", "");
            String email = json.optString("email", "");
            String contact = json.optString("contact", "");
            String profileUriStr = json.optString("profileImageUri", "");

            textViewUserName.setText(firstName + " " + lastName);
            textViewUserEmail.setText(email);
            textViewUserPhone.setText(contact);

            if (!profileUriStr.isEmpty()) {
                imageViewProfile.setImageURI(Uri.parse(profileUriStr));
            } else {
                imageViewProfile.setImageResource(android.R.drawable.ic_menu_myplaces);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_LONG).show();
        }
    }

    private void setupNotificationSwitches() {
        switchReservationBooking.setChecked(sharedPreferences.getBoolean("notifyBooking", true));
        switchReservationModification.setChecked(sharedPreferences.getBoolean("notifyModification", true));
        switchReservationCancellation.setChecked(sharedPreferences.getBoolean("notifyCancellation", true));
        switchNewReservationAlert.setChecked(sharedPreferences.getBoolean("notifyNewReservation", true));

        switchReservationBooking.setOnCheckedChangeListener((b, v) ->
                sharedPreferences.edit().putBoolean("notifyBooking", v).apply());
        switchReservationModification.setOnCheckedChangeListener((b, v) ->
                sharedPreferences.edit().putBoolean("notifyModification", v).apply());
        switchReservationCancellation.setOnCheckedChangeListener((b, v) ->
                sharedPreferences.edit().putBoolean("notifyCancellation", v).apply());
        switchNewReservationAlert.setOnCheckedChangeListener((b, v) ->
                sharedPreferences.edit().putBoolean("notifyNewReservation", v).apply());
    }

    private void logout() {
        sharedPreferences.edit().clear().apply();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            loadUserData();
        }
    }
}
