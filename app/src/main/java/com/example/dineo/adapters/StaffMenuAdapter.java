package com.example.dineo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
 * StaffMenuAdapter - Manage menu items with edit/delete (NO Glide)
 * Student ID: BSSE2506008
 */
public class StaffMenuAdapter extends RecyclerView.Adapter<StaffMenuAdapter.MenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private OnMenuItemActionListener listener;

    public interface OnMenuItemActionListener {
        void onEditClick(MenuItem menuItem);
        void onDeleteClick(MenuItem menuItem);
    }

    public StaffMenuAdapter(Context context, List<MenuItem> menuItems, OnMenuItemActionListener listener) {
        this.context = context;
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);

        // Set name
        holder.textViewName.setText(menuItem.getName());

        // Set price
        holder.textViewPrice.setText(menuItem.getPriceFormatted());

        // Set category
        if (menuItem.getCategory() != null && !menuItem.getCategory().isEmpty()) {
            holder.textViewCategory.setText(menuItem.getCategory());
            holder.textViewCategory.setVisibility(View.VISIBLE);
        } else {
            holder.textViewCategory.setVisibility(View.GONE);
        }

        // Load image WITHOUT Glide
        String imageData = menuItem.getImageUrl();
        if (imageData != null && !imageData.isEmpty()) {
            if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
                // URL - load in background
                holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
                new LoadImageTask(holder.imageViewMenu).execute(imageData);
            } else {
                // Base64 - decode directly
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
            holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(menuItem);
            }
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(menuItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    /**
     * AsyncTask to load images from URL
     */
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
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
                connection.setConnectTimeout(5000);
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
        }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMenu;
        TextView textViewName, textViewPrice, textViewCategory;
        ImageButton btnEdit, btnDelete;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMenu = itemView.findViewById(R.id.imageViewMenu);
            textViewName = itemView.findViewById(R.id.textViewMenuName);
            textViewPrice = itemView.findViewById(R.id.textViewMenuPrice);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}