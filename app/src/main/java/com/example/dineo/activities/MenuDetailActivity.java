package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuDetailActivity extends AppCompatActivity {

    private ImageView imageViewBack, imageViewNotification, imageViewFood;
    private TextView textViewFoodName, textViewPrice, textViewDescription, textViewCategory;

    private DatabaseHelper databaseHelper;
    private MenuItem menuItem;
    private int unreadCount = 0;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

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
            try {
                startActivity(new Intent(MenuDetailActivity.this, NotificationActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Notifications not available", Toast.LENGTH_SHORT).show();
            }
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
        textViewFoodName.setText(menuItem.getName() != null ? menuItem.getName() : "Unknown Dish");
        textViewPrice.setText(menuItem.getPriceFormatted());

        if (menuItem.getDescription() != null && !menuItem.getDescription().isEmpty()) {
            textViewDescription.setText(menuItem.getDescription());
        } else {
            textViewDescription.setText("Delicious " + textViewFoodName.getText() + " made with the finest ingredients.");
        }

        if (menuItem.getCategory() != null && !menuItem.getCategory().isEmpty()) {
            textViewCategory.setText(menuItem.getCategory());
            textViewCategory.setVisibility(TextView.VISIBLE);
        } else {
            textViewCategory.setVisibility(TextView.GONE);
        }

        loadMenuImage(menuItem.getImageUrl());
    }

    private void loadMenuImage(String imageData) {
        if (imageData == null || imageData.isEmpty()) {
            imageViewFood.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
            imageViewFood.setImageResource(android.R.drawable.ic_menu_gallery); // placeholder
            executorService.execute(() -> {
                Bitmap bitmap = loadBitmapFromUrl(imageData);
                runOnUiThread(() -> {
                    if (bitmap != null) imageViewFood.setImageBitmap(bitmap);
                });
            });
        } else {
            try {
                byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) imageViewFood.setImageBitmap(bitmap);
                else imageViewFood.setImageResource(android.R.drawable.ic_menu_gallery);
            } catch (Exception e) {
                e.printStackTrace();
                imageViewFood.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }

    private Bitmap loadBitmapFromUrl(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            input.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow(); // clean up threads
    }
}
