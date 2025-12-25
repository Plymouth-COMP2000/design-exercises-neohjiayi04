package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MenuListActivity extends AppCompatActivity {
    private RecyclerView rvMenuItems;
    private MenuAdapter adapter;
    private FloatingActionButton fabAdd;
    private EditText etSearch;
    private DatabaseReference menuRef;
    private List<MenuItem> allItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        initViews();
        setupFirebase();
        setupRecyclerView();
        setupListeners();
        loadMenuItems();
    }

    private void initViews() {
        rvMenuItems = findViewById(R.id.rvMenuItems);
        fabAdd = findViewById(R.id.fabAdd);
        etSearch = findViewById(R.id.etSearch);
        allItems = new ArrayList<>();
    }

    private void setupFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        menuRef = database.getReference("menu_items");
    }

    private void setupRecyclerView() {
        adapter = new MenuAdapter(this, new MenuAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(MenuItem item) {
                openEditActivity(item);
            }

            @Override
            public void onDeleteClick(MenuItem item) {
                showDeleteDialog(item);
            }
        });

        rvMenuItems.setLayoutManager(new LinearLayoutManager(this));
        rvMenuItems.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MenuListActivity.this, AddMenuItemActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
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

    private void loadMenuItems() {
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    MenuItem item = itemSnapshot.getValue(MenuItem.class);
                    if (item != null) {
                        item.setId(itemSnapshot.getKey());
                        allItems.add(item);
                    }
                }
                adapter.setMenuItems(allItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuListActivity.this,
                        "Failed to load menu items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterMenuItems(String query) {
        if (query.isEmpty()) {
            adapter.setMenuItems(allItems);
            return;
        }

        List<MenuItem> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (MenuItem item : allItems) {
            if (item.getName().toLowerCase().contains(lowerQuery) ||
                    item.getCategory().toLowerCase().contains(lowerQuery)) {
                filtered.add(item);
            }
        }
        adapter.setMenuItems(filtered);
    }

    private void openEditActivity(MenuItem item) {
        Intent intent = new Intent(MenuListActivity.this, EditMenuItemActivity.class);
        intent.putExtra("item_id", item.getId());
        intent.putExtra("item_name", item.getName());
        intent.putExtra("item_description", item.getDescription());
        intent.putExtra("item_price", item.getPrice());
        intent.putExtra("item_category", item.getCategory());
        intent.putExtra("item_image", item.getImageUrl());
        startActivity(intent);
    }

    private void showDeleteDialog(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem(MenuItem item) {
        menuRef.child(item.getId()).removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show());
    }
}
