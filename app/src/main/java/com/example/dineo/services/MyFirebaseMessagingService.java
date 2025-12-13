package com.example.dineo.services;

import android.util.Log;
import com.example.dineo.utils.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage);
        }

        // Check if message contains notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage);
        }
    }

    /**
     * Handle data messages from FCM
     */
    private void handleDataMessage(RemoteMessage remoteMessage) {
        try {
            String type = remoteMessage.getData().get("type");
            NotificationHelper notificationHelper = new NotificationHelper(this);

            if (type == null) return;

            switch (type) {
                case "new_reservation":
                    handleNewReservation(remoteMessage, notificationHelper);
                    break;

                case "reservation_update":
                    handleReservationUpdate(remoteMessage, notificationHelper);
                    break;

                case "upcoming_reminder":
                    handleUpcomingReminder(remoteMessage, notificationHelper);
                    break;

                case "cancellation":
                    handleCancellation(remoteMessage, notificationHelper);
                    break;

                default:
                    Log.d(TAG, "Unknown notification type: " + type);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling data message", e);
        }
    }

    /**
     * Handle notification messages from FCM
     */
    private void handleNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        NotificationHelper notificationHelper = new NotificationHelper(this);
        // Default notification for simple messages
        notificationHelper.showReservationUpdateNotification(
                String.valueOf(System.currentTimeMillis()),
                title != null ? title : "Notification",
                body != null ? body : ""
        );
    }

    /**
     * Handle new reservation notification
     */
    private void handleNewReservation(RemoteMessage remoteMessage, NotificationHelper helper) {
        String reservationId = remoteMessage.getData().get("reservation_id");
        String guestName = remoteMessage.getData().get("guest_name");
        String time = remoteMessage.getData().get("time");
        String guestsStr = remoteMessage.getData().get("guests");

        int guests = guestsStr != null ? Integer.parseInt(guestsStr) : 0;

        helper.showNewReservationNotification(reservationId, guestName, time, guests);
    }

    /**
     * Handle reservation update notification
     */
    private void handleReservationUpdate(RemoteMessage remoteMessage, NotificationHelper helper) {
        String reservationId = remoteMessage.getData().get("reservation_id");
        String guestName = remoteMessage.getData().get("guest_name");
        String status = remoteMessage.getData().get("status");

        helper.showReservationUpdateNotification(reservationId, guestName, status);
    }

    /**
     * Handle upcoming reservation reminder
     */
    private void handleUpcomingReminder(RemoteMessage remoteMessage, NotificationHelper helper) {
        String reservationId = remoteMessage.getData().get("reservation_id");
        String guestName = remoteMessage.getData().get("guest_name");
        String time = remoteMessage.getData().get("time");
        String guestsStr = remoteMessage.getData().get("guests");

        int guests = guestsStr != null ? Integer.parseInt(guestsStr) : 0;

        helper.showUpcomingReservationReminder(reservationId, guestName, time, guests);
    }

    /**
     * Handle cancellation notification
     */
    private void handleCancellation(RemoteMessage remoteMessage, NotificationHelper helper) {
        String guestName = remoteMessage.getData().get("guest_name");
        String time = remoteMessage.getData().get("time");

        helper.showCancellationNotification(guestName, time);
    }

    /**
     * Called when a new FCM token is generated
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);

        // Send token to your server
        sendTokenToServer(token);
    }

    /**
     * Send FCM token to your backend server
     */
    private void sendTokenToServer(String token) {
        // TODO: Implement sending token to your backend
        // Example: Save to SharedPreferences or send via API
        Log.d(TAG, "Sending token to server: " + token);
    }
}