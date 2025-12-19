package com.example.dineo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Edit Menu Item Activity - WITH Base64 Image Support
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
    private String base64Image = ""; // Store as Base64
    private boolean imageChanged = false;
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
        String[] categories = {"Main Food", "Appetizers", "Desserts", "Beverages", "Specials"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadMenuItem() {
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

                // Set category spinner
                String category = currentMenuItem.getCategory();
                if (category != null && !category.isEmpty()) {
                    ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
                    int position = adapter.getPosition(category);
                    if (position >= 0) {
                        spinnerCategory.setSelection(position);
                    }
                }

                // Load and display image
                String imageData = currentMenuItem.getImageUrl();
                if (imageData != null && !imageData.isEmpty()) {
                    base64Image = imageData; // Keep original

                    // Display image
                    if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
                        // It's a URL - we can't easily display it without network,
                        // so just show placeholder
                        imageViewPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                    } else {
                        // It's Base64 - decode and display
                        try {
                            byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            if (bitmap != null) {
                                imageViewPreview.setImageBitmap(bitmap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            imageViewPreview.setImageResource(android.R.drawable.ic_menu_gallery);
                        }
                    }
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
            try {
                Uri imageUri = data.getData();

                // Load bitmap
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                // Resize
                bitmap = resizeBitmap(bitmap, 800, 800);

                // Convert to Base64
                base64Image = bitmapToBase64(bitmap);
                imageChanged = true;

                // Show preview
                imageViewPreview.setImageBitmap(bitmap);
                imageViewPreview.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Image updated", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void saveChanges() {
        String name = editTextItemName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validate
        if (name.isEmpty()) {
            editTextItemName.setError("Item name is required");
            editTextItemName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            editTextPrice.setError("Price is required");
            editTextPrice.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                editTextPrice.setError("Price must be positive");
                editTextPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editTextPrice.setError("Invalid price");
            editTextPrice.requestFocus();
            return;
        }

        // Update menu item
        currentMenuItem.setName(name);
        currentMenuItem.setDescription(description);
        currentMenuItem.setPrice(price);
        currentMenuItem.setCategory(category);

        // Only update image if changed
        if (imageChanged) {
            currentMenuItem.setImageUrl(base64Image);
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
                    int result = databaseHelper.deleteMenuItem(menuItemId);
                    if (result > 0) {
                        Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}