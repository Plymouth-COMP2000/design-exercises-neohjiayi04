package com.example.dineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.MenuAdapter;
import com.example.dineo.models.MenuItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class GuestMenuActivity extends BaseActivity implements MenuAdapter.OnMenuItemClickListener {

    private RecyclerView recyclerViewMenu;
    private MenuAdapter adapter;
    private List<MenuItem> allMenuItems;

    private EditText editTextSearch;
    private MaterialButton btnCategoryAll, btnCategoryMain, btnCategoryDesserts, btnCategoryBeverages;
    private ImageView imageViewNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Bottom navigation active
        setupBottomNavigation(R.id.nav_menu);

        // Notification icon
        imageViewNotification = findViewById(R.id.imageViewNotification);
        imageViewNotification.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class))
        );

        // Category buttons
        btnCategoryAll = findViewById(R.id.btnCategoryAll);
        btnCategoryMain = findViewById(R.id.btnCategoryMain);
        btnCategoryDesserts = findViewById(R.id.btnCategoryDesserts);
        btnCategoryBeverages = findViewById(R.id.btnCategoryBeverages);

        // Search
        editTextSearch = findViewById(R.id.editTextSearch);

        // RecyclerView
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        recyclerViewMenu.setLayoutManager(new GridLayoutManager(this, 2));

        // Load menu
        allMenuItems = new ArrayList<>();
        loadSampleMenu();

        adapter = new MenuAdapter(this, new ArrayList<>(allMenuItems), this);
        recyclerViewMenu.setAdapter(adapter);

        setupCategoryButtons();
        setupSearchFilter();
    }

    private void setupCategoryButtons() {
        btnCategoryAll.setOnClickListener(v -> { filterMenuByCategory("All"); setActiveCategoryButton(btnCategoryAll); });
        btnCategoryMain.setOnClickListener(v -> { filterMenuByCategory("Main"); setActiveCategoryButton(btnCategoryMain); });
        btnCategoryDesserts.setOnClickListener(v -> { filterMenuByCategory("Desserts"); setActiveCategoryButton(btnCategoryDesserts); });
        btnCategoryBeverages.setOnClickListener(v -> { filterMenuByCategory("Beverages"); setActiveCategoryButton(btnCategoryBeverages); });

        // Default active
        setActiveCategoryButton(btnCategoryAll);
    }

    private void setupSearchFilter() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenuByKeyword(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setActiveCategoryButton(MaterialButton activeButton) {
        MaterialButton[] buttons = {btnCategoryAll, btnCategoryMain, btnCategoryDesserts, btnCategoryBeverages};
        for (MaterialButton btn : buttons) {
            btn.setBackgroundTintList(getColorStateList(R.color.category_inactive));
            btn.setTextColor(getColor(R.color.category_inactive_text));
        }
        activeButton.setBackgroundTintList(getColorStateList(R.color.category_active));
        activeButton.setTextColor(getColor(R.color.category_active_text));
    }

    private void filterMenuByCategory(String category) {
        List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : allMenuItems) {
            if (category.equalsIgnoreCase("All") || item.getCategory().equalsIgnoreCase(category)) {
                filtered.add(item);
            }
        }
        adapter.setMenuItems(filtered);
    }

    private void filterMenuByKeyword(String keyword) {
        List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : allMenuItems) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    item.getCategory().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.setMenuItems(filtered);
    }

    private void loadSampleMenu() {
        addMenuItem("Nasi Goreng", 13.0, "Traditional Malaysian fried rice with egg and vegetables", "Main", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400");
        addMenuItem("Satay Ayam", 12.0, "Grilled chicken skewers with peanut sauce", "Main", "https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=400");
        addMenuItem("Roti Canai", 5.0, "Flaky flatbread served with curry", "Main", "https://images.unsplash.com/photo-1567337710282-00832b415979?w=400");
        addMenuItem("Ice Cream", 6.0, "Creamy vanilla ice cream with chocolate sauce", "Desserts", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400");
        addMenuItem("Cendol", 5.0, "Shaved ice dessert with coconut milk and palm sugar", "Desserts", "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=400");
        addMenuItem("Teh Tarik", 3.5, "Traditional Malaysian pulled milk tea", "Beverages", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400");
        addMenuItem("Milo Ais", 4.0, "Iced chocolate malt drink", "Beverages", "https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=400");
        addMenuItem("Fresh Juice", 5.5, "Freshly squeezed orange juice", "Beverages", "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400");
    }

    private void addMenuItem(String name, double price, String description, String category, String imageUrl) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        item.setCategory(category);
        item.setImageUrl(imageUrl);
        allMenuItems.add(item);
    }

    @Override
    public void onMenuItemClick(MenuItem menuItem) {
        Intent intent = new Intent(this, MenuDetailActivity.class);
        intent.putExtra("menuItemName", menuItem.getName());
        intent.putExtra("menuItemPrice", menuItem.getPrice());
        intent.putExtra("menuItemDescription", menuItem.getDescription());
        intent.putExtra("menuItemCategory", menuItem.getCategory());
        intent.putExtra("menuItemImageUrl", menuItem.getImageUrl());
        startActivity(intent);
    }
}
