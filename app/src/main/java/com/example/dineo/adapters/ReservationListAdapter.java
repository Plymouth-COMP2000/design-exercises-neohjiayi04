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
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ReservationViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationClickListener listener;

    public interface OnReservationClickListener {
        void onReservationClick(Reservation reservation);
    }

    public ReservationListAdapter(Context context, List<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations != null ? reservations : new ArrayList<>();
    }

    public void setOnReservationClickListener(OnReservationClickListener listener) {
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
        TextView tvTimeHour, tvTimePeriod, tvCustomerName, tvGuests, tvStatus;
        MaterialCardView cardStatus;
        ImageView ivSpecialRequest;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeHour = itemView.findViewById(R.id.tv_time_hour);
            tvTimePeriod = itemView.findViewById(R.id.tv_time_period);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvGuests = itemView.findViewById(R.id.tv_guests);
            tvStatus = itemView.findViewById(R.id.tv_status);
            cardStatus = itemView.findViewById(R.id.card_status);
            ivSpecialRequest = itemView.findViewById(R.id.iv_special_request);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onReservationClick(reservations.get(position));
                    }
                }
            });
        }

        public void bind(Reservation reservation) {
            // Set time
            tvTimeHour.setText(reservation.getTimeHour());
            tvTimePeriod.setText(reservation.getTimePeriod());

            // Set customer name
            tvCustomerName.setText(reservation.getCustomerName());

            // Set guests
            tvGuests.setText(reservation.getNumberOfPax() + " guests");

            // Set status
            tvStatus.setText(reservation.getStatus());

            // Set status badge color
            int statusColor;
            switch (reservation.getStatus().toLowerCase()) {
                case "upcoming":
                    statusColor = ContextCompat.getColor(context, R.color.orange_accent);
                    break;
                case "confirmed":
                    statusColor = ContextCompat.getColor(context, R.color.success_green);
                    break;
                case "seated":
                    statusColor = ContextCompat.getColor(context, R.color.teal_accent);
                    break;
                case "no-show":
                case "cancelled":
                    statusColor = ContextCompat.getColor(context, R.color.error_red);
                    break;
                default:
                    statusColor = ContextCompat.getColor(context, R.color.text_secondary);
            }
            cardStatus.setCardBackgroundColor(statusColor);

            // Show special request icon if there are special requests
            if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) {
                ivSpecialRequest.setVisibility(View.VISIBLE);
            } else {
                ivSpecialRequest.setVisibility(View.GONE);
            }
        }
    }
}