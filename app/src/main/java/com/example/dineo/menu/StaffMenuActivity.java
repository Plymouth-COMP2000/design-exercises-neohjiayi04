package com.example.dineo.menu;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.StaffMenuAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StaffMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StaffMenuAdapter adapter;
    private DatabaseHelper databaseHelper;
    private EditText searchEditText;
    private ChipGroup categoryChipGroup;
    private FloatingActionButton fabAdd;
    private List<MenuItem> allMenuItems;
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);

        initializeViews();
        setupRecyclerView();
        setupSearch();
        setupCategories();
        loadMenuItems();
        setupFab();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.menu_recycler_view);
        searchEditText = findViewById(R.id.search_edit_text);
        categoryChipGroup = findViewById(R.id.category_chip_group);
        fabAdd = findViewById(R.id.fab_add_menu);
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StaffMenuAdapter(this, null);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemActionListener(new StaffMenuAdapter.OnItemActionListener() {
            @Override
            public void onEditClick(MenuItem item) {
                Intent intent = new Intent(StaffMenuActivity.this, EditMenuItemActivity.class);
                intent.putExtra("menu_item_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(MenuItem item) {
                showDeleteConfirmationDialog(item);
            }
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
        categoryChipGroup.removeAllViews();

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

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(StaffMenuActivity.this, AddMenuItemActivity.class);
            startActivity(intent);
        });
    }

    private void loadMenuItems() {
        allMenuItems = databaseHelper.getAllMenuItems(); // MUST return List<com.example.dineo.models.MenuItem>
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

    private void showDeleteConfirmationDialog(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Menu Item")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteMenuItem(item.getId());
                    Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
                    loadMenuItems();
                    setupCategories();
                    filterMenuItems(searchEditText.getText().toString());
                })
                .setNegativeButton("Cancel", null)
                .show();
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
