package com.example.dineo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dineo.R;
import com.example.dineo.models.MenuItem;

import java.util.ArrayList;

public class StaffMenuAdapter extends RecyclerView.Adapter<StaffMenuAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MenuItem> list;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnEditClickListener {
        void onEdit(MenuItem item);
    }

    public interface OnDeleteClickListener {
        void onDelete(MenuItem item);
    }

    public StaffMenuAdapter(
            Context context,
            ArrayList<MenuItem> list,
            OnEditClickListener editClickListener,
            OnDeleteClickListener deleteClickListener
    ) {
        this.context = context;
        this.list = list;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    public void updateList(ArrayList<MenuItem> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @Override
    public StaffMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_staff_food_row, parent, false);
        return new StaffMenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StaffMenuAdapter.ViewHolder holder, int position) {
        MenuItem item = list.get(position);

        holder.tvFoodName.setText(item.getName());
        holder.tvFoodPrice.setText("RM " + item.getPrice());

        Glide.with(context)
                .load(item.getImageURL())
                .placeholder(R.drawable.placeholder)
                .into(holder.ivFoodImage);

        holder.btnEdit.setOnClickListener(v -> editClickListener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> deleteClickListener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFoodImage, btnEdit, btnDelete;
        TextView tvFoodName, tvFoodPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
