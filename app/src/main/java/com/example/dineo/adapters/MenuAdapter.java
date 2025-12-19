package com.example.dineo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dineo.R;
import com.example.dineo.models.MenuItem;

import java.util.List;

/**
 * Menu Adapter for RecyclerView - Browse menu items only
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

        // Set price with proper formatting
        holder.textViewPrice.setText(menuItem.getPriceFormatted());

        // Load image with Glide
        if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(menuItem.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(holder.imageViewMenu);
        } else {
            // Default placeholder
            holder.imageViewMenu.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Click listener - opens menu detail screen
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

    // Update list for search/filter
    public void updateList(List<MenuItem> newList) {
        this.menuItems.clear();
        this.menuItems.addAll(newList);
        notifyDataSetChanged();
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
