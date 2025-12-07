package com.example.dineo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LaunchActivity extends AppCompatActivity {

    private MaterialButton btnCustomer;
    private MaterialButton btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Initialize views
        btnCustomer = findViewById(R.id.btnCustomer);
        btnAdmin = findViewById(R.id.btnAdmin);

        // Set click listeners
        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login as Customer
                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                intent.putExtra("USER_TYPE", "customer");
                startActivity(intent);
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login as Admin
                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                intent.putExtra("USER_TYPE", "admin");
                startActivity(intent);
            }
        });
    }
}