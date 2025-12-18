package com.example.dineo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.MenuItem;

import java.util.List;

/**
 * Staff Menu Adapter - For staff to manage menu items
 * Student ID: BSSE2506008
 */
public class StaffMenuAdapter extends RecyclerView.Adapter<StaffMenuAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);

        holder.textViewName.setText(menuItem.getName());
        holder.textViewPrice.setText(menuItem.getPriceFormatted());

        // Load image if exists
        if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
            // Use Glide or Picasso here
            holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Edit button
        holder.imageViewEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(menuItem);
            }
        });

        // Delete button
        holder.imageViewDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(menuItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMenu, imageViewEdit, imageViewDelete;
        TextView textViewName, textViewPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMenu = itemView.findViewById(R.id.imageViewMenu);
            imageViewEdit = itemView.findViewById(R.id.imageViewEdit);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }
}