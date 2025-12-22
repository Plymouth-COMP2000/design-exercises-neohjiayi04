package com.example.dineo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * Edit Menu Item Activity - Staff Only
 * Allows staff to edit existing menu items
 * NOW WITH STAFF NAVIGATION! ✅
 */
public class EditMenuItemActivity extends StaffBaseActivity {

    private static final int PICK_IMAGE = 1;

    private DatabaseHelper dbHelper;
    private ImageView imageViewItem, imageViewBack;
    private EditText editTextName, editTextDescription, editTextPrice;
    private Spinner spinnerCategory;
    private Button btnSelectImage, btnSaveItem, btnDeleteItem;
    private byte[] imageBytes;
    private int menuItemId;
    private MenuItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_item);

        // ✅ FIXED: Added staff bottom navigation
        setupStaffBottomNavigation(R.id.nav_menu_staff);

        dbHelper = new DatabaseHelper(this);

        // Get menu item ID from intent
        menuItemId = getIntent().getIntExtra("menu_item_id", -1);
        if (menuItemId == -1) {
            Toast.makeText(this, "Error loading menu item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        imageViewItem = findViewById(R.id.imageViewMenuItem);
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextName = findViewById(R.id.editTextItemName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSelectImage = findViewById(R.id.imageViewUpload);
        btnSaveItem = findViewById(R.id.btnSaveItem);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);

        // Setup category spinner
        String[] categories = {"Appetizer", "Main Course", "Dessert", "Beverage", "Side Dish", "Special"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Load menu item data
        loadMenuItem();

        // Back button
        imageViewBack.setOnClickListener(v -> finish());

        // Select image button
        btnSelectImage.setOnClickListener(v -> selectImage());

        // Save button
        btnSaveItem.setOnClickListener(v -> saveMenuItem());

        // Delete button
        btnDeleteItem.setOnClickListener(v -> deleteMenuItem());
    }

    private void loadMenuItem() {
        currentItem = dbHelper.getMenuItemById(menuItemId);
        if (currentItem == null) {
            Toast.makeText(this, "Menu item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields
        editTextName.setText(currentItem.getName());
        editTextDescription.setText(currentItem.getDescription());
        editTextPrice.setText(String.valueOf(currentItem.getPrice()));

        // Set category spinner
        String category = currentItem.getCategory();
        String[] categories = {"Appetizer", "Main Course", "Dessert", "Beverage", "Side Dish", "Special"};
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        // Load image
        imageBytes = currentItem.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageViewItem.setImageBitmap(bitmap);
        }
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

        // Update menu item
        currentItem.setName(name);
        currentItem.setDescription(description);
        currentItem.setPrice(price);
        currentItem.setCategory(category);
        currentItem.setImage(imageBytes);

        // Update in database
        int result = dbHelper.updateMenuItem(currentItem);

        if (result > 0) {
            Toast.makeText(this, "Menu item updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating menu item", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMenuItem() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Menu Item")
                .setMessage("Are you sure you want to delete this menu item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = dbHelper.deleteMenuItem(menuItemId);
                    if (result > 0) {
                        Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error deleting menu item", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}