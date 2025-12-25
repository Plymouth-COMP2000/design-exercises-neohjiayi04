package com.example.dineo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.Reservation;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuestReservationAdapter extends RecyclerView.Adapter<GuestReservationAdapter.ReservationViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationActionListener listener;

    public interface OnReservationActionListener {
        void onEditClick(Reservation reservation);
        void onCancelClick(Reservation reservation);
    }

    public GuestReservationAdapter(Context context, List<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations != null ? reservations : new ArrayList<>();
    }

    public void setOnReservationActionListener(OnReservationActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_guest_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void updateReservations(List<Reservation> newReservations) {
        this.reservations = newReservations != null ? newReservations : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateTime, tvPax, tvTable;
        MaterialButton statusBadge;
        ImageView ivEdit, ivCancel;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvPax = itemView.findViewById(R.id.tv_pax);
            tvTable = itemView.findViewById(R.id.tv_table);
            statusBadge = itemView.findViewById(R.id.status_badge);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivCancel = itemView.findViewById(R.id.iv_cancel);

            ivEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(reservations.get(position));
                    }
                }
            });

            ivCancel.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCancelClick(reservations.get(position));
                    }
                }
            });
        }

        public void bind(Reservation reservation) {
            // Format date and time
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                java.util.Date date = inputFormat.parse(reservation.getDate());
                String formattedDate = outputFormat.format(date);
                String formattedTime = reservation.getFormattedTime();
                tvDateTime.setText(formattedDate + " at " + formattedTime);
            } catch (Exception e) {
                tvDateTime.setText(reservation.getDate() + " at " + reservation.getTime());
            }

            // Set pax
            tvPax.setText(reservation.getNumberOfPax() + " Pax");

            // Set table
            tvTable.setText("Table: " + reservation.getTableNumber());

            // Set status badge
            statusBadge.setText(reservation.getStatus());
            int statusColor = getStatusColor(reservation.getStatus());
            statusBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(statusColor));

            // Show/hide action buttons based on status
            String status = reservation.getStatus().toLowerCase();
            if (status.equals("cancelled") || status.equals("completed") || status.equals("no-show")) {
                ivEdit.setVisibility(View.GONE);
                ivCancel.setVisibility(View.GONE);
            } else {
                ivEdit.setVisibility(View.VISIBLE);
                ivCancel.setVisibility(View.VISIBLE);
            }
        }

        private int getStatusColor(String status) {
            switch (status.toLowerCase()) {
                case "confirmed":
                    return ContextCompat.getColor(context, R.color.success_green);
                case "upcoming":
                    return ContextCompat.getColor(context, R.color.orange_accent);
                case "seated":
                    return ContextCompat.getColor(context, R.color.teal_accent);
                case "completed":
                    return ContextCompat.getColor(context, R.color.text_secondary);
                case "cancelled":
                case "no-show":
                    return ContextCompat.getColor(context, R.color.error_red);
                default:
                    return ContextCompat.getColor(context, R.color.text_secondary);
            }
        }
    }
}