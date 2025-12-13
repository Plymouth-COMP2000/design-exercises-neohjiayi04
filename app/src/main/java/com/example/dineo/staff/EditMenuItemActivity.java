package com.example.dineo.staff;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dineo.R;
import com.example.dineo.models.MenuItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditMenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivBack, ivMenuItem;
    private EditText etName, etDesc, etPrice;
    private TextView tvCategory, tvDelete, tvTitle;
    private Button btnChangeImage, btnSave;
    private LinearLayout llCategory;

    private DatabaseReference menuRef;
    private StorageReference storageRef;
    private Uri newImageUri;

    private String itemId;
    private MenuItem currentItem;
    private String selectedCategory = "Burgers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_item);

        initViews();
        setupFirebase();
        getDataFromIntent();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivMenuItem = findViewById(R.id.ivMenuItem);
        etName = findViewById(R.id.etItemName);
        etDesc = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        tvCategory = findViewById(R.id.tvCategory);
        tvDelete = findViewById(R.id.tvDelete);
        tvTitle = findViewById(R.id.tvTitle);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSave = findViewById(R.id.btnSave);
        llCategory = findViewById(R.id.llCategory);
    }

    private void setupFirebase() {
        menuRef = FirebaseDatabase.getInstance().getReference("menu_items");
        storageRef = FirebaseStorage.getInstance().getReference("menu_images");
    }

    private void getDataFromIntent() {
        Intent i = getIntent();
        itemId = i.getStringExtra("id");

        currentItem = new MenuItem(
                itemId,
                i.getStringExtra("name"),
                i.getStringExtra("desc"),
                i.getDoubleExtra("price", 0),
                i.getStringExtra("category"),
                i.getStringExtra("img")
        );

        tvTitle.setText("Edit Menu Item");
        etName.setText(currentItem.getName());
        etDesc.setText(currentItem.getDescription());
        etPrice.setText(String.valueOf(currentItem.getPrice()));
        selectedCategory = currentItem.getCategory();
        tvCategory.setText(selectedCategory);

        Glide.with(this).load(currentItem.getImageUrl()).into(ivMenuItem);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnChangeImage.setOnClickListener(v -> openImagePicker());

        llCategory.setOnClickListener(v -> chooseCategory());

        btnSave.setOnClickListener(v -> saveChanges());

        tvDelete.setOnClickListener(v -> confirmDelete());
    }

    private void openImagePicker() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (req == PICK_IMAGE_REQUEST && res == RESULT_OK && data != null) {
            newImageUri = data.getData();
            ivMenuItem.setImageURI(newImageUri);
        }
    }

    private void chooseCategory() {
        String[] cats = {"Appetizers","Burgers","Main Course","Desserts","Beverages","Salads"};
        new AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setItems(cats, (d, w) -> {
                    selectedCategory = cats[w];
                    tvCategory.setText(selectedCategory);
                }).show();
    }

    private void saveChanges() {
        String name = etName.getText().toString();
        String desc = etDesc.getText().toString();
        double price = Double.parseDouble(etPrice.getText().toString());

        if (newImageUri != null) {
            uploadNewImage(name, desc, price);
        } else {
            updateDatabase(name, desc, price, currentItem.getImageUrl());
        }
    }

    private void uploadNewImage(String name, String desc, double price) {
        StorageReference fileRef = storageRef.child(itemId + ".jpg");
        fileRef.putFile(newImageUri).addOnSuccessListener(t -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                updateDatabase(name, desc, price, uri.toString());
            });
        });
    }

    private void updateDatabase(String name, String desc, double price, String imageUrl) {
        menuRef.child(itemId).setValue(
                new MenuItem(itemId, name, desc, price, selectedCategory, imageUrl)
        ).addOnSuccessListener(a -> {
            Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Confirm delete?")
                .setPositiveButton("Delete", (d, w) -> deleteItem())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem() {
        menuRef.child(itemId).removeValue();
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        finish();
    }
}
