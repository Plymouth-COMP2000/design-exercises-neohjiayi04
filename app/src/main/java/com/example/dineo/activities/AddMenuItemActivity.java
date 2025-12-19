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

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Add Menu Item Activity - With Base64 Image Storage
 * Student ID: BSSE2506008
 */
public class AddMenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewBack, imageViewUpload, imageViewPreview;
    private EditText editTextItemName, editTextDescription, editTextPrice;
    private Spinner spinnerCategory;
    private Button btnSaveItem, btnDiscardChanges;

    private DatabaseHelper databaseHelper;
    private String base64Image = ""; // Store as Base64

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        // Initialize views
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        imageViewPreview = findViewById(R.id.imageViewPreview);
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveItem = findViewById(R.id.btnSaveItem);
        btnDiscardChanges = findViewById(R.id.btnDiscardChanges);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Setup category spinner
        setupCategorySpinner();

        // Setup click listeners
        imageViewBack.setOnClickListener(v -> finish());
        imageViewUpload.setOnClickListener(v -> selectImage());
        btnSaveItem.setOnClickListener(v -> saveMenuItem());
        btnDiscardChanges.setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        String[] categories = {"Main Food", "Appetizers", "Desserts", "Beverages", "Specials"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
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

                // Load bitmap from URI
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                // Resize bitmap to save space (max 800x800)
                bitmap = resizeBitmap(bitmap, 800, 800);

                // Convert to Base64
                base64Image = bitmapToBase64(bitmap);

                // Show preview
                imageViewPreview.setImageBitmap(bitmap);
                imageViewPreview.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Resize bitmap to max dimensions while maintaining aspect ratio
     */
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

    /**
     * Convert Bitmap to Base64 string
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream); // 80% quality
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void saveMenuItem() {
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
            editTextPrice.setError("Invalid price format");
            editTextPrice.requestFocus();
            return;
        }

        // Create menu item
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        menuItem.setImageUrl(base64Image); // Store Base64 string

        // Save to database
        long id = databaseHelper.addMenuItem(menuItem);

        if (id > 0) {
            Toast.makeText(this, "Menu item added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add menu item", Toast.LENGTH_SHORT).show();
        }
    }
}