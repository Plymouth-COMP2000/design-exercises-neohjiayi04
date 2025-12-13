package com.example.dineo.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.dineo.R;
import com.example.dineo.staff.StaffReservationActivity;
import com.example.dineo.staff.StaffReservationDetailsActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "reservation_notifications";
    private static final String CHANNEL_NAME = "Reservation Notifications";
    private static final String CHANNEL_DESC = "Notifications for new and updated reservations";

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    /**
     * Create notification channel (required for Android 8.0+)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription(CHANNEL_DESC);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 400, 200, 400});
            channel.setShowBadge(true);

            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Show notification for new reservation
     */
    public void showNewReservationNotification(String reservationId, String guestName,
                                               String time, int guests) {
        String title = "New Reservation";
        String message = guestName + " - " + guests + " guests at " + time;

        Intent intent = new Intent(context, StaffReservationDetailsActivity.class);
        intent.putExtra("reservation_id", reservationId);
        intent.putExtra("guest_name", guestName);
        intent.putExtra("time", time);
        intent.putExtra("guests", guests);
        intent.putExtra("status", "Upcoming");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                reservationId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        showNotification(1001, title, message, pendingIntent, true);
    }

    /**
     * Show notification for reservation update
     */
    public void showReservationUpdateNotification(String reservationId, String guestName,
                                                  String status) {
        String title = "Reservation Updated";
        String message = guestName + "'s reservation is now " + status;

        Intent intent = new Intent(context, StaffReservationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                reservationId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        showNotification(1002, title, message, pendingIntent, false);
    }

    /**
     * Show notification for upcoming reservation reminder
     */
    public void showUpcomingReservationReminder(String reservationId, String guestName,
                                                String time, int guests) {
        String title = "Upcoming Reservation";
        String message = guestName + " - " + guests + " guests arriving at " + time;

        Intent intent = new Intent(context, StaffReservationDetailsActivity.class);
        intent.putExtra("reservation_id", reservationId);
        intent.putExtra("guest_name", guestName);
        intent.putExtra("time", time);
        intent.putExtra("guests", guests);
        intent.putExtra("status", "Upcoming");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                reservationId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        showNotification(1003, title, message, pendingIntent, true);
    }

    /**
     * Show notification for cancelled reservation
     */
    public void showCancellationNotification(String guestName, String time) {
        String title = "Reservation Cancelled";
        String message = guestName + "'s reservation at " + time + " has been cancelled";

        Intent intent = new Intent(context, StaffReservationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        showNotification(1004, title, message, pendingIntent, true);
    }

    /**
     * Core notification builder
     */
    private void showNotification(int notificationId, String title, String message,
                                  PendingIntent pendingIntent, boolean withSound) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reservation)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(context.getResources().getColor(R.color.primary));

        if (withSound) {
            builder.setSound(soundUri);
            builder.setVibrate(new long[]{0, 400, 200, 400});
        }

        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * Cancel a specific notification
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    /**
     * Cancel all notifications
     */
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }

    /**
     * Get notification badge count
     */
    public int getNotificationCount() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return notificationManager.getActiveNotifications().length;
        }
        return 0;
    }
}