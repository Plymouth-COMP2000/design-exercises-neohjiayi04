package com.example.dineo.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dineo.R;
import com.squareup.picasso.Picasso;

public class MenuDetailActivity extends AppCompatActivity {

    private ImageView imageViewBack, imageViewFood;
    private TextView textViewName, textViewCategory, textViewPrice, textViewDescription;
    private CheckBox checkBoxLessSpicy, checkBoxLessSalty, checkBoxLessSweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewFood = findViewById(R.id.imageViewFood);
        textViewName = findViewById(R.id.textViewName);
        textViewCategory = findViewById(R.id.textViewCategory);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewDescription = findViewById(R.id.textViewDescription);
        checkBoxLessSpicy = findViewById(R.id.checkBoxLessSpicy);
        checkBoxLessSalty = findViewById(R.id.checkBoxLessSalty);
        checkBoxLessSweet = findViewById(R.id.checkBoxLessSweet);

        imageViewBack.setOnClickListener(v -> finish());

        // ✅ Correct keys must match GuestMenuActivity
        String name = getIntent().getStringExtra("menuItemName");
        double price = getIntent().getDoubleExtra("menuItemPrice", 0.0);
        String category = getIntent().getStringExtra("menuItemCategory");
        String description = getIntent().getStringExtra("menuItemDescription");
        String imageUrl = getIntent().getStringExtra("menuItemImageUrl");

        // Bind data safely
        textViewName.setText(name != null ? name : "N/A");
        textViewCategory.setText(category != null ? category : "N/A");
        textViewPrice.setText(String.format("RM %.2f", price));
        textViewDescription.setText(description != null ? description : "No description");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(imageViewFood);
        }
    }
}
