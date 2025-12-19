package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.MenuAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu Activity - Display menu items
 * Student ID: BSSE2506008
 */
public class MenuActivity extends AppCompatActivity {

    private ImageView imageViewNotification;
    private EditText editTextSearch;
    private RecyclerView recyclerViewMenu;
    private BottomNavigationView bottomNavigationView;

    // Category buttons
    private Button btnCategoryAll;
    private Button btnCategoryAppetizers;
    private Button btnCategoryMain;
    private Button btnCategoryDesserts;
    private Button btnCategoryBeverages;

    private DatabaseHelper databaseHelper;
    private MenuAdapter menuAdapter;
    private List<MenuItem> menuItems;
    private List<MenuItem> filteredMenuItems;

    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize views
        imageViewNotification = findViewById(R.id.imageViewNotification);
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize category buttons
        btnCategoryAll = findViewById(R.id.btnCategoryAll);
        btnCategoryAppetizers = findViewById(R.id.btnCategoryAppetizers);
        btnCategoryMain = findViewById(R.id.btnCategoryMain);
        btnCategoryDesserts = findViewById(R.id.btnCategoryDesserts);
        btnCategoryBeverages = findViewById(R.id.btnCategoryBeverages);

        // Initialize
        databaseHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        recyclerViewMenu.setLayoutManager(new GridLayoutManager(this, 2));
        filteredMenuItems = new ArrayList<>();

        // Load menu items
        loadMenuItems();

        // Setup search with TextWatcher
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenuItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Notification click
        imageViewNotification.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, NotificationActivity.class));
        });

        // Setup category filters
        setupCategoryFilters();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void setupCategoryFilters() {
        btnCategoryAll.setOnClickListener(v -> {
            currentCategory = "All";
            updateCategoryButtons();
            filterMenuItems(editTextSearch.getText().toString());
        });

        btnCategoryAppetizers.setOnClickListener(v -> {
            currentCategory = "Appetizers";
            updateCategoryButtons();
            filterMenuItems(editTextSearch.getText().toString());
        });

        btnCategoryMain.setOnClickListener(v -> {
            currentCategory = "Main Course";
            updateCategoryButtons();
            filterMenuItems(editTextSearch.getText().toString());
        });

        btnCategoryDesserts.setOnClickListener(v -> {
            currentCategory = "Desserts";
            updateCategoryButtons();
            filterMenuItems(editTextSearch.getText().toString());
        });

        btnCategoryBeverages.setOnClickListener(v -> {
            currentCategory = "Beverages";
            updateCategoryButtons();
            filterMenuItems(editTextSearch.getText().toString());
        });
    }

    private void updateCategoryButtons() {
        // Reset all buttons to default style
        btnCategoryAll.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        btnCategoryAppetizers.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        btnCategoryMain.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        btnCategoryDesserts.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        btnCategoryBeverages.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));

        // Highlight selected button
        switch (currentCategory) {
            case "All":
                btnCategoryAll.setBackgroundTintList(getColorStateList(R.color.orange));
                break;
            case "Appetizers":
                btnCategoryAppetizers.setBackgroundTintList(getColorStateList(R.color.orange));
                break;
            case "Main Course":
                btnCategoryMain.setBackgroundTintList(getColorStateList(R.color.orange));
                break;
            case "Desserts":
                btnCategoryDesserts.setBackgroundTintList(getColorStateList(R.color.orange));
                break;
            case "Beverages":
                btnCategoryBeverages.setBackgroundTintList(getColorStateList(R.color.orange));
                break;
        }
    }

    private void loadMenuItems() {
        menuItems = databaseHelper.getAllMenuItems();

        if (menuItems.isEmpty()) {
            addSampleMenuItems();
            menuItems = databaseHelper.getAllMenuItems();
        }

        filteredMenuItems.clear();
        filteredMenuItems.addAll(menuItems);

        menuAdapter = new MenuAdapter(this, filteredMenuItems, menuItem -> {
            Intent intent = new Intent(MenuActivity.this, MenuDetailActivity.class);
            intent.putExtra("MENU_ITEM_ID", menuItem.getId());
            intent.putExtra("MENU_ITEM_NAME", menuItem.getName());
            intent.putExtra("MENU_ITEM_PRICE", menuItem.getPrice());
            intent.putExtra("MENU_ITEM_DESCRIPTION", menuItem.getDescription());
            intent.putExtra("MENU_ITEM_IMAGE", menuItem.getImageUrl());
            startActivity(intent);
        });

        recyclerViewMenu.setAdapter(menuAdapter);
    }

    private void filterMenuItems(String query) {
        filteredMenuItems.clear();

        for (MenuItem item : menuItems) {
            boolean matchesSearch = query.isEmpty() ||
                    item.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = currentCategory.equals("All") ||
                    item.getCategory().equals(currentCategory);

            if (matchesSearch && matchesCategory) {
                filteredMenuItems.add(item);
            }
        }

        menuAdapter.notifyDataSetChanged();
    }

    private void addSampleMenuItems() {
        addMenuItem("Fried Rice", 13.00, "Delicious fried rice with vegetables", "Main Course");
        addMenuItem("Nasi Lemak", 9.50, "Traditional Malaysian rice dish", "Main Course");
        addMenuItem("Chicken Rendang", 15.00, "Spicy tender chicken", "Main Course");
        addMenuItem("Satay", 12.00, "Grilled meat skewers", "Appetizers");
        addMenuItem("Roti Canai", 3.50, "Crispy flatbread", "Appetizers");
        addMenuItem("Laksa", 11.00, "Spicy noodle soup", "Main Course");
        addMenuItem("Ice Cream", 5.00, "Vanilla ice cream", "Desserts");
        addMenuItem("Teh Tarik", 3.00, "Malaysian pulled tea", "Beverages");
    }

    private void addMenuItem(String name, double price, String desc, String category) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(price);
        item.setDescription(desc);
        item.setCategory(category);
        item.setImageUrl("");
        databaseHelper.addMenuItem(item);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_menu) {
                return true;
            } else if (itemId == R.id.nav_reservation) {
                startActivity(new Intent(this, ReservationActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
    }
}