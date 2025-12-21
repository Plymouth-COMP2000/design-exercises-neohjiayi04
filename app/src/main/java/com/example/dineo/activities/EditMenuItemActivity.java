package com.example.dineo.staff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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

public class EditMenuItemActivity extends AppCompatActivity {

    private ImageView imageViewPreview, imageViewUpload;
    private EditText editTextName, editTextDesc, editTextPrice;
    private Spinner spinnerCategory;
    private Button btnSave, btnDelete;
    private DatabaseHelper dbHelper;
    private int menuItemId;
    private MenuItem currentItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_item);

        dbHelper = new DatabaseHelper(this);

        imageViewPreview = findViewById(R.id.imageViewPreview);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        editTextName = findViewById(R.id.editTextItemName);
        editTextDesc = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSaveItem);
        btnDelete = findViewById(R.id.btnDiscardChanges); // reuse button as delete

        menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);
        if (menuItemId == -1) {
            Toast.makeText(this, "Invalid menu item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMenuItem();

        imageViewUpload.setOnClickListener(v -> {
            // TODO: Implement image picker
            Toast.makeText(this, "Image picker not implemented", Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(v -> saveChanges());
        btnDelete.setOnClickListener(v -> deleteItem());
    }

    private void loadMenuItem() {
        try {
            for (MenuItem item : dbHelper.getAllMenuItems()) {
                if (item.getId() == menuItemId) {
                    currentItem = item;
                    break;
                }
            }
            if (currentItem == null) {
                Toast.makeText(this, "Menu item not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            editTextName.setText(currentItem.getName());
            editTextDesc.setText(currentItem.getDescription());
            editTextPrice.setText(String.valueOf(currentItem.getPrice()));
            // Spinner selection
            String[] categories = {"Main Food", "Appetizers", "Desserts", "Beverages", "Specials"};
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(currentItem.getCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }

            // Load image
            if (currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()) {
                try {
                    byte[] decoded = Base64.decode(currentItem.getImageUrl(), Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                    imageViewPreview.setImageBitmap(bmp);
                    imageViewPreview.setVisibility(View.VISIBLE);
                    imageViewUpload.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        String name = editTextName.getText().toString().trim();
        String desc = editTextDesc.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageBase64 = currentItem.getImageUrl(); // keep existing image
        MenuItem updatedItem = new MenuItem();
        updatedItem.setId(menuItemId);
        updatedItem.setName(name);
        updatedItem.setDescription(desc);
        updatedItem.setPrice(price);
        updatedItem.setCategory(category);
        updatedItem.setImageUrl(imageBase64);

        int rows = dbHelper.updateMenuItem(updatedItem);
        if (rows > 0) {
            Toast.makeText(this, "Menu item updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem() {
        int rows = dbHelper.deleteMenuItem(menuItemId);
        if (rows > 0) {
            Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }
}