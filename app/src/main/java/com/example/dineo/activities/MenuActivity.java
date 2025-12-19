package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
 * MenuActivity - Browse menu items safely
 * Fixed NullPointerException and safe filtering
 */
public class MenuActivity extends AppCompatActivity {

    private ImageView imageViewNotification;
    private EditText editTextSearch;
    private RecyclerView recyclerViewMenu;
    private BottomNavigationView bottomNavigationView;

    private Button btnCategoryAll, btnCategoryMain,
            btnCategoryDesserts, btnCategoryBeverages;

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

        btnCategoryAll = findViewById(R.id.btnCategoryAll);
        btnCategoryMain = findViewById(R.id.btnCategoryMain);
        btnCategoryDesserts = findViewById(R.id.btnCategoryDesserts);
        btnCategoryBeverages = findViewById(R.id.btnCategoryBeverages);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewMenu.setLayoutManager(new GridLayoutManager(this, 2));
        filteredMenuItems = new ArrayList<>();

        loadMenuItems();

        // Search functionality
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenuItems(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        imageViewNotification.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, NotificationActivity.class));
        });

        setupCategoryFilters();
        setupBottomNavigation();
    }

    private void setupCategoryFilters() {
        btnCategoryAll.setOnClickListener(v -> {
            currentCategory = "All";
            updateCategoryButtons();
            filterMenuItems(editTextSearch.getText().toString());
        });
        btnCategoryMain.setOnClickListener(v -> {
            currentCategory = "Main Food";
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
        int grayColor = 0xFFE0E0E0;
        int orangeColor = 0xFFFF9966;

        btnCategoryAll.setBackgroundColor(grayColor);
        btnCategoryMain.setBackgroundColor(grayColor);
        btnCategoryDesserts.setBackgroundColor(grayColor);
        btnCategoryBeverages.setBackgroundColor(grayColor);

        switch (currentCategory) {
            case "All": btnCategoryAll.setBackgroundColor(orangeColor); break;
            case "Main Food": btnCategoryMain.setBackgroundColor(orangeColor); break;
            case "Desserts": btnCategoryDesserts.setBackgroundColor(orangeColor); break;
            case "Beverages": btnCategoryBeverages.setBackgroundColor(orangeColor); break;
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

        if (menuAdapter == null) {
            menuAdapter = new MenuAdapter(this, filteredMenuItems, menuItem -> {
                Intent intent = new Intent(MenuActivity.this, MenuDetailActivity.class);
                intent.putExtra("MENU_ITEM_ID", menuItem.getId());
                startActivity(intent);
            });
            recyclerViewMenu.setAdapter(menuAdapter);
        } else {
            menuAdapter.notifyDataSetChanged();
        }
    }

    private void filterMenuItems(String query) {
        filteredMenuItems.clear();

        for (MenuItem item : menuItems) {
            boolean matchesSearch = item.getName() != null && item.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = "All".equals(currentCategory) || (item.getCategory() != null && item.getCategory().equals(currentCategory));

            if (matchesSearch && matchesCategory) {
                filteredMenuItems.add(item);
            }
        }

        if (menuAdapter != null) {
            menuAdapter.notifyDataSetChanged();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_menu) return true;
            else if (id == R.id.nav_reservation) {
                startActivity(new Intent(this, ReservationActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_menu);
        }
    }

    // Sample menu items for first launch
    private void addSampleMenuItems() {
        // Add only safe non-null categories
        addMenuItem("Fried Rice", 13.00, "Delicious fried rice", "Main Food", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400");
        addMenuItem("Nasi Lemak", 9.50, "Traditional Malaysian dish", "Main Food", "https://images.unsplash.com/photo-1596040033229-a0b3b4dc9937?w=400");
        addMenuItem("Satay", 12.00, "Grilled meat skewers", "Appetizers", "https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=400");
        addMenuItem("Ice Cream", 5.00, "Creamy vanilla ice cream", "Desserts", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400");
        addMenuItem("Teh Tarik", 3.00, "Malaysian milk tea", "Beverages", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400");
    }

    private void addMenuItem(String name, double price, String desc, String category, String imageUrl) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(price);
        item.setDescription(desc);
        item.setCategory(category);
        item.setImageUrl(imageUrl);
        databaseHelper.addMenuItem(item);
    }
}
