package com.example.dineo.staff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.MenuItem;
import com.example.dineo.adapters.MenuAdapter;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class StaffMenuListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView btnAdd;
    private MenuAdapter adapter;
    private ArrayList<MenuItem> menuList = new ArrayList<>();
    private DatabaseReference menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu_list);

        menuRef = FirebaseDatabase.getInstance().getReference("menu_items");

        recyclerView = findViewById(R.id.recyclerStaffMenu);
        btnAdd = findViewById(R.id.btnAddItem);

        adapter = new MenuAdapter(this, menuList, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddMenuItemActivity.class))
        );

        loadMenu();
    }

    private void loadMenu() {
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                menuList.clear();
                for (DataSnapshot s : snap.getChildren()) {
                    MenuItem item = s.getValue(MenuItem.class);
                    if (item != null)
                        menuList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}
