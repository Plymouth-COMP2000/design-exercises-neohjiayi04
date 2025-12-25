package com.example.dineo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
<<<<<<< HEAD
import android.util.Base64;
import android.util.Log;
=======
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
<<<<<<< HEAD
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.MenuItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private static final String TAG = "MenuAdapter";

    private final Context context;
    private List<MenuItem> menuItems;
    private final OnMenuItemClickListener listener;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem menuItem);
    }

    public MenuAdapter(Context context, List<MenuItem> menuItems, OnMenuItemClickListener listener) {
        this.context = context;
        this.menuItems = menuItems;
        this.listener = listener;
    }

    public void setMenuItems(List<MenuItem> items) {
        this.menuItems = items;
        notifyDataSetChanged();
=======
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
// IMPORTANT: Import YOUR MenuItem model class
import com.example.dineo.models.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    public MenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
<<<<<<< HEAD
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
=======
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_guest, parent, false);
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
<<<<<<< HEAD
        if (position < 0 || position >= menuItems.size()) return;

        MenuItem menuItem = menuItems.get(position);

        holder.textViewName.setText(menuItem.getName());
        holder.textViewPrice.setText(menuItem.getPriceFormatted());
        holder.textViewCategory.setText(menuItem.getCategory());

        // Optional: style category badge background based on category (color logic)
        holder.textViewCategory.setBackgroundResource(R.drawable.category_badge);

        holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery); // placeholder
        loadImage(menuItem.getImageUrl(), holder.imageViewMenu);

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                listener.onMenuItemClick(menuItems.get(adapterPosition));
            }
        });
=======
        MenuItem item = menuItems.get(position);
        holder.bind(item);
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
    }

    @Override
    public int getItemCount() {
<<<<<<< HEAD
        return menuItems != null ? menuItems.size() : 0;
    }

    private void loadImage(String imageData, ImageView imageView) {
        if (imageData == null || imageData.isEmpty()) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        imageView.setTag(imageData);

        if (!imageData.startsWith("http://") && !imageData.startsWith("https://")) {
            try {
                byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e(TAG, "Base64 decode error", e);
                imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            executor.execute(() -> {
                Bitmap bitmap = null;
                try {
                    URL url = new URL(imageData);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    input.close();
                    connection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Image load error", e);
                }

                Bitmap finalBitmap = bitmap;
                imageView.post(() -> {
                    if (imageData.equals(imageView.getTag()) && finalBitmap != null) {
                        imageView.setImageBitmap(finalBitmap);
                    }
                });
            });
        }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMenu;
        TextView textViewName, textViewPrice, textViewCategory;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMenu = itemView.findViewById(R.id.imageViewMenu);
            textViewName = itemView.findViewById(R.id.textViewMenuName);
            textViewPrice = itemView.findViewById(R.id.textViewMenuPrice);
            textViewCategory = itemView.findViewById(R.id.textViewMenuCategory);
        }
    }
}
=======
        return menuItems.size();
    }

    public void updateItems(List<MenuItem> newItems) {
        this.menuItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void filterByCategory(String category) {
        // This method can be used if you want to filter in the adapter
        notifyDataSetChanged();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        CardView cardView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.menu_item_image);
            nameTextView = itemView.findViewById(R.id.menu_item_name);
            priceTextView = itemView.findViewById(R.id.menu_item_price);
            cardView = itemView.findViewById(R.id.menu_card);

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(menuItems.get(position));
                    }
                }
            });
        }

        public void bind(MenuItem item) {
            nameTextView.setText(item.getName());
            priceTextView.setText(String.format("RM%.2f", item.getPrice()));

            // Load image from file path
            if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                File imgFile = new File(item.getImagePath());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.ic_placeholder);
                }
            } else {
                imageView.setImageResource(R.drawable.ic_placeholder);
            }
        }
    }
}
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
