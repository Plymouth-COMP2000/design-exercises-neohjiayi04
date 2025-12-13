package com.example.dineo.staff;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dineo.R;
import com.example.dineo.models.MenuItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddMenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivMenuItem, ivBack;
    private EditText etName, etDesc, etPrice;
    private TextView tvCategory;
    private Button btnChangeImage, btnSave;
    private LinearLayout llCategory;

    private Uri imageUri;
    private String selectedCategory = "Burgers";

    private DatabaseReference menuRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        initViews();
        setupFirebase();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivMenuItem = findViewById(R.id.ivMenuItem);
        etName = findViewById(R.id.etItemName);
        etDesc = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        tvCategory = findViewById(R.id.tvCategory);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSave = findViewById(R.id.btnSave);
        llCategory = findViewById(R.id.llCategory);
        tvCategory.setText(selectedCategory);
    }

    private void setupFirebase() {
        menuRef = FirebaseDatabase.getInstance().getReference("menu_items");
        storageRef = FirebaseStorage.getInstance().getReference("menu_images");
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnChangeImage.setOnClickListener(v -> openImagePicker());

        llCategory.setOnClickListener(v -> selectCategory());

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void selectCategory() {
        String[] categories = {"Appetizers","Burgers","Main Course","Desserts","Beverages","Salads"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choose Category")
                .setItems(categories, (dialog, which) -> {
                    selectedCategory = categories[which];
                    tvCategory.setText(selectedCategory);
                }).show();
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (req == PICK_IMAGE_REQUEST && res == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivMenuItem.setImageURI(imageUri);
        }
    }

    private void saveItem() {
        String name = etName.getText().toString();
        String desc = etDesc.getText().toString();
        String priceStr = etPrice.getText().toString();

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Fill all fields & select image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        String id = menuRef.push().getKey();

        StorageReference fileRef = storageRef.child(id + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(t -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                MenuItem item = new MenuItem(
                        id, name, desc, price, selectedCategory, uri.toString()
                );

                menuRef.child(id).setValue(item);
                Toast.makeText(this, "Menu Item Added", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
