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

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

/**
 * Add Menu Item Activity - Staff can add new menu items
 * Student ID: BSSE2506008
 */
public class AddMenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewBack, imageViewUpload, imageViewPreview;
    private EditText editTextItemName, editTextDescription, editTextPrice;
    private Spinner spinnerCategory;
    private Button btnSaveItem, btnDiscardChanges;

    private DatabaseHelper databaseHelper;
    private Uri imageUri;

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
        String[] categories = {"Appetizers", "Main Course", "Burgers", "Desserts", "Beverages", "Specials"};
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
            imageUri = data.getData();
            imageViewPreview.setImageURI(imageUri);
            imageViewPreview.setVisibility(View.VISIBLE);
        }
    }

    private void saveMenuItem() {
        String name = editTextItemName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

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

        // Create menu item
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setImageUrl(imageUri != null ? imageUri.toString() : "");

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