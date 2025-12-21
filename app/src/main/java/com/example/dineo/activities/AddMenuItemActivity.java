package com.example.dineo.staff;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;

public class AddMenuItemActivity extends AppCompatActivity {

    private ImageView imageViewPreview, imageViewUpload;
    private EditText editTextName, editTextDesc, editTextPrice;
    private Spinner spinnerCategory;
    private Button btnSave, btnDiscard;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        dbHelper = new DatabaseHelper(this);

        imageViewPreview = findViewById(R.id.imageViewPreview);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        editTextName = findViewById(R.id.editTextItemName);
        editTextDesc = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSaveItem);
        btnDiscard = findViewById(R.id.btnDiscardChanges);

        imageViewUpload.setOnClickListener(v -> {
            // TODO: Implement image picker and set imageViewPreview
            Toast.makeText(this, "Image picker not implemented", Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(v -> saveMenuItem());
        btnDiscard.setOnClickListener(v -> finish());
    }

    private void saveMenuItem() {
        String name = editTextName.getText().toString().trim();
        String desc = editTextDesc.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try { price = Double.parseDouble(priceStr); }
        catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageBase64 = null; // TODO: convert imageViewPreview to Base64 if available

        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(desc);
        item.setPrice(price);
        item.setCategory(category);
        item.setImageUrl(imageBase64);

        long id = dbHelper.addMenuItem(item);
        if (id > 0) {
            Toast.makeText(this, "Menu item added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Add failed", Toast.LENGTH_SHORT).show();
        }
    }
}
