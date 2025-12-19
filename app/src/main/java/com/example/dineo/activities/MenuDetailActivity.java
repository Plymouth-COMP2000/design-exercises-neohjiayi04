package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
 * MenuDetailActivity - Browse-only food details safely
 */
public class MenuDetailActivity extends AppCompatActivity {

    private ImageView imageViewBack, imageViewNotification, imageViewFood;
    private TextView textViewFoodName, textViewPrice, textViewDescription, textViewCategory;

    private DatabaseHelper databaseHelper;
    private MenuItem menuItem;
    private int unreadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();

        int menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);

        if (menuItemId != -1) {
            loadMenuItem(menuItemId);
        } else {
            Toast.makeText(this, "Error loading menu item", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupClickListeners();
        loadNotificationCount();
    }

    private void initializeViews() {
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        imageViewFood = findViewById(R.id.imageViewFood);
        textViewFoodName = findViewById(R.id.textViewMenuName);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewCategory = findViewById(R.id.textViewCategory);
    }

    private void setupClickListeners() {
        imageViewBack.setOnClickListener(v -> finish());

        imageViewNotification.setOnClickListener(v -> {
            startActivity(new Intent(MenuDetailActivity.this, NotificationActivity.class));
        });
    }

    private void loadMenuItem(int menuItemId) {
        List<MenuItem> menuItems = databaseHelper.getAllMenuItems();

        menuItem = null;
        for (MenuItem item : menuItems) {
            if (item != null && item.getId() == menuItemId) {
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
        // Food name
        textViewFoodName.setText(menuItem.getName() != null ? menuItem.getName() : "Unknown Dish");

        // Price
        textViewPrice.setText(menuItem.getPrice() >= 0 ? String.format("RM %.2f", menuItem.getPrice()) : "Price N/A");

        // Description
        if (menuItem.getDescription() != null && !menuItem.getDescription().isEmpty()) {
            textViewDescription.setText(menuItem.getDescription());
        } else {
            textViewDescription.setText("Delicious " + textViewFoodName.getText() + " made with the finest ingredients.");
        }

        // Category
        if (menuItem.getCategory() != null && !menuItem.getCategory().isEmpty()) {
            textViewCategory.setText(menuItem.getCategory());
            textViewCategory.setVisibility(TextView.VISIBLE);
        } else {
            textViewCategory.setVisibility(TextView.GONE);
        }

        // Image with Glide (safe)
        String imageUrl = menuItem.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(imageViewFood);
        } else {
            imageViewFood.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void loadNotificationCount() {
        SharedPreferences prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("userEmail", "");

        if (userEmail != null && !userEmail.isEmpty()) {
            unreadCount = databaseHelper.getUnreadNotificationCount(userEmail);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotificationCount();
    }
}
