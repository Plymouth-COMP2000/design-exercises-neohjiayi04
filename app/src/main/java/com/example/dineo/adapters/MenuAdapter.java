package com.example.dineo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dineo.R;
import com.example.dineo.models.MenuItem;
import com.example.dineo.staff.EditMenuItemActivity;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuVH> {

    private Context ctx;
    private ArrayList<MenuItem> list;
    private boolean isStaff;

    public MenuAdapter(Context ctx, ArrayList<MenuItem> list, boolean isStaff) {
        this.ctx = ctx;
        this.list = list;
        this.isStaff = isStaff;
    }

    @NonNull
    @Override
    public MenuVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuVH(LayoutInflater.from(ctx)
                .inflate(R.layout.item_staff_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuVH h, int pos) {
        MenuItem m = list.get(pos);
        h.tvName.setText(m.getName());
        h.tvPrice.setText("RM " + m.getPrice());

        Glide.with(ctx).load(m.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(h.ivImg);

        if (isStaff) {
            h.btnEdit.setVisibility(View.VISIBLE);
            h.btnDelete.setVisibility(View.VISIBLE);

            h.btnEdit.setOnClickListener(v -> {
                Intent i = new Intent(ctx, EditMenuItemActivity.class);
                i.putExtra("id", m.getId());
                i.putExtra("name", m.getName());
                i.putExtra("desc", m.getDescription());
                i.putExtra("price", m.getPrice());
                i.putExtra("category", m.getCategory());
                i.putExtra("img", m.getImageUrl());
                ctx.startActivity(i);
            });

            h.btnDelete.setOnClickListener(v -> {
                Toast.makeText(ctx, "Delete inside EditPage", Toast.LENGTH_SHORT).show();
            });

        } else {
            h.btnEdit.setVisibility(View.GONE);
            h.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MenuVH extends RecyclerView.ViewHolder {
        ImageView ivImg, btnEdit, btnDelete;
        TextView tvName, tvPrice;

        MenuVH(View v) {
            super(v);
            ivImg = v.findViewById(R.id.ivStaffImage);
            tvName = v.findViewById(R.id.tvStaffName);
            tvPrice = v.findViewById(R.id.tvStaffPrice);
            btnEdit = v.findViewById(R.id.ivEdit);
            btnDelete = v.findViewById(R.id.ivDelete);
        }
    }
}
