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

/**
 * Notification Adapter - Display notifications for users
 * Student ID: BSSE2506008
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notifications;
    private OnNotificationActionListener listener;

    public interface OnNotificationActionListener {
        void onNotificationClick(Notification notification);
        void onDeleteClick(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> notifications, OnNotificationActionListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.textViewTitle.setText(notification.getTitle());
        holder.textViewMessage.setText(notification.getMessage());
        holder.textViewTimestamp.setText(notification.getTimestamp());

        // Set icon based on notification type
        int iconResource = getIconForType(notification.getType());
        holder.imageViewIcon.setImageResource(iconResource);

        // Set background based on read status
        if (notification.isRead()) {
            // Read notification - normal background
            holder.itemView.setBackgroundColor(0xFFFFFFFF); // White
            holder.textViewTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            // Unread notification - highlighted background
            holder.itemView.setBackgroundColor(0xFFFFF5EB); // Light orange
            holder.textViewTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        // Click on notification
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });

        // Delete button
        holder.imageViewDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * Get icon resource based on notification type
     */
    private int getIconForType(String type) {
        if (type == null) {
            return android.R.drawable.ic_dialog_info;
        }

        switch (type) {
            case "reservation_confirmed":
                return android.R.drawable.ic_menu_my_calendar; // Calendar icon

            case "reservation_modified":
                return android.R.drawable.ic_menu_edit; // Edit icon

            case "reservation_cancelled":
                return android.R.drawable.ic_delete; // Delete icon

            case "new_reservation":
                return android.R.drawable.ic_input_add; // Add icon

            default:
                return android.R.drawable.ic_dialog_info; // Info icon
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon, imageViewDelete;
        TextView textViewTitle, textViewMessage, textViewTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // ✅ CORRECTED IDs to match item_notification.xml
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
        }
    }
}