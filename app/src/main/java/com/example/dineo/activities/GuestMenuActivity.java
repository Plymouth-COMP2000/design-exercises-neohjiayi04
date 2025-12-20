package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * GuestMenuActivity - Browse menu items with Executor for background DB loading
 * Student ID: BSSE2506008
 */
public class GuestMenuActivity extends AppCompatActivity {

    private static final String TAG = "GuestMenuActivity";

    private ImageView imageViewNotification;
    private EditText editTextSearch;
    private RecyclerView recyclerViewMenu;
    private BottomNavigationView bottomNavigationView;

    private Button btnCategoryAll, btnCategoryMain, btnCategoryDesserts, btnCategoryBeverages;

    private DatabaseHelper databaseHelper;
    private MenuAdapter menuAdapter;
    private List<MenuItem> menuItems = new ArrayList<>();
    private List<MenuItem> filteredMenuItems = new ArrayList<>();

    private String currentCategory = "All";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Log.d(TAG, "GuestMenuActivity started");

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

        // Setup RecyclerView
        recyclerViewMenu.setLayoutManager(new GridLayoutManager(this, 2));
        menuAdapter = new MenuAdapter(this, filteredMenuItems, menuItem -> {
            try {
                Intent intent = new Intent(GuestMenuActivity.this, MenuDetailActivity.class);
                intent.putExtra("MENU_ITEM_ID", menuItem.getId());
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error opening menu detail: " + e.getMessage());
            }
        });
        recyclerViewMenu.setAdapter(menuAdapter);

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
            try {
                startActivity(new Intent(GuestMenuActivity.this, NotificationActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "NotificationActivity not found");
            }
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
        int orangeColor = 0xFFFF6B35;

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
        executor.execute(() -> {
            List<MenuItem> items = databaseHelper.getAllMenuItems();

            // Add sample items if database is empty
            if (items.isEmpty()) {
                addSampleMenuItems();
                items = databaseHelper.getAllMenuItems();
            }

            final List<MenuItem> loadedItems = items;
            runOnUiThread(() -> {
                menuItems.clear();
                menuItems.addAll(loadedItems);
                filterMenuItems(editTextSearch.getText().toString());
                Log.d(TAG, "Menu items loaded: " + menuItems.size());
            });
        });
    }

    private void filterMenuItems(String query) {
        filteredMenuItems.clear();

        for (MenuItem item : menuItems) {
            boolean matchesSearch = item.getName() != null &&
                    item.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = "All".equals(currentCategory) ||
                    (item.getCategory() != null && item.getCategory().equals(currentCategory));

            if (matchesSearch && matchesCategory) {
                filteredMenuItems.add(item);
            }
        }

        menuAdapter.notifyDataSetChanged();
        Log.d(TAG, "Filtered to " + filteredMenuItems.size() + " items");
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_menu) {
                return true;
            } else if (id == R.id.nav_reservation) {
                try {
                    startActivity(new Intent(this, ReservationActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "ReservationActivity not found");
                }
                return true;
            } else if (id == R.id.nav_profile) {
                try {
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "ProfileActivity not found");
                }
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

    private void addSampleMenuItems() {
        addMenuItem("Nasi Goreng", 13.00, "Traditional Malaysian fried rice with egg and vegetables",
                "Main Food", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400");
        addMenuItem("Satay Ayam", 12.00, "Grilled chicken skewers with peanut sauce",
                "Main Food", "https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=400");
        addMenuItem("Roti Canai", 5.00, "Flaky flatbread served with curry",
                "Main Food", "https://images.unsplash.com/photo-1567337710282-00832b415979?w=400");
        addMenuItem("Ice Cream", 6.00, "Creamy vanilla ice cream with chocolate sauce",
                "Desserts", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400");
        addMenuItem("Cendol", 5.00, "Shaved ice dessert with coconut milk and palm sugar",
                "Desserts", "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=400");
        addMenuItem("Teh Tarik", 3.50, "Traditional Malaysian pulled milk tea",
                "Beverages", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400");
        addMenuItem("Milo Ais", 4.00, "Iced chocolate malt drink",
                "Beverages", "https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=400");
        addMenuItem("Fresh Juice", 5.50, "Freshly squeezed orange juice",
                "Beverages", "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400");
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
