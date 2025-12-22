package com.example.dineo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineo.R;
import com.example.dineo.adapters.ReservationAdapter;
import com.example.dineo.database.DatabaseHelper;
import com.example.dineo.models.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ReservationActivity - GUEST ONLY with Enhanced Security
 * NOW WITH SIMPLIFIED MINIMALIST SUCCESS POPUP! ✨
 */
public class ReservationActivity extends AppCompatActivity {

    private static final int EDIT_RESERVATION_REQUEST = 100;

    private EditText editTextDate, editTextTime, editTextGuests, editTextSpecialRequests;
    private Spinner spinnerTable;
    private Button btnReserve;
    private ImageView imageViewNotification;
    private RecyclerView recyclerViewReservations;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigationView;

    private ReservationAdapter reservationAdapter;
    private DatabaseHelper databaseHelper;
    private List<Reservation> reservationList;

    private String userEmail = "";
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Get user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        userEmail = getUserEmail(prefs);
        userName = getUserName(prefs);

        // Security check: Verify user is logged in
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        initializeViews();

        // Set up listeners
        setupClickListeners();

        // Setup table spinner
        setupTableSpinner();

        // Setup tabs
        setupTabs();

        // Setup bottom navigation
        setupBottomNavigation();

        // Load initial data (upcoming reservations)
        loadReservations("upcoming");
    }

    /**
     * Get user email from SharedPreferences
     */
    private String getUserEmail(SharedPreferences prefs) {
        String email = prefs.getString("userEmail", "");

        // Fallback: Try user_json
        if (email.isEmpty()) {
            String userJson = prefs.getString("user_json", null);
            if (userJson != null) {
                try {
                    JSONObject json = new JSONObject(userJson);
                    email = json.optString("email", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return email;
    }

    /**
     * Get user name from SharedPreferences
     */
    private String getUserName(SharedPreferences prefs) {
        String name = prefs.getString("userName", "Guest");

        // Fallback: Try user_json
        if (name.equals("Guest")) {
            String userJson = prefs.getString("user_json", null);
            if (userJson != null) {
                try {
                    JSONObject json = new JSONObject(userJson);
                    String firstName = json.optString("firstname", "");
                    String lastName = json.optString("lastname", "");
                    if (!firstName.isEmpty()) {
                        name = firstName + " " + lastName;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return name;
    }

    private void initializeViews() {
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextGuests = findViewById(R.id.editTextGuests);
        editTextSpecialRequests = findViewById(R.id.editTextSpecialRequests);
        spinnerTable = findViewById(R.id.spinnerTable);
        btnReserve = findViewById(R.id.btnReserve);
        imageViewNotification = findViewById(R.id.imageViewNotification);
        recyclerViewReservations = findViewById(R.id.recyclerViewReservations);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setup RecyclerView
        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(this));
        reservationList = new ArrayList<>();
    }

    private void setupClickListeners() {
        if (imageViewNotification != null) {
            imageViewNotification.setOnClickListener(v ->
                    startActivity(new Intent(this, NotificationActivity.class))
            );
        }

        btnReserve.setOnClickListener(v -> createReservation());
        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTime.setOnClickListener(v -> showTimePicker());
    }

    private void setupTableSpinner() {
        String[] tables = {
                "Any Table",
                "Indoor - Table 1 (2 seats)",
                "Indoor - Table 2 (4 seats)",
                "Indoor - Table 3 (4 seats)",
                "Outdoor - Table 4 (6 seats)",
                "Outdoor - Table 5 (6 seats)",
                "Outdoor - Table 6 (8 seats)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tables);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTable.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("UPCOMING"));
        tabLayout.addTab(tabLayout.newTab().setText("FINISHED"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadReservations("upcoming");
                } else {
                    loadReservations("finished");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_reservation) {
                return true;
            }

            if (itemId == R.id.nav_menu) {
                Intent intent = new Intent(this, GuestMenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }

            if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_reservation);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    editTextDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    editTextTime.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);

        timePickerDialog.show();
    }

    private void createReservation() {
        // Validation
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String guestsStr = editTextGuests.getText().toString().trim();

        if (date.isEmpty()) {
            editTextDate.setError("Please select a date");
            return;
        }
        if (time.isEmpty()) {
            editTextTime.setError("Please select a time");
            return;
        }
        if (guestsStr.isEmpty()) {
            editTextGuests.setError("Please enter number of guests");
            return;
        }

        int guests;
        try {
            guests = Integer.parseInt(guestsStr);
            if (guests <= 0) {
                editTextGuests.setError("Number of guests must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            editTextGuests.setError("Please enter a valid number");
            return;
        }

        String specialRequests = editTextSpecialRequests.getText().toString().trim();
        String tableSelection = spinnerTable.getSelectedItem().toString();

        // Create reservation object
        Reservation reservation = new Reservation();
        reservation.setCustomerName(userName);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setNumberOfGuests(guests);
        reservation.setTableNumber(tableSelection);
        reservation.setSpecialRequests(specialRequests);
        reservation.setStatus("Pending");
        reservation.setUserEmail(userEmail); // SECURITY: Set owner

        long result = databaseHelper.addReservation(reservation);

        if (result > 0) {
            createNotification("Reservation Created",
                    "Your reservation for " + date + " at " + time + " has been created.");

            // ✅ Show simplified minimalist success dialog
            showBeautifulSuccessDialog(date, time, guests, tableSelection);

            clearInputs();
            loadReservations("upcoming");
        } else {
            Toast.makeText(this, "Failed to create reservation", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotification(String title, String message, String... extras) {
        String timestamp = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
                .format(new Date());
        String type = (extras.length > 0) ? extras[0] : "reservation";
        databaseHelper.addNotification(title, message, timestamp, type, userEmail);
    }

    /**
     * ✅ SIMPLIFIED: Beautiful Minimalist Success Dialog
     * Clean design with clear text and single action button
     */
    private void showBeautifulSuccessDialog(String date, String time, int guests, String table) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_reservation_success, null);

        // Find views in dialog
        TextView textViewDateTime = dialogView.findViewById(R.id.textViewDateTime);
        TextView textViewGuests = dialogView.findViewById(R.id.textViewGuests);
        TextView textViewTable = dialogView.findViewById(R.id.textViewTable);
        MaterialButton btnOk = dialogView.findViewById(R.id.btnOk);

        // Set reservation details with clear text
        textViewDateTime.setText(date + " at " + time);
        textViewGuests.setText(guests + " Guest" + (guests > 1 ? "s" : ""));
        textViewTable.setText(table);

        // Create dialog with transparent background
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Make background transparent for rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // OK button - dismiss and refresh
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            // Ensure upcoming tab is selected
            if (tabLayout.getSelectedTabPosition() != 0) {
                tabLayout.selectTab(tabLayout.getTabAt(0));
            }
        });

        dialog.show();
    }

    private void clearInputs() {
        editTextDate.setText("");
        editTextTime.setText("");
        editTextGuests.setText("");
        editTextSpecialRequests.setText("");
        spinnerTable.setSelection(0);
    }

    /**
     * SECURITY: Load ONLY user's own reservations
     */
    private void loadReservations(String type) {
        // Query with userEmail to ensure privacy
        List<Reservation> allReservations = databaseHelper.getUserReservations(userEmail);
        reservationList.clear();

        for (Reservation res : allReservations) {
            String status = res.getStatus();
            if ("upcoming".equals(type)) {
                if ("Pending".equals(status) || "Confirmed".equals(status)) {
                    reservationList.add(res);
                }
            } else { // "finished"
                if ("Cancelled".equals(status) || "Completed".equals(status)) {
                    reservationList.add(res);
                }
            }
        }

        if (reservationAdapter == null) {
            reservationAdapter = new ReservationAdapter(this, reservationList,
                    new ReservationAdapter.OnReservationClickListener() {
                        @Override
                        public void onCancelClick(Reservation reservation) {
                            cancelReservation(reservation);
                        }

                        @Override
                        public void onEditClick(Reservation reservation) {
                            editReservation(reservation);
                        }
                    });
            recyclerViewReservations.setAdapter(reservationAdapter);
        } else {
            reservationAdapter.notifyDataSetChanged();
        }
    }

    /**
     * SECURITY: Edit reservation with ownership verification
     */
    private void editReservation(Reservation reservation) {
        // Double check ownership
        if (!reservation.getUserEmail().equals(userEmail)) {
            Toast.makeText(this, "Cannot edit: Not your reservation", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cannot edit cancelled or completed reservations
        if ("Cancelled".equalsIgnoreCase(reservation.getStatus()) ||
                "Completed".equalsIgnoreCase(reservation.getStatus())) {
            Toast.makeText(this, "Cannot edit " + reservation.getStatus().toLowerCase() +
                    " reservation", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, EditReservationActivity.class);
        intent.putExtra("RESERVATION_ID", reservation.getId());
        startActivityForResult(intent, EDIT_RESERVATION_REQUEST);
    }

    /**
     * SECURITY: Cancel reservation with ownership verification
     */
    private void cancelReservation(final Reservation reservation) {
        // Double check ownership
        if (!reservation.getUserEmail().equals(userEmail)) {
            Toast.makeText(this, "Cannot cancel: Not your reservation", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int result = databaseHelper.cancelReservation(reservation.getId());
                    if (result > 0) {
                        Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                        createNotification(
                                "Reservation Cancelled",
                                "Your reservation for " + reservation.getDate() + " has been cancelled.",
                                "cancellation"
                        );
                        String currentTab = (tabLayout.getSelectedTabPosition() == 0) ?
                                "upcoming" : "finished";
                        loadReservations(currentTab);
                    } else {
                        Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_RESERVATION_REQUEST && resultCode == RESULT_OK) {
            // Refresh reservations after edit
            String currentTab = (tabLayout.getSelectedTabPosition() == 0) ? "upcoming" : "finished";
            loadReservations(currentTab);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh reservations when returning to activity
        String currentTab = (tabLayout.getSelectedTabPosition() == 0) ? "upcoming" : "finished";
        loadReservations(currentTab);
    }
}