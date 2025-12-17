package com.example.dineo.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.MenuItem;

import java.io.File;

public class MenuDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameTextView;
    private TextView priceTextView;
    private TextView descriptionTextView;
    private CheckBox lessSpicyCheckBox;
    private CheckBox lessSaltyCheckBox;
    private CheckBox lessSweetCheckBox;

    private DatabaseHelper databaseHelper;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        initializeViews();
        loadMenuItemDetails();
    }

    private void initializeViews() {
        imageView = findViewById(R.id.menu_detail_image);
        nameTextView = findViewById(R.id.menu_detail_name);
        priceTextView = findViewById(R.id.menu_detail_price);
        descriptionTextView = findViewById(R.id.menu_detail_description);
        lessSpicyCheckBox = findViewById(R.id.checkbox_less_spicy);
        lessSaltyCheckBox = findViewById(R.id.checkbox_less_salty);
        lessSweetCheckBox = findViewById(R.id.checkbox_less_sweet);
        databaseHelper = new DatabaseHelper(this);
    }

    private void loadMenuItemDetails() {
        int menuItemId = getIntent().getIntExtra("menu_item_id", -1);
        if (menuItemId == -1) {
            finish();
            return;
        }

        menuItem = databaseHelper.getMenuItemById(menuItemId); // ✅ FIXED
        if (menuItem == null) {
            finish();
            return;
        }

        nameTextView.setText(menuItem.getName());
        priceTextView.setText(String.format("RM %.2f", menuItem.getPrice()));
        descriptionTextView.setText(menuItem.getDescription());

        if (menuItem.getImagePath() != null && !menuItem.getImagePath().isEmpty()) {
            File imgFile = new File(menuItem.getImagePath());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            }
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