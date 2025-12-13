package com.example.dineo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CustomerMenuActivity extends AppCompatActivity {
    private RecyclerView rvMenu;
    private CustomerMenuAdapter adapter;
    private EditText etSearch;
    private DatabaseReference menuRef;
    private List<MenuItem> allItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_menu);

        initViews();
        setupFirebase();
        setupRecyclerView();
        setupSearch();
        loadMenuItems();
    }

    private void initViews() {
        rvMenu = findViewById(R.id.rvMenu);
        etSearch = findViewById(R.id.etSearch);
        allItems = new ArrayList<>();
    }

    private void setupFirebase() {
        menuRef = FirebaseDatabase.getInstance().getReference("menu_items");
    }

    private void setupRecyclerView() {
        adapter = new CustomerMenuAdapter(this);
        rvMenu.setLayoutManager(new GridLayoutManager(this, 2));
        rvMenu.setAdapter(adapter);
    }

    private void setupSearch() {
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
                        allItems.add(item);
                    }
                }
                adapter.setMenuItems(allItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filterMenuItems(String query) {
        if (query.isEmpty()) {
            adapter.setMenuItems(allItems);
            return;
        }

        List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : allItems) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.setMenuItems(filtered);
    }
}
