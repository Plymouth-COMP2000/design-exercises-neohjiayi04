package com.example.dineo.guest;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dineo.R;

public class GuestFoodActivity extends AppCompatActivity {

    private ImageView ivFood;
    private TextView tvName, tvPrice, tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_food);

        ivFood = findViewById(R.id.ivFoodImage);
        tvName = findViewById(R.id.tvFoodName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);

        loadFoodDetails();
    }

    private void loadFoodDetails() {
        String name = getIntent().getStringExtra("name");
        double price = getIntent().getDoubleExtra("price", 0);
        String desc = getIntent().getStringExtra("desc");
        String img = getIntent().getStringExtra("img");

        tvName.setText(name);
        tvPrice.setText(String.format("RM %.2f", price));
        tvDescription.setText(desc);

        Glide.with(this).load(img).into(ivFood);
    }
}
