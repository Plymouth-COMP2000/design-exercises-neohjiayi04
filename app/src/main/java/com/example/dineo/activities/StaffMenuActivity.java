package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.StaffMenuAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Staff Menu Activity - Manage all menu items (view, edit, delete)
 * Student ID: BSSE2506008
 */
public class StaffMenuActivity extends AppCompatActivity implements StaffMenuAdapter.OnMenuItemActionListener {

    private SearchView searchView;
    private RecyclerView recyclerViewMenu;
    private FloatingActionButton fabAddItem;
    private ImageView imageViewNotification;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private StaffMenuAdapter menuAdapter;
    private List<MenuItem> allMenuItems;
    private List<MenuItem> filteredMenuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        // Initialize views
        searchView = findViewById(R.id.searchView);
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        fabAddItem = findViewById(R.id.fabAddItem);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));

        // Load menu items
        loadMenuItems();

        // Setup search
        setupSearch();

        // Setup FAB
        fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(StaffMenuActivity.this, AddMenuItemActivity.class);
            startActivity(intent);
        });

        // Setup notification bell
        imageViewNotification.setOnClickListener(v -> {
            Intent intent = new Intent(StaffMenuActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void loadMenuItems() {
        allMenuItems = databaseHelper.getAllMenuItems();
        filteredMenuItems = new ArrayList<>(allMenuItems);

        menuAdapter = new StaffMenuAdapter(this, filteredMenuItems, this);
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

    @Override
    public void onEditClick(MenuItem menuItem) {
        Intent intent = new Intent(this, EditMenuItemActivity.class);
        intent.putExtra("MENU_ITEM_ID", menuItem.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(MenuItem menuItem) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Menu Item")
                .setMessage("Are you sure you want to delete " + menuItem.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteMenuItem(menuItem.getId());
                    Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
                    loadMenuItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(StaffMenuActivity.this, StaffDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_menu) {
                return true;
            } else if (itemId == R.id.nav_reservation) {
                startActivity(new Intent(StaffMenuActivity.this, StaffReservationActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(StaffMenuActivity.this, ProfileActivity.class));
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