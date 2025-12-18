package com.example.dineo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

import java.util.List;

/**
 * Edit Menu Item Activity - Staff can edit existing menu items
 * Student ID: BSSE2506008
 */
public class EditMenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewBack, imageViewChangeImage, imageViewPreview;
    private EditText editTextItemName, editTextDescription, editTextPrice;
    private Spinner spinnerCategory;
    private Button btnSaveItem, btnDeleteItem;

    private DatabaseHelper databaseHelper;
    private MenuItem currentMenuItem;
    private Uri imageUri;
    private int menuItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_item);

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewChangeImage = findViewById(R.id.imageViewChangeImage);
        imageViewPreview = findViewById(R.id.imageViewPreview);
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveItem = findViewById(R.id.btnSaveItem);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Get menu item ID
        menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);

        // Setup category spinner
        setupCategorySpinner();

        // Load menu item data
        loadMenuItem();

        // Setup click listeners
        imageViewBack.setOnClickListener(v -> finish());

        imageViewChangeImage.setOnClickListener(v -> selectImage());

        btnSaveItem.setOnClickListener(v -> saveChanges());

        btnDeleteItem.setOnClickListener(v -> confirmDelete());
    }

    private void setupCategorySpinner() {
        String[] categories = {"Appetizers", "Main Course", "Burgers", "Desserts", "Beverages", "Specials"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadMenuItem() {
        // In real app, get by ID from database
        // For now, we'll create a sample
        if (menuItemId != -1) {
            // Load from database
            List<MenuItem> items = databaseHelper.getAllMenuItems();
            for (MenuItem item : items) {
                if (item.getId() == menuItemId) {
                    currentMenuItem = item;
                    break;
                }
            }

            if (currentMenuItem != null) {
                editTextItemName.setText(currentMenuItem.getName());
                editTextDescription.setText(currentMenuItem.getDescription());
                editTextPrice.setText(String.valueOf(currentMenuItem.getPrice()));

                // Load image if exists
                if (currentMenuItem.getImageUrl() != null && !currentMenuItem.getImageUrl().isEmpty()) {
                    imageUri = Uri.parse(currentMenuItem.getImageUrl());
                    imageViewPreview.setImageURI(imageUri);
                }
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageViewPreview.setImageURI(imageUri);
        }
    }

    private void saveChanges() {
        String name = editTextItemName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();

        // Validate
        if (name.isEmpty()) {
            editTextItemName.setError("Item name is required");
            return;
        }

        if (priceStr.isEmpty()) {
            editTextPrice.setError("Price is required");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            editTextPrice.setError("Invalid price");
            return;
        }

        // Update menu item
        currentMenuItem.setName(name);
        currentMenuItem.setDescription(description);
        currentMenuItem.setPrice(price);
        if (imageUri != null) {
            currentMenuItem.setImageUrl(imageUri.toString());
        }

        // Save to database
        int result = databaseHelper.updateMenuItem(currentMenuItem);

        if (result > 0) {
            Toast.makeText(this, "Menu item updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update menu item", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Menu Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteMenuItem(menuItemId);
                    Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}