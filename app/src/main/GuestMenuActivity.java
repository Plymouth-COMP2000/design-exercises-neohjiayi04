package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class GuestMenuActivity extends AppCompatActivity {

    private ImageView ivNotification;
    private EditText etSearch;
    private LinearLayout navMenu, navReservation, navProfile;

    // Category buttons
    private AppCompatButton btnAll, btnRice1, btnRice2, btnNoodle, btnMeat, btnVegetable, btnDrinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_menu);

        // Initialize views
        initializeViews();

        // Set up listeners
        setupListeners();
    }

    private void initializeViews() {
        // Header
        ivNotification = findViewById(R.id.ivNotification);

        // Search
        etSearch = findViewById(R.id.etSearch);

        // Bottom Navigation
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);

    }

    private void setupListeners() {
        // Notification icon click
        ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to notifications activity
                Intent intent = new Intent(GuestMenuActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Implement search filter logic here
                filterMenuItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bottom Navigation
        navMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already on menu page
            }
        });

        navReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to reservation activity
                Intent intent = new Intent(GuestMenuActivity.this, GuestReservationActivity.class);
                startActivity(intent);
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to profile activity
                Intent intent = new Intent(GuestMenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void filterMenuItems(String searchText) {
        // Implement your search/filter logic here
        if (searchText.isEmpty()) {
            // Show all items
        } else {
            // Filter items that match the search text
        }
    }

    // Method to handle category button clicks
    public void onCategoryClick(View view) {
        // Reset all buttons to default style
        resetCategoryButtons();

        // Set clicked button to selected style
        AppCompatButton clickedButton = (AppCompatButton) view;
        clickedButton.setBackgroundResource(R.drawable.label_hover_bg);
        clickedButton.setTextColor(getResources().getColor(android.R.color.white));

        // Filter items based on category
        String category = clickedButton.getText().toString();
        filterByCategory(category);
    }

    private void resetCategoryButtons() {
        // Reset all category buttons to default style
        // You would need to store references to all category buttons
        // and loop through them to reset their appearance
    }

    private void filterByCategory(String category) {
        // Implement category filter logic
        if (category.equals("All")) {
            // Show all items
        } else {
            // Show only items of selected category
        }
    }

    // Method to handle food item clicks
    public void onFoodItemClick(View view) {
        // Navigate to food detail page or add to cart
        Intent intent = new Intent(GuestMenuActivity.this, GuestFoodActivity.class);
        intent.putExtra("food_name", "Nasi Lemak");
        intent.putExtra("food_price", 12.50);
        intent.putExtra("food_description", "Traditional Malaysian dish...");
        intent.putExtra("food_image", R.drawable.m2);
        startActivity(intent);
    }

    private void displayFoodItems(List<GuestFoodItem> items) {
        // ADD THIS LINE HERE ⬇️
        Log.d("GuestMenu", "Displaying " + items.size() + " items");

        foodGridContainer.removeAllViews();

        // Create rows (2 items per row)
        for (int i = 0; i < items.size(); i += 2) {
            // ... rest of the code
        }
    }

📝 Complete Step-by-Step:
    Step 1: Add Import at the Top
    At the very top of GuestMenuActivity.java, add this import:
    javapackage com.example.dineo.guest;

import android.util.Log; // ← ADD THIS LINE
import android.content.Intent;
import android.os.Bundle;
// ... other imports
    Step 2: Add Debug Logs to Check Each Step
    Add these logs to different methods to see what's happening:
    java@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_menu);

        Log.d("GuestMenu", "onCreate started"); // ← ADD THIS

        initializeViews();
        initializeFoodData();

        Log.d("GuestMenu", "Food items loaded: " + allFoodItems.size()); // ← ADD THIS

        setupListeners();
        displayFoodItems(allFoodItems);

        Log.d("GuestMenu", "onCreate completed"); // ← ADD THIS
    }

    private void initializeViews() {
        Log.d("GuestMenu", "initializeViews started"); // ← ADD THIS

        // Header
        ivNotification = findViewById(R.id.ivNotification);
        etSearch = findViewById(R.id.etSearch);
        foodGridContainer = findViewById(R.id.foodGridContainer);

        if (foodGridContainer == null) {
            Log.e("GuestMenu", "ERROR: foodGridContainer is NULL!"); // ← ADD THIS
        } else {
            Log.d("GuestMenu", "foodGridContainer found successfully"); // ← ADD THIS
        }

        // ... rest of code
    }

    private void initializeFoodData() {
        Log.d("GuestMenu", "initializeFoodData started"); // ← ADD THIS

        // Add all your food items here
        allFoodItems.add(new GuestFoodItem("Fried Rice", 13.00, "Rice", R.drawable.r1, "Delicious fried rice"));
        // ... more items

        Log.d("GuestMenu", "Total items added: " + allFoodItems.size()); // ← ADD THIS

        filteredFoodItems.addAll(allFoodItems);
    }

    private void displayFoodItems(List<GuestFoodItem> items) {
        Log.d("GuestMenu", "displayFoodItems called with " + items.size() + " items"); // ← ADD THIS

        foodGridContainer.removeAllViews();

        // ... rest of code

        Log.d("GuestMenu", "displayFoodItems completed"); // ← ADD THIS
    }
}