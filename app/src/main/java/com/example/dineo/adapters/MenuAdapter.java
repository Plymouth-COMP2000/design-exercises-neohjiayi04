package com.example.dineo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.MenuItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Menu Adapter - Handles BOTH Base64 and URL images WITHOUT AsyncTask
 * Student ID: BSSE2506008
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private static final String TAG = "MenuAdapter";

    private final Context context;
    private final List<MenuItem> menuItems;
    private final OnMenuItemClickListener listener;
    private final ExecutorService executor = Executors.newFixedThreadPool(3); // Thread pool for image loading

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem menuItem);
    }

    public MenuAdapter(Context context, List<MenuItem> menuItems, OnMenuItemClickListener listener) {
        this.context = context;
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);

        // Set name and price
        holder.textViewName.setText(menuItem.getName() != null ? menuItem.getName() : "Unknown");
        holder.textViewPrice.setText(menuItem.getPriceFormatted());

        // Load image safely
        loadImage(menuItem.getImageUrl(), holder.imageViewMenu);

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMenuItemClick(menuItem);
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    private void loadImage(String imageData, ImageView imageView) {
        if (imageData == null || imageData.isEmpty()) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        // Base64 image
        if (!imageData.startsWith("http://") && !imageData.startsWith("https://")) {
            try {
                byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) imageView.setImageBitmap(bitmap);
                else imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            // URL image - load with executor
            imageView.setImageResource(android.R.drawable.ic_menu_gallery); // placeholder
            executor.execute(() -> {
                Bitmap bitmap = null;
                try {
                    URL url = new URL(imageData);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    input.close();
                    connection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Image load error: " + e.getMessage());
                }

                Bitmap finalBitmap = bitmap;
                imageView.post(() -> {
                    if (finalBitmap != null) imageView.setImageBitmap(finalBitmap);
                    // else placeholder is already set
                });
            });
        }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMenu;
        TextView textViewName, textViewPrice;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMenu = itemView.findViewById(R.id.imageViewMenu);
            textViewName = itemView.findViewById(R.id.textViewMenuName);
            textViewPrice = itemView.findViewById(R.id.textViewMenuPrice);
        }
    }
}
