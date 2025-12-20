package com.example.dineo.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupBottomNavigation(int activeItemId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView == null) return;

        bottomNavigationView.setSelectedItemId(activeItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == activeItemId) return true;

            Intent intent = null;

            if (item.getItemId() == R.id.nav_menu) {
                intent = new Intent(this, GuestMenuActivity.class);
            } else if (item.getItemId() == R.id.nav_reservation) {
                intent = new Intent(this, ReservationActivity.class);
            } else if (item.getItemId() == R.id.nav_profile) {
                intent = new Intent(this, ProfileActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }

            return true;
        });
    }
}
