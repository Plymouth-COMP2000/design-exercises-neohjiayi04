package com.example.dineo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class EditMenuItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivBack, ivMenuItem;
    private Button btnChangeImage, btnSave;
    private EditText etItemName, etDescription, etPrice;
    private LinearLayout llCategory;
    private TextView tvCategory, tvDelete, tvTitle;

    private DatabaseReference menuRef;
    private StorageReference storageRef;
    private String itemId;
    private Uri imageUri;
    private String currentImageUrl;
    private String selectedCategory = "Burgers";
    private boolean isEditMode = false;

    private String[] categories = {"Appetizers", "Burgers", "Main Course",
            "Desserts", "Beverages", "Salads"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_item);

        initViews();
        setupFirebase();
        loadItemData();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivMenuItem = findViewById(R.id.ivMenuItem);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSave = findViewById(R.id.btnSave);
        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        llCategory = findViewById(R.id.llCategory);
        tvCategory = findViewById(R.id.tvCategory);
        tvDelete = findViewById(R.id.tvDelete);
        tvTitle = findViewById(R.id.tvTitle);
    }

    private void setupFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        menuRef = database.getReference("menu_items");
        storageRef = FirebaseStorage.getInstance().getReference("menu_images");
    }

    private void loadItemData() {
        Intent intent = getIntent();
        itemId = intent.getStringExtra("item_id");

        if (itemId != null) {
            isEditMode = true;
            tvTitle.setText("Edit Menu Item");
            etItemName.setText(intent.getStringExtra("item_name"));
            etDescription.setText(intent.getStringExtra("item_description"));
            etPrice.setText(String.valueOf(intent.getDoubleExtra("item_price", 0.0)));
            selectedCategory = intent.getStringExtra("item_category");
            tvCategory.setText(selectedCategory);
            currentImageUrl = intent.getStringExtra("item_image");

            Glide.with(this)
                    .load(currentImageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(ivMenuItem);
        } else {
            tvTitle.setText("Add New Menu Item");
            tvDelete.setVisibility(TextView.GONE);
        }
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnChangeImage.setOnClickListener(v -> openImagePicker());

        llCategory.setOnClickListener(v -> showCategoryDialog());

        btnSave.setOnClickListener(v -> saveMenuItem());

        tvDelete.setOnClickListener(v -> showDeleteDialog());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivMenuItem.setImageURI(imageUri);
        }
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category")
                .setItems(categories, (dialog, which) -> {
                    selectedCategory = categories[which];
                    tvCategory.setText(selectedCategory);
                })
                .show();
    }

    private void saveMenuItem() {
        String name = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        if (imageUri != null) {
            uploadImageAndSave(name, description, price);
        } else {
            saveToDatabase(name, description, price, currentImageUrl);
        }
    }

    private void uploadImageAndSave(String name, String description, double price) {
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveToDatabase(name, description, price, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToDatabase(String name, String description, double price, String imageUrl) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("name", name);
        itemMap.put("description", description);
        itemMap.put("price", price);
        itemMap.put("category", selectedCategory);
        itemMap.put("imageUrl", imageUrl);

        DatabaseReference ref;
        if (isEditMode) {
            ref = menuRef.child(itemId);
        } else {
            ref = menuRef.push();
        }

        ref.updateChildren(itemMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save item", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> deleteItem())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem() {
        menuRef.child(itemId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                });
    }
}
