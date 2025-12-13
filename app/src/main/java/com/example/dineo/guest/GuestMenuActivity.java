package com.example.dineo.guest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.MenuAdapter;
import com.example.dineo.models.MenuItem;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class GuestMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etSearch;

    private MenuAdapter adapter;
    private DatabaseReference menuRef;

    private List<MenuItem> allItems = new ArrayList<>();
    private List<MenuItem> filteredItems = new ArrayList<>();

    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_menu);

        recyclerView = findViewById(R.id.recyclerMenu);
        etSearch = findViewById(R.id.etSearch);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new MenuAdapter(this, filteredItems, item -> openFoodDetail(item));
        recyclerView.setAdapter(adapter);

        menuRef = FirebaseDatabase.getInstance().getReference("menu_items");

        setupSearch();
        loadMenuFromFirebase();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
        });
    }

    private void loadMenuFromFirebase() {
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allItems.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    MenuItem item = s.getValue(MenuItem.class);
                    if (item != null) {
                        item.setId(s.getKey());
                        allItems.add(item);
                    }
                }

                filteredItems = new ArrayList<>(allItems);
                adapter.updateList(filteredItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filterList(String query) {

        String q = query.toLowerCase();
        filteredItems.clear();

        for (MenuItem item : allItems) {

            boolean matchSearch = item.getName().toLowerCase().contains(q);
            boolean matchCategory = selectedCategory.equals("All") ||
                    item.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchSearch && matchCategory) {
                filteredItems.add(item);
            }
        }

        adapter.updateList(filteredItems);
    }

    private void openFoodDetail(MenuItem item) {
        Intent intent = new Intent(this, GuestFoodActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("name", item.getName());
        intent.putExtra("price", item.getPrice());
        intent.putExtra("desc", item.getDescription());
        intent.putExtra("img", item.getImageUrl());
        startActivity(intent);
    }
}
