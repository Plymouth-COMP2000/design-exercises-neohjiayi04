package com.example.dineo.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.dashboard.DashboardActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Base activity for STAFF-side screens
 * Handles staff bottom navigation safely
 */
public abstract class StaffBaseActivity extends AppCompatActivity {

    protected void setupStaffBottomNavigation(int activeItemId) {

        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottomNavigationView);

        if (bottomNavigationView == null) return;

        bottomNavigationView.setSelectedItemId(activeItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == activeItemId) {
                return true;
            }

            Intent intent = null;

            if (item.getItemId() == R.id.nav_staff_reservations) {
                intent = new Intent(this, StaffReservationActivity.class);

            } else if (item.getItemId() == R.id.nav_staff_dashboard) {
                intent = new Intent(this, DashboardActivity.class);

            } else if (item.getItemId() == R.id.nav_staff_profile) {
                intent = new Intent(this, ProfileActivity.class);
            } else if (item.getItemId() == R.id.nav_menu_staff) {
                intent = new Intent(this, com.example.dineo.activities.StaffMenuActivity.class);
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
