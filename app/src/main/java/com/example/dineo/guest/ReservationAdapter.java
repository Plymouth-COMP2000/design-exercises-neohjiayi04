package com.example.dineo.guest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineo.R;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationActionListener listener;

    public interface OnReservationActionListener {
        void onEdit(Reservation reservation);
        void onCancel(Reservation reservation);
    }

    public ReservationAdapter(Context context, List<Reservation> reservations,
                              OnReservationActionListener listener) {
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

        holder.tvReservationId.setText(reservation.getId());
        holder.tvDate.setText(reservation.getDate());
        holder.tvTime.setText(reservation.getTime());
        holder.tvGuests.setText(reservation.getGuests() + " guests");
        holder.tvTable.setText(reservation.getTable());

        if (reservation.getRequests() != null && !reservation.getRequests().isEmpty()) {
            holder.tvRequests.setVisibility(View.VISIBLE);
            holder.tvRequests.setText("Requests: " + reservation.getRequests());
        } else {
            holder.tvRequests.setVisibility(View.GONE);
        }

        // Show/hide action buttons based on status
        if ("Upcoming".equals(reservation.getStatus())) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(reservation);
                }
            });

            holder.btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancel(reservation);
                }
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }
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
        CardView cardView;
        TextView tvReservationId, tvDate, tvTime, tvGuests, tvTable, tvRequests;
        MaterialButton btnEdit, btnCancel;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.reservationCard);
            tvReservationId = itemView.findViewById(R.id.tvReservationId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvGuests = itemView.findViewById(R.id.tvGuests);
            tvTable = itemView.findViewById(R.id.tvTable);
            tvRequests = itemView.findViewById(R.id.tvRequests);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}