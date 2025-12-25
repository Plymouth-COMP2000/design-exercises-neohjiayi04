package com.example.dineo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
<<<<<<< HEAD
import android.util.Base64;
=======
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
<<<<<<< HEAD
import android.widget.Toast;
=======
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
<<<<<<< HEAD
import com.example.dineo.models.MenuItem;

import java.util.List;

public class StaffMenuAdapter extends RecyclerView.Adapter<StaffMenuAdapter.ViewHolder> {

    public interface OnMenuItemActionListener {
=======
// IMPORTANT: Import YOUR MenuItem model class
import com.example.dineo.models.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StaffMenuAdapter extends RecyclerView.Adapter<StaffMenuAdapter.StaffMenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        void onEditClick(MenuItem item);
        void onDeleteClick(MenuItem item);
    }

<<<<<<< HEAD
    private List<MenuItem> menuList;
    private Context context;
    private OnMenuItemActionListener listener;

    public StaffMenuAdapter(List<MenuItem> menuList, Context context, OnMenuItemActionListener listener) {
        this.menuList = menuList;
        this.context = context;
=======
    public StaffMenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        this.listener = listener;
    }

    @NonNull
    @Override
<<<<<<< HEAD
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = menuList.get(position);
        holder.textViewName.setText(item.getName());
        holder.textViewCategory.setText(item.getCategory());
        holder.textViewPrice.setText("$" + String.format("%.2f", item.getPrice()));

        // Load image (Base64 or URL)
        try {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                if (item.getImageUrl().startsWith("http")) {
                    // URL: Use default placeholder (or Glide/Picasso if available)
                    holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
                } else {
                    byte[] decoded = Base64.decode(item.getImageUrl(), Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                    holder.imageView.setImageBitmap(bmp);
                }
            } else {
                holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
            e.printStackTrace();
        }

        holder.imageViewEdit.setOnClickListener(v -> listener.onEditClick(item));
        holder.imageViewDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() { return menuList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, imageViewEdit, imageViewDelete;
        TextView textViewName, textViewCategory, textViewPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewMenuItem);
            imageViewEdit = itemView.findViewById(R.id.imageViewEdit);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            textViewName = itemView.findViewById(R.id.textViewItemName);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }
}
=======
    public StaffMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_staff, parent, false);
        return new StaffMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffMenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void updateItems(List<MenuItem> newItems) {
        this.menuItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    class StaffMenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        ImageView editButton;
        ImageView deleteButton;

        public StaffMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.menu_item_image);
            nameTextView = itemView.findViewById(R.id.menu_item_name);
            priceTextView = itemView.findViewById(R.id.menu_item_price);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(menuItems.get(position));
                    }
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(menuItems.get(position));
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
