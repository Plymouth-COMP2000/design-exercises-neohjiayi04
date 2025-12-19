package com.example.dineo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
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

/**
 * Menu Adapter - Handles BOTH Base64 and URL images WITHOUT Glide
 * Student ID: BSSE2506008
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private OnMenuItemClickListener listener;

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

        // Set name
        holder.textViewName.setText(menuItem.getName());

        // Set price
        holder.textViewPrice.setText(menuItem.getPriceFormatted());

        // Load image
        String imageData = menuItem.getImageUrl();

        if (imageData != null && !imageData.isEmpty()) {
            // Check if it's a Base64 string or URL
            if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
                // It's a URL - load from internet
                holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
                new LoadUrlImageTask(holder.imageViewMenu).execute(imageData);
            } else {
                // It's Base64 - decode directly
                try {
                    byte[] decodedBytes = Base64.decode(imageData, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        holder.imageViewMenu.setImageBitmap(bitmap);
                    } else {
                        holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        } else {
            // No image - show placeholder
            holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMenuItemClick(menuItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    /**
     * AsyncTask to load images from URL in background
     */
    private static class LoadUrlImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadUrlImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(5000); // 5 second timeout
                connection.setReadTimeout(5000);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && imageView != null) {
                imageView.setImageBitmap(result);
            }
            // If null, placeholder is already showing
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