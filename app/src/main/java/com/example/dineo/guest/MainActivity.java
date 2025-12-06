package com.example.dineo.guest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dineo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_menu) {
                selectedFragment = new MenuFragment();
            } else if (itemId == R.id.nav_reservation) {
                selectedFragment = new ReservationFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MenuFragment())
                    .commit();
        }
    }

    private String userType = "customer";
    private String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get user data from intent
        if (getIntent().hasExtra("USER_TYPE")) {
            userType = getIntent().getStringExtra("USER_TYPE");
        }
        if (getIntent().hasExtra("USER_EMAIL")) {
            userEmail = getIntent().getStringExtra("USER_EMAIL");
        }

        setupUI();
    }

    private void setupUI() {
        // TODO: Setup your main page UI based on userType
        // You can customize the UI for customer vs admin here

        // Example: Set welcome message
        // TextView welcomeText = findViewById(R.id.welcomeText);
        // welcomeText.setText("Welcome, " + userEmail + "!");
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to login screen
        // Show exit dialog or just minimize app
        moveTaskToBack(true);
    }
}