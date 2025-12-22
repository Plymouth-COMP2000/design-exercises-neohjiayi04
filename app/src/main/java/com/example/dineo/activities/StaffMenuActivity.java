package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

public class StaffMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMenu;
    private StaffMenuAdapter adapter;
    private List<MenuItem> menuItemList;
    private DatabaseHelper dbHelper;
    private SearchView searchViewMenu;
    private Spinner spinnerCategoryFilter;
    private FloatingActionButton fabAddMenuItem;
    private ImageView imageViewNotification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        dbHelper = new DatabaseHelper(this);

        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        searchViewMenu = findViewById(R.id.searchViewMenu);
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        fabAddMenuItem = findViewById(R.id.fabAddMenuItem);

        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        menuItemList = new ArrayList<>();
        adapter = new StaffMenuAdapter(menuItemList, this, new StaffMenuAdapter.OnMenuItemActionListener() {
            @Override
            public void onEditClick(MenuItem item) {
                Intent intent = new Intent(StaffMenuActivity.this, EditMenuItemActivity.class);
                intent.putExtra("MENU_ITEM_ID", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(MenuItem item) {
                int rows = dbHelper.deleteMenuItem(item.getId());
                if (rows > 0) {
                    Toast.makeText(StaffMenuActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    loadMenuItems();
                } else {
                    Toast.makeText(StaffMenuActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerViewMenu.setAdapter(adapter);

        // ✅ Setup notification icon
        setupNotificationIcon();

        setupCategorySpinner();
        setupSearchView();
        setupFab();

        loadMenuItems();
    }

    /**
     * ✅ Setup notification icon click listener for Staff
     */
    private void setupNotificationIcon() {
        imageViewNotification = findViewById(R.id.imageViewNotification);
        if (imageViewNotification != null) {
            imageViewNotification.setOnClickListener(v ->
                    startActivity(new Intent(this, StaffNotificationActivity.class))
            );
        }
    }

    private void setupCategorySpinner() {
        String[] categories = {"All", "Main Food", "Appetizers", "Desserts", "Beverages", "Specials"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryFilter.setAdapter(spinnerAdapter);

        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterMenu();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearchView() {
        searchViewMenu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                filterMenu();
                return true;
            }
        });
    }

    private void setupFab() {
        fabAddMenuItem.setOnClickListener(v -> {
            startActivity(new Intent(StaffMenuActivity.this, AddMenuItemActivity.class));
        });
    }

    private void loadMenuItems() {
        menuItemList.clear();
        List<MenuItem> allItems = dbHelper.getAllMenuItems();
        if (allItems != null) menuItemList.addAll(allItems);
        adapter.notifyDataSetChanged();
    }

    private void filterMenu() {
        String query = searchViewMenu.getQuery().toString().trim().toLowerCase();
        String selectedCategory = spinnerCategoryFilter.getSelectedItem().toString();
        List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : dbHelper.getAllMenuItems()) {
            boolean matchesCategory = selectedCategory.equals("All") || item.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesQuery = item.getName().toLowerCase().contains(query);
            if (matchesCategory && matchesQuery) filtered.add(item);
        }
        menuItemList.clear();
        menuItemList.addAll(filtered);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
    }
}