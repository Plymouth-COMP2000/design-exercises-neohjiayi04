package com.example.dineo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android:view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.Reservation;

import java.util.ArrayList;
import java.util.List;

public class CalendarReservationAdapter extends RecyclerView.Adapter<CalendarReservationAdapter.CalendarReservationViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationClickListener listener;

    public interface OnReservationClickListener {
        void onReservationClick(Reservation reservation);
    }

    public CalendarReservationAdapter(Context context, List<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations != null ? reservations : new ArrayList<>();
    }

    public void setOnReservationClickListener(OnReservationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_reservation, parent, false);
        return new CalendarReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarReservationViewHolder holder, int position) {
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

    class CalendarReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeName, tvGuests;
        View statusDot;

        public CalendarReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeName = itemView.findViewById(R.id.tv_time_name);
            tvGuests = itemView.findViewById(R.id.tv_guests);
            statusDot = itemView.findViewById(R.id.status_dot);

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
            // Set time and name
            tvTimeName.setText(reservation.getFormattedTime() + " - " + reservation.getCustomerName());

            // Set guests
            tvGuests.setText(reservation.getNumberOfPax() + " guests");

            // Set status dot color
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
            statusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(statusColor));
        }
    }
}