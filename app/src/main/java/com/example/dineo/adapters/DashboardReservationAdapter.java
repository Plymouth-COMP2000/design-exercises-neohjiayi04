package com.example.dineo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.models.Reservation;

import java.util.List;

public class DashboardReservationAdapter extends RecyclerView.Adapter<DashboardReservationAdapter.ViewHolder> {

    private final List<Reservation> reservations;

    public DashboardReservationAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public DashboardReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardReservationAdapter.ViewHolder holder, int position) {
        Reservation res = reservations.get(position);
        holder.textCustomerName.setText(res.getCustomerName());
        holder.textReservationInfo.setText(res.getDate() + " " + res.getTime() + " | " + res.getStatus());
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCustomerName, textReservationInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCustomerName = itemView.findViewById(R.id.textCustomerName);
            textReservationInfo = itemView.findViewById(R.id.textReservationInfo);
        }
    }
}
