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
import com.example.dineo.models.Reservation;

import java.util.List;

/**
 * Reservation Adapter for RecyclerView
 * Student ID: BSSE2506008
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationClickListener listener;

    public interface OnReservationClickListener {
        void onEditClick(Reservation reservation);
        void onCancelClick(Reservation reservation);
    }

    public ReservationAdapter(Context context, List<Reservation> reservations, OnReservationClickListener listener) {
        this.context = context;
        this.reservations = reservations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.textViewDateTime.setText(reservation.getDateTimeFormatted());
        holder.textViewGuests.setText(reservation.getGuestsFormatted());
        holder.textViewTable.setText(reservation.getTableFormatted());
        holder.textViewStatus.setText(reservation.getStatus());

        // Set status color
        if ("Confirmed".equals(reservation.getStatus())) {
            holder.textViewStatus.setBackgroundResource(R.drawable.bg_status_confirmed);
        } else if ("Cancelled".equals(reservation.getStatus())) {
            holder.textViewStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
        } else {
            holder.textViewStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }

        // Edit button
        holder.imageViewEdit.setOnClickListener(v -> {
            if (listener != null && !"Cancelled".equals(reservation.getStatus())) {
                listener.onEditClick(reservation);
            }
        });

        // Cancel button
        holder.imageViewCancel.setOnClickListener(v -> {
            if (listener != null && !"Cancelled".equals(reservation.getStatus())) {
                listener.onCancelClick(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDateTime, textViewGuests, textViewTable, textViewStatus;
        ImageView imageViewEdit, imageViewCancel;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewGuests = itemView.findViewById(R.id.textViewGuests);
            textViewTable = itemView.findViewById(R.id.textViewTable);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            imageViewEdit = itemView.findViewById(R.id.imageViewEdit);
            imageViewCancel = itemView.findViewById(R.id.imageViewCancel);
        }
    }
}