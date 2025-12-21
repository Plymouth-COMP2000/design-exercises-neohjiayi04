package com.example.dineo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.Reservation;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Staff Reservation List
 * Safe, defensive, and status-aware
 */
public class StaffReservationAdapter
        extends RecyclerView.Adapter<StaffReservationAdapter.ReservationViewHolder> {

    private final Context context;
    private final List<Reservation> reservations = new ArrayList<>();
    private final OnReservationClickListener listener;

    // ===================== INTERFACE =====================
    public interface OnReservationClickListener {
        void onReservationClick(Reservation reservation);
        void onConfirm(Reservation reservation);
        void onCancel(Reservation reservation);
    }

    // ===================== CONSTRUCTOR =====================
    public StaffReservationAdapter(Context context,
                                   List<Reservation> initialData,
                                   OnReservationClickListener listener) {
        this.context = context;
        if (initialData != null) {
            this.reservations.addAll(initialData);
        }
        this.listener = listener;
    }

    // ===================== ADAPTER METHODS =====================
    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_reservation_staff, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation r = reservations.get(position);
        if (r == null) return;

        // Guest name
        holder.textGuestName.setText(
                safeText(r.getCustomerName(), "Guest")
        );

        // Reservation info line
        String info = safeText(r.getDate(), "Date")
                + " • "
                + safeText(r.getTime(), "Time")
                + " • "
                + r.getGuestsFormatted();

        holder.textInfo.setText(info);

        // Status badge
        String status = safeText(r.getStatus(), "Pending");
        holder.textStatus.setText(status.toUpperCase());

        setupStatusUI(holder, status);

        // Item click → details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReservationClick(r);
            }
        });

        // Confirm
        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirm(r);
            }
        });

        // Cancel
        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel(r);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    // ===================== PUBLIC METHODS =====================
    public void setReservations(List<Reservation> newList) {
        reservations.clear();
        if (newList != null) {
            reservations.addAll(newList);
        }
        notifyDataSetChanged();
    }

    // ===================== VIEW HOLDER =====================
    static class ReservationViewHolder extends RecyclerView.ViewHolder {

        TextView textGuestName, textInfo, textStatus;
        MaterialButton btnConfirm, btnCancel;

        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);

            textGuestName = itemView.findViewById(R.id.textViewGuestName);
            textInfo = itemView.findViewById(R.id.textViewReservationInfo);
            textStatus = itemView.findViewById(R.id.textViewStatusBadge);

            btnConfirm = itemView.findViewById(R.id.buttonConfirm);
            btnCancel = itemView.findViewById(R.id.buttonCancel);
        }
    }

    // ===================== HELPERS =====================
    private void setupStatusUI(ReservationViewHolder holder, String status) {

        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);

        switch (status) {
            case "Pending":
                holder.textStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.btnConfirm.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;

            case "Confirmed":
                holder.textStatus.setBackgroundResource(R.drawable.bg_status_confirmed);
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;

            case "Seated":
                holder.textStatus.setBackgroundResource(R.drawable.bg_status_seated);
                break;

            case "Cancelled":
                holder.textStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                break;

            default:
                holder.textStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
