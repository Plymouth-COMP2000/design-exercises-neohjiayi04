package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

/**
 * Menu Detail Activity - Display food details
 * Student ID: BSSE2506008
 */
public class MenuDetailActivity extends AppCompatActivity {

    private ImageView imageViewBack, imageViewNotification, imageViewFood;
    private TextView textViewFoodName, textViewPrice, textViewDescription;
    private CheckBox checkBoxLessSpicy, checkBoxLessSalty, checkBoxLessSweet;

    private DatabaseHelper databaseHelper;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        imageViewFood = findViewById(R.id.imageViewFood);
        textViewFoodName = findViewById(R.id.textViewFoodName);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewDescription = findViewById(R.id.textViewDescription);
        checkBoxLessSpicy = findViewById(R.id.checkBoxLessSpicy);
        checkBoxLessSalty = findViewById(R.id.checkBoxLessSalty);
        checkBoxLessSweet = findViewById(R.id.checkBoxLessSweet);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Get menu item ID
        int menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);

        // Load menu item
        loadMenuItem(menuItemId);

        // Setup back button
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Setup notification bell
        imageViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuDetailActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadMenuItem(int menuItemId) {
        // In a real app, query by ID
        // For demonstration, using default data
        if (menuItemId != -1) {
            // Get from database
            // For now, use placeholder
            textViewFoodName.setText("Food Name");
            textViewPrice.setText("RM 00.00");
            textViewDescription.setText("Text description of the food. This food is delicious, yummy, awesome and unforgettable. If you come, must try!!! You won't be regretted.");

            // Load image
            imageViewFood.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}