package com.example.dineo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class StaffReservationAdapter extends RecyclerView.Adapter<StaffReservationAdapter.ReservationViewHolder> {
    private Context context;
    private List<Reservation> reservations;
    private OnReservationClickListener listener;

    public interface OnReservationClickListener {
        void onConfirm(Reservation reservation);
        void onCancel(Reservation reservation);
    }

    public StaffReservationAdapter(Context context, List<Reservation> reservations, OnReservationClickListener listener) {
        this.context = context;
        this.reservations = reservations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation_staff, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.textViewGuestName.setText(reservation.getCustomerName());
        String info = reservation.getDate() + " · " + reservation.getTime() + " · " +
                reservation.getTableNumber() + " · " + reservation.getNumberOfGuests() + " Pax";
        holder.textViewReservationInfo.setText(info);

        // Set status badge
        holder.textViewStatusBadge.setText(reservation.getStatus().toUpperCase());
        switch (reservation.getStatus()) {
            case "Pending":
                holder.textViewStatusBadge.setBackgroundColor(context.getResources().getColor(R.color.status_pending));
                holder.buttonConfirm.setVisibility(View.VISIBLE);
                holder.buttonCancel.setVisibility(View.VISIBLE);
                break;
            case "Confirmed":
                holder.textViewStatusBadge.setBackgroundColor(context.getResources().getColor(R.color.status_confirmed));
                holder.buttonConfirm.setVisibility(View.GONE);
                holder.buttonCancel.setVisibility(View.VISIBLE);
                break;
            case "Seated":
                holder.textViewStatusBadge.setBackgroundColor(context.getResources().getColor(R.color.status_seated));
                holder.buttonConfirm.setVisibility(View.GONE);
                holder.buttonCancel.setVisibility(View.VISIBLE);
                break;
            case "Cancelled":
                holder.textViewStatusBadge.setBackgroundColor(context.getResources().getColor(R.color.status_cancelled));
                holder.buttonConfirm.setVisibility(View.GONE);
                holder.buttonCancel.setVisibility(View.GONE);
                break;
        }

        // Button click listeners
        holder.buttonConfirm.setOnClickListener(v -> {
            if(listener != null) listener.onConfirm(reservation);
        });

        holder.buttonCancel.setOnClickListener(v -> {
            if(listener != null) listener.onCancel(reservation);
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGuestName, textViewReservationInfo, textViewStatusBadge;
        MaterialButton buttonConfirm, buttonCancel;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGuestName = itemView.findViewById(R.id.textViewGuestName);
            textViewReservationInfo = itemView.findViewById(R.id.textViewReservationInfo);
            textViewStatusBadge = itemView.findViewById(R.id.textViewStatusBadge);
            buttonConfirm = itemView.findViewById(R.id.buttonConfirm);
            buttonCancel = itemView.findViewById(R.id.buttonCancel);
        }
    }
}
