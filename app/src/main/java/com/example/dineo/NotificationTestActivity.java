package com.example.dineo.test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dineo.R;
import com.example.dineo.utils.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Test activity to demonstrate and test push notifications
 * Remove this in production
 */
public class NotificationTestActivity extends AppCompatActivity {

    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_test);

        notificationHelper = new NotificationHelper(this);

        setupTestButtons();
        getFCMToken();
    }

    private void setupTestButtons() {
        Button btnNewReservation = findViewById(R.id.btnTestNewReservation);
        Button btnUpdate = findViewById(R.id.btnTestUpdate);
        Button btnReminder = findViewById(R.id.btnTestReminder);
        Button btnCancellation = findViewById(R.id.btnTestCancellation);
        Button btnClearAll = findViewById(R.id.btnClearAll);

        btnNewReservation.setOnClickListener(v -> {
            notificationHelper.showNewReservationNotification(
                    "TEST001",
                    "John Doe",
                    "7:30 PM",
                    4
            );
            showToast("New reservation notification sent");
        });

        btnUpdate.setOnClickListener(v -> {
            notificationHelper.showReservationUpdateNotification(
                    "TEST002",
                    "Jane Smith",
                    "Seated"
            );
            showToast("Update notification sent");
        });

        btnReminder.setOnClickListener(v -> {
            notificationHelper.showUpcomingReservationReminder(
                    "TEST003",
                    "Mike Johnson",
                    "8:00 PM",
                    6
            );
            showToast("Reminder notification sent");
        });

        btnCancellation.setOnClickListener(v -> {
            notificationHelper.showCancellationNotification(
                    "Sarah Williams",
                    "9:00 PM"
            );
            showToast("Cancellation notification sent");
        });

        btnClearAll.setOnClickListener(v -> {
            notificationHelper.cancelAllNotifications();
            showToast("All notifications cleared");
        });
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        android.util.Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    android.util.Log.d("FCM", "FCM Token: " + token);

                    // Display token (remove in production)
                    showToast("FCM Token logged to console");
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}