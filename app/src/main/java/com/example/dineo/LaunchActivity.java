package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LaunchActivity extends AppCompatActivity {

    private MaterialButton btnCustomer;
    private MaterialButton btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnCustomer = findViewById(R.id.btnCustomer);
        btnAdmin = findViewById(R.id.btnAdmin);
    }

    private void setupClickListeners() {
        btnCustomer.setOnClickListener(v -> {
            // Navigate to Login page for Customer
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            intent.putExtra("USER_TYPE", "customer");
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            // Navigate to Login page for Admin
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            intent.putExtra("USER_TYPE", "admin");
            startActivity(intent);
        });
    }
}