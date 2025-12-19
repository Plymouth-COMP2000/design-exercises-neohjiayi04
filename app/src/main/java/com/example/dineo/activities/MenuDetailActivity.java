package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

import java.util.List;

/**
 * Menu Detail Activity - Display food details with customization
 * Student ID: BSSE2506008
 */
public class MenuDetailActivity extends AppCompatActivity {

    private ImageView imageViewBack, imageViewNotification, imageViewFood;
    private TextView textViewFoodName, textViewPrice, textViewDescription, textViewCategory;
    // REMOVED: Customization checkboxes and order button (not needed for browse-only menu)

    private DatabaseHelper databaseHelper;
    private MenuItem menuItem;
    private int unreadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();

        // Get menu item ID from intent
        int menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);

        if (menuItemId != -1) {
            loadMenuItem(menuItemId);
        } else {
            Toast.makeText(this, "Error loading menu item", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup click listeners
        setupClickListeners();

        // Load notification count
        loadNotificationCount();
    }

    private void initializeViews() {
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        imageViewFood = findViewById(R.id.imageViewFood);
        textViewFoodName = findViewById(R.id.textViewFoodName);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewCategory = findViewById(R.id.textViewCategory);
        // REMOVED: No customization or order button
    }

    private void setupClickListeners() {
        // Back button
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Notification bell
        imageViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuDetailActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        // REMOVED: Order button - this is browse-only
    }

    private void loadMenuItem(int menuItemId) {
        // Get all menu items and find the one with matching ID
        List<MenuItem> menuItems = databaseHelper.getAllMenuItems();

        for (MenuItem item : menuItems) {
            if (item.getId() == menuItemId) {
                menuItem = item;
                break;
            }
        }

        if (menuItem != null) {
            displayMenuItem();
        } else {
            Toast.makeText(this, "Menu item not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayMenuItem() {
        // Set food name
        textViewFoodName.setText(menuItem.getName());

        // Set price with formatting
        textViewPrice.setText(String.format("RM %.2f", menuItem.getPrice()));

        // Set description
        if (menuItem.getDescription() != null && !menuItem.getDescription().isEmpty()) {
            textViewDescription.setText(menuItem.getDescription());
        } else {
            textViewDescription.setText("Delicious " + menuItem.getName() + " made with the finest ingredients. A must-try dish that will leave you wanting more!");
        }

        // Set category
        if (menuItem.getCategory() != null && !menuItem.getCategory().isEmpty()) {
            textViewCategory.setText(menuItem.getCategory());
            textViewCategory.setVisibility(View.VISIBLE);
        } else {
            textViewCategory.setVisibility(View.GONE);
        }

        // Load image with Glide
        if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(menuItem.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(imageViewFood);
        } else {
            // Use default placeholder
            imageViewFood.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    // REMOVED - No order function needed
    // This is just a browse menu screen for guests

    private void loadNotificationCount() {
        SharedPreferences prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("userEmail", "");

        if (!userEmail.isEmpty()) {
            unreadCount = databaseHelper.getUnreadNotificationCount(userEmail);

            // Optional: Show badge on notification icon if you have a badge view
            // For now, just store the count
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload notification count when returning to this screen
        loadNotificationCount();
    }
}