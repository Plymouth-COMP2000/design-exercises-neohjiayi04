package com.example.dineo.staff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineo.R;
import java.util.List;

public class StaffReservationAdapter extends RecyclerView.Adapter<StaffReservationAdapter.ViewHolder> {

    private Context context;
    private List<StaffReservation> reservations;
    private OnReservationClickListener listener;

    public interface OnReservationClickListener {
        void onReservationClick(StaffReservation reservation);
    }

    public StaffReservationAdapter(Context context, List<StaffReservation> reservations,
                                   OnReservationClickListener listener) {
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
    public void onBindViewHolder(@NonNon ViewHolder holder, int position) {
        StaffReservation reservation = reservations.get(position);

        holder.tvTime.setText(reservation.getTime());
        holder.tvGuestName.setText(reservation.getGuestName());
        holder.tvGuests.setText(reservation.getGuests() + " guests");
        holder.tvStatus.setText(reservation.getStatus());

        // Set status badge color and background based on status
        int statusColor;
        int statusBgColor;

        switch (reservation.getStatus()) {
            case "Upcoming":
                statusColor = context.getResources().getColor(R.color.status_upcoming_text);
                statusBgColor = context.getResources().getColor(R.color.status_upcoming_bg);
                break;
            case "Seated":
                statusColor = context.getResources().getColor(R.color.status_seated_text);
                statusBgColor = context.getResources().getColor(R.color.status_seated_bg);
                break;
            case "No-Show":
                statusColor = context.getResources().getColor(R.color.status_noshow_text);
                statusBgColor = context.getResources().getColor(R.color.status_noshow_bg);
                break;
            default:
                statusColor = context.getResources().getColor(R.color.text_secondary);
                statusBgColor = context.getResources().getColor(R.color.background);
                break;
        }

        holder.tvStatus.setTextColor(statusColor);

        // Create rounded background programmatically
        android.graphics.drawable.GradientDrawable badge = new android.graphics.drawable.GradientDrawable();
        badge.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        badge.setCornerRadius(40f);
        badge.setColor(statusBgColor);
        holder.tvStatus.setBackground(badge);

        // Click listener
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReservationClick(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTime, tvGuestName, tvGuests, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.reservationCard);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvGuestName = itemView.findViewById(R.id.tvGuestName);
            tvGuests = itemView.findViewById(R.id.tvGuests);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}