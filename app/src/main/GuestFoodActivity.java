package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GuestFoodActivity extends AppCompatActivity {

    private ImageView ivBack, ivNotification, ivFoodImage;
    private TextView tvFoodName, tvPrice, tvDescription;
    private CheckBox cbLessSpicy, cbLessSalty, cbLessSweet;
    private LinearLayout navMenu, navReservation, navProfile;

    private String foodId;
    private String foodName;
    private double price;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_food);

        // Initialize views
        initializeViews();

        // Get data from intent
        loadFoodData();

        // Set up listeners
        setupListeners();
    }

    private void initializeViews() {
        // Header
        ivBack = findViewById(R.id.ivBack);
        ivNotification = findViewById(R.id.ivNotification);

        // Food details
        ivFoodImage = findViewById(R.id.ivFoodImage);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);

        // Checkboxes
        cbLessSpicy = findViewById(R.id.cbLessSpicy);
        cbLessSalty = findViewById(R.id.cbLessSalty);
        cbLessSweet = findViewById(R.id.cbLessSweet);

        // Bottom Navigation
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);
    }

    private void loadFoodData() {
        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            foodId = intent.getStringExtra("food_id");
            foodName = intent.getStringExtra("food_name");
            price = intent.getDoubleExtra("food_price", 0.0);
            description = intent.getStringExtra("food_description");
            int imageResId = intent.getIntExtra("food_image", R.drawable.m2);

            // Set data to views
            if (foodName != null) {
                tvFoodName.setText(foodName);
            }
            tvPrice.setText(String.format("RM %.2f", price));
            if (description != null) {
                tvDescription.setText(description);
            }
            ivFoodImage.setImageResource(imageResId);
        }
    }

    private void setupListeners() {
        // Back button
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Notification icon
        ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestFoodActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        // Bottom Navigation
        navMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestFoodActivity.this, GuestMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        navReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestFoodActivity.this, GuestReservationEditActivity.class);
                startActivity(intent);
                finish();
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuestFoodActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to get selected remarks
    private String getSelectedRemarks() {
        StringBuilder remarks = new StringBuilder();

        if (cbLessSpicy.isChecked()) {
            remarks.append("Less Spicy");
        }
        if (cbLessSalty.isChecked()) {
            if (remarks.length() > 0) remarks.append(", ");
            remarks.append("Less Salty");
        }
        if (cbLessSweet.isChecked()) {
            if (remarks.length() > 0) remarks.append(", ");
            remarks.append("Less Sweet");
        }

        return remarks.toString();
    }

    // Method to add to cart (you can implement this based on your cart logic)
    public void addToCart(View view) {
        String remarks = getSelectedRemarks();

        // TODO: Implement cart logic here
        // For now, just show a toast
        Toast.makeText(this, "Added to cart with remarks: " + remarks, Toast.LENGTH_SHORT).show();
    }
}