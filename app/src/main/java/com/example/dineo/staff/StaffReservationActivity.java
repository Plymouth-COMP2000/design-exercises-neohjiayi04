package com.example.dineo.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineo.R;
import com.example.dineo.utils.NotificationHelper;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaffReservationActivity extends AppCompatActivity
        implements StaffReservationAdapter.OnReservationClickListener {

    private ImageButton btnBack, btnNotification, btnCalendar;
    private TextView tvCurrentDate, tvReservationCount;
    private TextView filterUpcoming, filterSeated, filterNoShow;
    private RecyclerView reservationRecyclerView;
    private LinearLayout navDashboard, navMenu, navReservation, navProfile;

    private StaffReservationAdapter adapter;
    private List<StaffReservation> allReservations;
    private List<StaffReservation> filteredReservations;
    private String currentFilter = "Upcoming";

    private NotificationHelper notificationHelper;
    private TextView tvNotificationBadge;

    private static final int CALENDAR_REQUEST_CODE = 100;
    private static final int DETAIL_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_reservation);

        initializeViews();
        setupRecyclerView();
        loadReservations();
        setupClickListeners();
        updateCurrentDate();
        applyFilter("Upcoming");

        // Initialize notification helper
        notificationHelper = new NotificationHelper(this);
        updateNotificationBadge();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnNotification = findViewById(R.id.btnNotification);
        btnCalendar = findViewById(R.id.btnCalendar);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvReservationCount = findViewById(R.id.tvReservationCount);

        filterUpcoming = findViewById(R.id.filterUpcoming);
        filterSeated = findViewById(R.id.filterSeated);
        filterNoShow = findViewById(R.id.filterNoShow);

        reservationRecyclerView = findViewById(R.id.reservationRecyclerView);

        navDashboard = findViewById(R.id.navDashboard);
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupRecyclerView() {
        allReservations = new ArrayList<>();
        filteredReservations = new ArrayList<>();

        adapter = new StaffReservationAdapter(this, filteredReservations, this);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reservationRecyclerView.setAdapter(adapter);
    }

    private void loadReservations() {
        // Sample data - replace with actual API call
        allReservations.add(new StaffReservation("1", "7:30", "Jane Doe", 4, "Upcoming"));
        allReservations.add(new StaffReservation("2", "8:00", "John Smith", 2, "Upcoming"));
        allReservations.add(new StaffReservation("3", "6:45", "Emily Johnson", 6, "Seated"));
        allReservations.add(new StaffReservation("4", "7:15", "Michael Brown", 3, "Seated"));
        allReservations.add(new StaffReservation("5", "8:30", "Sarah Davis", 5, "Upcoming"));
        allReservations.add(new StaffReservation("6", "6:00", "David Wilson", 2, "No-Show"));
        allReservations.add(new StaffReservation("7", "7:45", "Lisa Anderson", 4, "Upcoming"));
        allReservations.add(new StaffReservation("8", "9:00", "Tom Martinez", 8, "Upcoming"));

        tvReservationCount.setText(allReservations.size() + " reservations");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCalendar.setOnClickListener(v -> openCalendarView());

        btnNotification.setOnClickListener(v -> openNotifications());

        // Filter buttons
        filterUpcoming.setOnClickListener(v -> applyFilter("Upcoming"));
        filterSeated.setOnClickListener(v -> applyFilter("Seated"));
        filterNoShow.setOnClickListener(v -> applyFilter("No-Show"));

        // Bottom navigation
        navDashboard.setOnClickListener(v -> {
            // TODO: Navigate to dashboard
        });

        navMenu.setOnClickListener(v -> {
            // TODO: Navigate to menu
        });

        navReservation.setOnClickListener(v -> {
            // Already on reservation page
        });

        navProfile.setOnClickListener(v -> {
            // TODO: Navigate to profile
        });
    }

    private void applyFilter(String filter) {
        currentFilter = filter;

        // Update filter button styles
        resetFilterStyles();

        // Move underline indicator
        View underline = findViewById(R.id.filterUnderline);
        android.view.ViewGroup.MarginLayoutParams params =
                (android.view.ViewGroup.MarginLayoutParams) underline.getLayoutParams();

        switch (filter) {
            case "Upcoming":
                filterUpcoming.setTextColor(getResources().getColor(R.color.primary));
                filterUpcoming.setTypeface(null, android.graphics.Typeface.BOLD);
                params.leftMargin = dpToPx(40);
                filteredReservations.clear();
                for (StaffReservation res : allReservations) {
                    if ("Upcoming".equals(res.getStatus())) {
                        filteredReservations.add(res);
                    }
                }
                break;

            case "Seated":
                filterSeated.setTextColor(getResources().getColor(R.color.primary));
                filterSeated.setTypeface(null, android.graphics.Typeface.BOLD);
                params.leftMargin = dpToPx(165);
                filteredReservations.clear();
                for (StaffReservation res : allReservations) {
                    if ("Seated".equals(res.getStatus())) {
                        filteredReservations.add(res);
                    }
                }
                break;

            case "No-Show":
                filterNoShow.setTextColor(getResources().getColor(R.color.primary));
                filterNoShow.setTypeface(null, android.graphics.Typeface.BOLD);
                params.leftMargin = dpToPx(290);
                filteredReservations.clear();
                for (StaffReservation res : allReservations) {
                    if ("No-Show".equals(res.getStatus())) {
                        filteredReservations.add(res);
                    }
                }
                break;
        }

        underline.setLayoutParams(params);
        adapter.notifyDataSetChanged();
    }

    private void resetFilterStyles() {
        filterUpcoming.setTextColor(getResources().getColor(R.color.text_secondary));
        filterUpcoming.setTypeface(null, android.graphics.Typeface.NORMAL);

        filterSeated.setTextColor(getResources().getColor(R.color.text_secondary));
        filterSeated.setTypeface(null, android.graphics.Typeface.NORMAL);

        filterNoShow.setTextColor(getResources().getColor(R.color.text_secondary));
        filterNoShow.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void updateCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        tvCurrentDate.setText(currentDate);
    }

    private void openCalendarView() {
        Intent intent = new Intent(this, StaffCalendarActivity.class);
        startActivityForResult(intent, CALENDAR_REQUEST_CODE);
    }

    private void openNotifications() {
        // Clear notification badge
        notificationHelper.cancelAllNotifications();
        updateNotificationBadge();

        // TODO: Open notifications activity
        // Intent intent = new Intent(this, NotificationsActivity.class);
        // startActivity(intent);
    }

    private void updateNotificationBadge() {
        int count = notificationHelper.getNotificationCount();
        // You can add a badge view here or update an existing TextView
        // For now, just log it
        android.util.Log.d("Notifications", "Badge count: " + count);
    }

    @Override
    public void onReservationClick(StaffReservation reservation) {
        Intent intent = new Intent(this, StaffReservationDetailsActivity.class);
        intent.putExtra("reservation_id", reservation.getId());
        intent.putExtra("time", reservation.getTime());
        intent.putExtra("guest_name", reservation.getGuestName());
        intent.putExtra("guests", reservation.getGuests());
        intent.putExtra("status", reservation.getStatus());
        startActivityForResult(intent, DETAIL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CALENDAR_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                long selectedDate = data.getLongExtra("selected_date", 0);
                Date date = new Date(selectedDate);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
                tvCurrentDate.setText(sdf.format(date));

                // TODO: Load reservations for selected date
            }
        } else if (requestCode == DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh reservations after detail view changes
            loadReservations();
            applyFilter(currentFilter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
    }

    /**
     * Simulate receiving a new reservation (for testing)
     */
    public void simulateNewReservation() {
        notificationHelper.showNewReservationNotification(
                "TEST123",
                "Test Customer",
                "8:30 PM",
                4
        );
        updateNotificationBadge();
    }
}