package com.example.dineo.staff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dineo.R;
import java.util.Calendar;

public class StaffCalendarActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_calendar);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        calendarView = findViewById(R.id.calendarView);

        // Set minimum date to today
        calendarView.setMinDate(System.currentTimeMillis());
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_date", selectedDate.getTimeInMillis());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}