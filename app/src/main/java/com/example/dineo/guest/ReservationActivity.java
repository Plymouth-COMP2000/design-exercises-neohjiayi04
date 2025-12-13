package com.example.dineo.guest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineo.GuestMenuActivity;
import com.example.dineo.ProfileActivity;
import com.example.dineo.R;
import com.example.dineo.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {

    private TextInputEditText etDate, etTime, etGuests, etTable, etRequests;
    private MaterialButton btnReserve;
    private ImageButton btnNotification;
    private TextView tabUpcoming, tabFinished;
    private RecyclerView reservationRecyclerView;
    private LinearLayout navMenu, navReservation, navProfile;

    private SessionManager sessionManager;
    private ReservationAdapter adapter;
    private List<Reservation> upcomingReservations = new ArrayList<>();
    private List<Reservation> finishedReservations = new ArrayList<>();
    private boolean showingUpcoming = true;

    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedTime = Calendar.getInstance();
    private int selectedGuests = 4;
    private String selectedTable = "Any";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        sessionManager = new SessionManager(this);

        initializeViews();
        setupClickListeners();
        loadReservations();
        setupRecyclerView();
    }

    private void initializeViews() {
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etGuests = findViewById(R.id.etGuests);
        etTable = findViewById(R.id.etTable);
        etRequests = findViewById(R.id.etRequests);
        btnReserve = findViewById(R.id.btnReserve);
        btnNotification = findViewById(R.id.btnNotification);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabFinished = findViewById(R.id.tabFinished);
        reservationRecyclerView = findViewById(R.id.reservationRecyclerView);
        navMenu = findViewById(R.id.navMenu);
        navReservation = findViewById(R.id.navReservation);
        navProfile = findViewById(R.id.navProfile);

        // Set default values
        etGuests.setText(String.valueOf(selectedGuests));
        etTable.setText(selectedTable);
    }

    private void setupClickListeners() {
        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Time picker
        etTime.setOnClickListener(v -> showTimePicker());

        // Guests picker
        etGuests.setOnClickListener(v -> showGuestsPicker());

        // Table picker
        etTable.setOnClickListener(v -> showTablePicker());

        // Reserve button
        btnReserve.setOnClickListener(v -> makeReservation());

        // Notification button
        btnNotification.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
        });

        // Tabs
        tabUpcoming.setOnClickListener(v -> switchToUpcoming());
        tabFinished.setOnClickListener(v -> switchToFinished());

        // Bottom Navigation
        navMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationActivity.this, GuestMenuActivity.class);
            startActivity(intent);
        });

        navReservation.setOnClickListener(v -> {
            // Already on reservation page
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    etDate.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    etTime.setText(sdf.format(selectedTime.getTime()));
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void showGuestsPicker() {
        String[] guestsOptions = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Number of Guests");
        builder.setItems(guestsOptions, (dialog, which) -> {
            selectedGuests = which + 1;
            etGuests.setText(String.valueOf(selectedGuests));
        });
        builder.show();
    }

    private void showTablePicker() {
        String[] tableOptions = {"Any", "Window Seat", "Patio Seat #12", "Private Room", "Bar Counter"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Table Preference");
        builder.setItems(tableOptions, (dialog, which) -> {
            selectedTable = tableOptions[which];
            etTable.setText(selectedTable);
        });
        builder.show();
    }

    private void makeReservation() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String requests = etRequests.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new reservation
        Reservation reservation = new Reservation(
                generateReservationId(),
                date,
                time,
                selectedGuests,
                selectedTable,
                requests,
                "Upcoming"
        );

        upcomingReservations.add(0, reservation);

        // Save to preferences (or API in real app)
        saveReservations();

        // Show success message
        showSuccessDialog();

        // Clear form
        clearForm();

        // Refresh list
        if (showingUpcoming) {
            adapter.updateReservations(upcomingReservations);
        }
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success!");
        builder.setMessage("Your reservation has been booked successfully!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void clearForm() {
        etDate.setText("");
        etTime.setText("");
        etGuests.setText("4");
        etTable.setText("Any");
        etRequests.setText("");
        selectedGuests = 4;
        selectedTable = "Any";
    }

    private void setupRecyclerView() {
        adapter = new ReservationAdapter(this, upcomingReservations,
                new ReservationAdapter.OnReservationActionListener() {
                    @Override
                    public void onEdit(Reservation reservation) {
                        editReservation(reservation);
                    }

                    @Override
                    public void onCancel(Reservation reservation) {
                        showCancelDialog(reservation);
                    }
                });

        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reservationRecyclerView.setAdapter(adapter);
    }

    private void editReservation(Reservation reservation) {
        Intent intent = new Intent(ReservationActivity.this, EditReservationActivity.class);
        intent.putExtra("reservation_id", reservation.getId());
        intent.putExtra("date", reservation.getDate());
        intent.putExtra("time", reservation.getTime());
        intent.putExtra("guests", reservation.getGuests());
        intent.putExtra("table", reservation.getTable());
        intent.putExtra("requests", reservation.getRequests());
        startActivityForResult(intent, 100);
    }

    private void showCancelDialog(Reservation reservation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Reservation");
        builder.setMessage("Are you sure you want to cancel this reservation?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            upcomingReservations.remove(reservation);
            saveReservations();
            adapter.updateReservations(upcomingReservations);
            Toast.makeText(this, "Reservation canceled", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void switchToUpcoming() {
        showingUpcoming = true;
        tabUpcoming.setBackgroundResource(R.drawable.tab_selected);
        tabUpcoming.setTextColor(getResources().getColor(R.color.white));
        tabFinished.setBackgroundResource(0);
        tabFinished.setTextColor(getResources().getColor(R.color.text_secondary));
        adapter.updateReservations(upcomingReservations);
    }

    private void switchToFinished() {
        showingUpcoming = false;
        tabFinished.setBackgroundResource(R.drawable.tab_selected);
        tabFinished.setTextColor(getResources().getColor(R.color.white));
        tabUpcoming.setBackgroundResource(0);
        tabUpcoming.setTextColor(getResources().getColor(R.color.text_secondary));
        adapter.updateReservations(finishedReservations);
    }

    private void loadReservations() {
        // TODO: Load from API or local database
        // For now, using dummy data

        // Sample upcoming reservation
        upcomingReservations.add(new Reservation(
                "RES001",
                "May 28, 2024",
                "7:30 PM",
                4,
                "Patio Seat #12",
                "Celebrating an anniversary",
                "Upcoming"
        ));

        // Sample finished reservation
        finishedReservations.add(new Reservation(
                "RES000",
                "May 20, 2024",
                "6:00 PM",
                2,
                "Window Seat",
                "",
                "Finished"
        ));
    }

    private void saveReservations() {
        // TODO: Save to API or local database
        // For now, just in memory
    }

    private String generateReservationId() {
        return "RES" + System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Reservation was edited
            String id = data.getStringExtra("reservation_id");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            int guests = data.getIntExtra("guests", 4);
            String table = data.getStringExtra("table");
            String requests = data.getStringExtra("requests");

            // Update the reservation
            for (Reservation res : upcomingReservations) {
                if (res.getId().equals(id)) {
                    res.setDate(date);
                    res.setTime(time);
                    res.setGuests(guests);
                    res.setTable(table);
                    res.setRequests(requests);
                    break;
                }
            }

            saveReservations();
            adapter.updateReservations(upcomingReservations);
            Toast.makeText(this, "Reservation updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
}