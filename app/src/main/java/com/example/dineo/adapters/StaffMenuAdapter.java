package com.example.dineo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.MenuItem;

import java.util.List;

public class StaffMenuAdapter extends RecyclerView.Adapter<StaffMenuAdapter.ViewHolder> {

    public interface OnMenuItemActionListener {
        void onEditClick(MenuItem item);
        void onDeleteClick(MenuItem item);
    }

    private List<MenuItem> menuList;
    private Context context;
    private OnMenuItemActionListener listener;

    public StaffMenuAdapter(List<MenuItem> menuList, Context context, OnMenuItemActionListener listener) {
        this.menuList = menuList;
        this.context = context;
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
