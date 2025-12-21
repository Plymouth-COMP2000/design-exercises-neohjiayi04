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
import com.example.dineo.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<Notification> notificationList;
    private final OnNotificationActionListener listener;

    // Interface for notification actions
    public interface OnNotificationActionListener {
        void onNotificationClick(Notification notification);
        void onDeleteClick(Notification notification);
    }

    // Constructor - Context FIRST, then List, then Listener
    public NotificationAdapter(Context context, List<Notification> notificationList, OnNotificationActionListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.textViewTitle.setText(notification.getTitle());
        holder.textViewMessage.setText(notification.getMessage());
        holder.textViewTimestamp.setText(notification.getTimestamp());

        // Visual indicator for unread notifications
        if (notification.isRead()) {
            holder.itemView.setAlpha(0.6f);
            holder.imageViewIcon.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.imageViewIcon.setAlpha(1.0f);
        }

        // Click listener for the whole item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });

        // Delete button click listener
        holder.imageViewDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewMessage, textViewTimestamp;
        ImageView imageViewIcon, imageViewDelete;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            // FIXED: Use correct IDs from item_notification.xml
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
        }
    }
}