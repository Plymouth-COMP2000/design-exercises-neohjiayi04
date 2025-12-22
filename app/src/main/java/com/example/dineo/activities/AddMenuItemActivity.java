package com.example.dineo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.dineo.R;
import com.example.dineo.activities.StaffBaseActivity;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

import java.io.ByteArrayOutputStream;

public class AddMenuItemActivity extends StaffBaseActivity {

    private static final int PICK_IMAGE = 1;

    private DatabaseHelper dbHelper;
    private ImageView imageViewItem, imageViewBack;
    private EditText editTextName, editTextDescription, editTextPrice;
    private Spinner spinnerCategory;
    private Button btnSelectImage, btnSaveItem;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        // ✅ FIXED: Added staff bottom navigation
        setupStaffBottomNavigation(R.id.nav_menu_staff);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        imageViewItem = findViewById(R.id.imageViewMenuItem);
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextName = findViewById(R.id.editTextItemName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSelectImage = findViewById(R.id.imageViewUpload);
        btnSaveItem = findViewById(R.id.btnSaveItem);

        // Setup category spinner
        String[] categories = {"Appetizer", "Main Course", "Dessert", "Beverage", "Side Dish", "Special"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Back button
        imageViewBack.setOnClickListener(v -> finish());

        // Select image button
        btnSelectImage.setOnClickListener(v -> selectImage());

        // Save button
        btnSaveItem.setOnClickListener(v -> saveMenuItem());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewItem.setImageBitmap(bitmap);

                // Convert to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                imageBytes = stream.toByteArray();

            } catch (Exception e) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveMenuItem() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validation
        if (name.isEmpty()) {
            editTextName.setError("Name required");
            return;
        }

        if (priceStr.isEmpty()) {
            editTextPrice.setError("Price required");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            editTextPrice.setError("Invalid price");
            return;
        }

        if (imageBytes == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create menu item
        MenuItem menuItem = new MenuItem(0, name, description, price, category, imageBytes);

        // Save to database
        long result = dbHelper.addMenuItem(menuItem);

        if (result != -1) {
            Toast.makeText(this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding menu item", Toast.LENGTH_SHORT).show();
        }
    }
}