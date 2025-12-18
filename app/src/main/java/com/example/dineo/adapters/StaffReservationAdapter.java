package com.example.dineo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.Reservation;

import java.util.List;

/**
 * Staff Reservation Adapter - For staff to manage reservations
 * Student ID: BSSE2506008
 */
public class StaffReservationAdapter extends RecyclerView.Adapter<StaffReservationAdapter.ViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationActionListener listener;

    public interface OnReservationActionListener {
        void onConfirmClick(Reservation reservation);
        void onCancelClick(Reservation reservation);
    }

    public StaffReservationAdapter(Context context, List<Reservation> reservations, OnReservationActionListener listener) {
        this.context = context;
        this.reservations = reservations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.textViewCustomerName.setText(reservation.getCustomerName());
        holder.textViewDateTime.setText(reservation.getDateTimeFormatted());
        holder.textViewGuests.setText(reservation.getGuestsFormatted());
        holder.textViewTable.setText(reservation.getTableFormatted());
        holder.textViewStatus.setText(reservation.getStatus());

        // Set status color
        if ("Confirmed".equals(reservation.getStatus())) {
            holder.textViewStatus.setBackgroundResource(R.drawable.bg_status_confirmed);
            holder.btnConfirm.setVisibility(View.GONE);
        } else if ("Pending".equals(reservation.getStatus())) {
            holder.textViewStatus.setBackgroundResource(R.drawable.bg_status_pending);
            holder.btnConfirm.setVisibility(View.VISIBLE);
        } else if ("Cancelled".equals(reservation.getStatus())) {
            holder.textViewStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }

        // Confirm button
        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmClick(reservation);
            }
        });

        // Cancel button
        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCustomerName, textViewDateTime, textViewGuests, textViewTable, textViewStatus;
        Button btnConfirm, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewGuests = itemView.findViewById(R.id.textViewGuests);
            textViewTable = itemView.findViewById(R.id.textViewTable);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}