package com.example.dineo.menu;

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
import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddMenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Button uploadImageButton;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private Spinner categorySpinner;
    private Button saveButton;
    private Button discardButton;

    private DatabaseHelper databaseHelper;
    private String imagePath = "";
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        initializeViews();
        setupSpinner();
        setupListeners();
    }

    private void initializeViews() {
        imageView = findViewById(R.id.menu_image);
        uploadImageButton = findViewById(R.id.btn_upload_image);
        nameEditText = findViewById(R.id.edit_item_name);
        descriptionEditText = findViewById(R.id.edit_description);
        priceEditText = findViewById(R.id.edit_price);
        categorySpinner = findViewById(R.id.spinner_category);
        saveButton = findViewById(R.id.btn_save);
        discardButton = findViewById(R.id.btn_discard);
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupSpinner() {
        String[] categories = {"Rice", "Noodles", "Meat", "Vegetables", "Appetizers", "Desserts", "Drinks"}; // ✅ FIXED
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        uploadImageButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveMenuItem());
        discardButton.setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                imagePath = saveImageToInternalStorage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = new File(getFilesDir(), "menu_images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "menu_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void saveMenuItem() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            nameEditText.setError("Please enter item name");
            nameEditText.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            priceEditText.setError("Please enter price");
            priceEditText.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            priceEditText.setError("Invalid price format");
            priceEditText.requestFocus();
            return;
        }

        MenuItem menuItem = new MenuItem(name, description, price, category, imagePath);
        long id = databaseHelper.addMenuItem(menuItem);

        if (id > 0) {
            Toast.makeText(this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add menu item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}