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

import java.util.ArrayList;
import java.util.List;

/**
 * Menu Activity - Display menu items for guests
 * Student ID: BSSE2506008
 */
public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    private ImageView imageViewNotification;
    private SearchView searchView;
    private RecyclerView recyclerViewMenu;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private MenuAdapter menuAdapter;
    private List<MenuItem> menuItems;
    private List<MenuItem> filteredMenuItems;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize views
        imageViewNotification = findViewById(R.id.imageViewNotification);
        searchView = findViewById(R.id.searchView);
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);

        // Setup RecyclerView
        recyclerViewMenu.setLayoutManager(new GridLayoutManager(this, 2));
        filteredMenuItems = new ArrayList<>();

        // Load menu items
        loadMenuItems();

        // Setup search
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

        // Notification click
        imageViewNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void loadMenuItems() {
        menuItems = databaseHelper.getAllMenuItems();

        // If no items in database, add sample items
        if (menuItems.isEmpty()) {
            addSampleMenuItems();
            menuItems = databaseHelper.getAllMenuItems();
        }

        filteredMenuItems.clear();
        filteredMenuItems.addAll(menuItems);

        // Setup adapter
        menuAdapter = new MenuAdapter(this, filteredMenuItems, menuItem -> {
            // Click listener - go to detail
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

        if (query.isEmpty()) {
            filteredMenuItems.addAll(menuItems);
        } else {
            for (MenuItem item : menuItems) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredMenuItems.add(item);
                }
            }
        }

        menuAdapter.notifyDataSetChanged();
    }

    private void addSampleMenuItems() {
        // Add sample menu items
        MenuItem item1 = new MenuItem();
        item1.setName("Fried Rice");
        item1.setPrice(13.00);
        item1.setDescription("Delicious fried rice with vegetables and chicken");
        item1.setImageUrl("");
        item1.setCategory("Main Course");
        databaseHelper.addMenuItem(item1);

        MenuItem item2 = new MenuItem();
        item2.setName("Nasi Lemak");
        item2.setPrice(9.50);
        item2.setDescription("Traditional Malaysian rice dish with sambal");
        item2.setImageUrl("");
        item2.setCategory("Main Course");
        databaseHelper.addMenuItem(item2);

        MenuItem item3 = new MenuItem();
        item3.setName("Chicken Rendang");
        item3.setPrice(15.00);
        item3.setDescription("Spicy and tender chicken in rich coconut sauce");
        item3.setImageUrl("");
        item3.setCategory("Main Course");
        databaseHelper.addMenuItem(item3);

        MenuItem item4 = new MenuItem();
        item4.setName("Satay");
        item4.setPrice(12.00);
        item4.setDescription("Grilled meat skewers with peanut sauce");
        item4.setImageUrl("");
        item4.setCategory("Appetizers");
        databaseHelper.addMenuItem(item4);

        MenuItem item5 = new MenuItem();
        item5.setName("Roti Canai");
        item5.setPrice(3.50);
        item5.setDescription("Crispy flatbread served with curry");
        item5.setImageUrl("");
        item5.setCategory("Appetizers");
        databaseHelper.addMenuItem(item5);

        MenuItem item6 = new MenuItem();
        item6.setName("Laksa");
        item6.setPrice(11.00);
        item6.setDescription("Spicy noodle soup with coconut milk");
        item6.setImageUrl("");
        item6.setCategory("Main Course");
        databaseHelper.addMenuItem(item6);

        Toast.makeText(this, "Sample menu items added!", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        loadMenuItems();
    }
}