package com.example.dineo.reservation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.CalendarReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.dashboard.DashboardActivity;
import com.example.dineo.menu.StaffMenuActivity;
import com.example.dineo.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarReservationsActivity extends AppCompatActivity {

    private ImageView ivBack, ivPrevMonth, ivNextMonth;
    private TextView tvMonthYear, tvReservationsHeader;
    private CalendarView calendarView;
    private RecyclerView rvDateReservations;
    private MaterialCardView notificationButton;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper databaseHelper;
    private CalendarReservationAdapter adapter;
    private String selectedDate;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_reservations);

        calendar = Calendar.getInstance();
        initializeViews();
        setupRecyclerView();
        setupListeners();

        // Load today's reservations initially
        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sqlFormat.format(calendar.getTime());
        loadReservationsForDate(selectedDate);
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.iv_back);
        ivPrevMonth = findViewById(R.id.iv_prev_month);
        ivNextMonth = findViewById(R.id.iv_next_month);
        tvMonthYear = findViewById(R.id.tv_month_year);
        tvReservationsHeader = findViewById(R.id.tv_reservations_header);
        calendarView = findViewById(R.id.calendar_view);
        rvDateReservations = findViewById(R.id.rv_date_reservations);
        notificationButton = findViewById(R.id.notification_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        databaseHelper = new DatabaseHelper(this);

        updateMonthYearDisplay();
    }

    private void setupRecyclerView() {
        rvDateReservations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CalendarReservationAdapter(this, null);
        rvDateReservations.setAdapter(adapter);

        adapter.setOnReservationClickListener(reservation -> {
            Intent intent = new Intent(this, ReservationDetailsActivity.class);
            intent.putExtra("RESERVATION_ID", reservation.getId());
            startActivity(intent);
        });
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);
            SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());

            selectedDate = sqlFormat.format(selectedCal.getTime());
            tvReservationsHeader.setText("Reservations for " + displayFormat.format(selectedCal.getTime()));
            loadReservationsForDate(selectedDate);
        });

        ivPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendarAndDisplay();
        });

        ivNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendarAndDisplay();
        });

        notificationButton.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
        });

        // Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_reservations);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_menu) {
                startActivity(new Intent(this, StaffMenuActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_reservations) {
                finish(); // Go back to main reservations
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void updateCalendarAndDisplay() {
        calendarView.setDate(calendar.getTimeInMillis(), true, true);
        updateMonthYearDisplay();
    }

    private void updateMonthYearDisplay() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(monthFormat.format(calendar.getTime()));
    }

    private void loadReservationsForDate(String date) {
        List<Reservation> reservations = databaseHelper.getReservationsByDate(date);
        adapter.updateReservations(reservations);

        // Update header with count
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
        try {
            SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            java.util.Date dateObj = sqlFormat.parse(date);
            tvReservationsHeader.setText("Reservations for " + displayFormat.format(dateObj) +
                    " (" + reservations.size() + ")");
        } catch (Exception e) {
            tvReservationsHeader.setText("Reservations for " + date + " (" + reservations.size() + ")");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedDate != null) {
            loadReservationsForDate(selectedDate);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}