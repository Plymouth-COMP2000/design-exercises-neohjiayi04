package com.example.dineo.guest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.staff.Reservation;

import java.util.List;

public class GuestReservationAdapter extends RecyclerView.Adapter<GuestReservationAdapter.ReservationViewHolder> {

    private List<Reservation> reservations;
    private OnReservationActionListener listener;

    public interface OnReservationActionListener {
        void onEdit(Reservation reservation);
        void onCancel(Reservation reservation);
    }

    public GuestReservationAdapter(List<Reservation> reservations, OnReservationActionListener listener) {
        this.reservations = reservations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation, listener);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void updateReservations(List<Reservation> newReservations) {
        this.reservations = newReservations;
        notifyDataSetChanged();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateTime, tvPax, tvTable, tvStatus;
        private ImageButton btnEdit, btnCancel;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPax = itemView.findViewById(R.id.tvPax);
            tvTable = itemView.findViewById(R.id.tvTable);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }

        public void bind(Reservation reservation, OnReservationActionListener listener) {
            tvDateTime.setText(reservation.getDateTime());
            tvPax.setText(reservation.getPax());
            tvTable.setText(reservation.getTable());
            tvStatus.setText(reservation.getStatus());

            // Hide action buttons for finished reservations
            if (reservation.getStatus().equals("Completed") || reservation.getStatus().equals("Cancelled")) {
                btnEdit.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
            } else {
                btnEdit.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);

                btnEdit.setOnClickListener(v -> listener.onEdit(reservation));
                btnCancel.setOnClickListener(v -> listener.onCancel(reservation));
            }
        }
    }
}