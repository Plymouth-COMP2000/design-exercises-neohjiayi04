package com.example.dineo.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.google.android.material.button.MaterialButton;

public class RoleSelectionActivity extends AppCompatActivity {

    private MaterialButton btnCustomer, btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        MaterialButton btnCustomer = findViewById(R.id.btn_customer);
        MaterialButton btnAdmin = findViewById(R.id.btn_admin);

        btnCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("USER_ROLE", "customer");
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("USER_ROLE", "admin");
            startActivity(intent);
        });
    }
}