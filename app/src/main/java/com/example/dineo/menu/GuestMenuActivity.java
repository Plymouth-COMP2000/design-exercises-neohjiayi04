package com.example.dineo.menu;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.MenuAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;  // ✅ CORRECT IMPORT
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class GuestMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private DatabaseHelper databaseHelper;
    private EditText searchEditText;
    private ChipGroup categoryChipGroup;
    private List<MenuItem> allMenuItems;
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_menu);

        initializeViews();
        setupRecyclerView();
        setupSearch();
        setupCategories();
        loadMenuItems();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.menu_recycler_view);
        searchEditText = findViewById(R.id.search_edit_text);
        categoryChipGroup = findViewById(R.id.category_chip_group);
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MenuAdapter(this, null);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(GuestMenuActivity.this, MenuDetailActivity.class);
            intent.putExtra("menu_item_id", item.getId());
            startActivity(intent);
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenuItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCategories() {
        Chip allChip = new Chip(this);
        allChip.setText("All");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            selectedCategory = "All";
            filterMenuItems(searchEditText.getText().toString());
        });
        categoryChipGroup.addView(allChip);

        List<String> categories = databaseHelper.getAllCategories();
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedCategory = category;
                filterMenuItems(searchEditText.getText().toString());
            });
            categoryChipGroup.addView(chip);
        }

        categoryChipGroup.setSingleSelection(true);
    }

    private void loadMenuItems() {
        allMenuItems = databaseHelper.getAllMenuItems();
        adapter.updateItems(allMenuItems);
    }

    private void filterMenuItems(String query) {
        List<MenuItem> filteredItems;

        if (selectedCategory.equals("All")) {
            if (query.isEmpty()) {
                filteredItems = allMenuItems;
            } else {
                filteredItems = databaseHelper.searchMenuItems(query);
            }
        } else {
            filteredItems = databaseHelper.getMenuItemsByCategory(selectedCategory);
            if (!query.isEmpty()) {
                filteredItems.removeIf(item ->
                        !item.getName().toLowerCase().contains(query.toLowerCase()) &&
                                !item.getDescription().toLowerCase().contains(query.toLowerCase())
                );
            }
        }

        adapter.updateItems(filteredItems);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
        setupCategories();
        filterMenuItems(searchEditText.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }


}