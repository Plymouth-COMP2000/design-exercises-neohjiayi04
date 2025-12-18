package com.example.dineo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.MenuAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu Activity - Display menu items with search and filter
 * Student ID: BSSE2506008
 */
public class MenuActivity extends AppCompatActivity implements MenuAdapter.OnMenuItemClickListener {

    private RecyclerView recyclerViewMenu;
    private MenuAdapter menuAdapter;
    private DatabaseHelper databaseHelper;
    private List<MenuItem> allMenuItems;
    private List<MenuItem> filteredMenuItems;
    private SearchView searchView;
    private ChipGroup chipGroupCategories;
    private ImageView imageViewNotification;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize views
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        searchView = findViewById(R.id.searchView);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Setup RecyclerView
        recyclerViewMenu.setLayoutManager(new GridLayoutManager(this, 2));

        // Load menu items
        loadMenuItems();

        // Setup search
        setupSearch();

        // Setup category filters
        setupCategoryFilters();

        // Setup notification bell
        imageViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        // Setup bottom navigation
        setupBottomNavigation();

        // Add sample data if empty (for demonstration)
        if (allMenuItems.isEmpty()) {
            addSampleMenuItems();
            loadMenuItems();
        }
    }

    private void loadMenuItems() {
        allMenuItems = databaseHelper.getAllMenuItems();
        filteredMenuItems = new ArrayList<>(allMenuItems);

        menuAdapter = new MenuAdapter(this, filteredMenuItems, this);
        recyclerViewMenu.setAdapter(menuAdapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenuItems(newText);
                return true;
            }
        });
    }

    private void filterMenuItems(String query) {
        filteredMenuItems.clear();

        if (query.isEmpty()) {
            filteredMenuItems.addAll(allMenuItems);
        } else {
            for (MenuItem item : allMenuItems) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredMenuItems.add(item);
                }
            }
        }

        menuAdapter.notifyDataSetChanged();
    }

    private void setupCategoryFilters() {
        chipGroupCategories.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                // Filter by category (implement based on your categories)
                // For now, just reload all items
                filterMenuItems("");
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_menu) {
                return true;
            } else if (itemId == R.id.nav_reservation) {
                startActivity(new Intent(MenuActivity.this, ReservationActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    @Override
    public void onMenuItemClick(MenuItem menuItem) {
        // Navigate to menu item detail
        Intent intent = new Intent(this, MenuDetailActivity.class);
        intent.putExtra("MENU_ITEM_ID", menuItem.getId());
        startActivity(intent);
    }

    private void addSampleMenuItems() {
        // Add sample menu items for demonstration
        databaseHelper.addMenuItem(new MenuItem("Fried Rice", 13.00, "",
                "Text description of the food. This food is delicious, yummy, awesome and unforgettable."));
        databaseHelper.addMenuItem(new MenuItem("Nasi Lemak", 8.50, "",
                "Traditional Malaysian fragrant rice dish cooked in coconut milk."));
        databaseHelper.addMenuItem(new MenuItem("Chicken Rendang", 15.00, "",
                "Spicy and tender chicken slow-cooked in aromatic spices."));
        databaseHelper.addMenuItem(new MenuItem("Satay", 12.00, "",
                "Grilled meat skewers served with peanut sauce."));
        databaseHelper.addMenuItem(new MenuItem("Roti Canai", 3.50, "",
                "Flaky flatbread served with curry dipping sauce."));
        databaseHelper.addMenuItem(new MenuItem("Laksa", 11.00, "",
                "Spicy noodle soup with a rich coconut-based broth."));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        // Update notification badge
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        String userEmail = sharedPreferences.getString("userEmail", "");
        int unreadCount = databaseHelper.getUnreadNotificationCount(userEmail);

        // Show badge if there are unread notifications
        // You can implement a custom badge view here
    }
}