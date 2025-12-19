package com.example.dineo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.dineo.models.MenuItem;
import com.example.dineo.models.Reservation;
import com.example.dineo.models.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper - Manages local SQLite database
 * Student ID: BSSE2506008
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dineo.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_MENU_ITEMS = "menu_items";
    private static final String TABLE_RESERVATIONS = "reservations";
    private static final String TABLE_NOTIFICATIONS = "notifications";

    // Reservation table columns
    private static final String COLUMN_RESERVATION_ID = "id";
    private static final String COLUMN_RESERVATION_CUSTOMER_NAME = "customer_name";
    private static final String COLUMN_RESERVATION_DATE = "date";
    private static final String COLUMN_RESERVATION_TIME = "time";
    private static final String COLUMN_RESERVATION_TABLE_NUMBER = "table_number";
    private static final String COLUMN_RESERVATION_GUESTS = "number_of_guests";
    private static final String COLUMN_RESERVATION_STATUS = "status";
    private static final String COLUMN_RESERVATION_USER_EMAIL = "user_email";
    private static final String COLUMN_RESERVATION_SPECIAL_REQUESTS = "special_requests";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create menu items table WITH CATEGORY
        String createMenuTable = "CREATE TABLE " + TABLE_MENU_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "price REAL, " +
                "image_url TEXT, " +
                "description TEXT, " +
                "category TEXT)";
        db.execSQL(createMenuTable);

        // Create reservations table
        String createReservationsTable = "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_name TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "table_number TEXT, " +
                "number_of_guests INTEGER, " +
                "status TEXT, " +
                "user_email TEXT, " +
                "special_requests TEXT)";
        db.execSQL(createReservationsTable);

        // Create notifications table
        String createNotificationsTable = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "message TEXT, " +
                "timestamp TEXT, " +
                "is_read INTEGER DEFAULT 0, " +
                "type TEXT, " +
                "user_email TEXT)";
        db.execSQL(createNotificationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);

        // Create tables again
        onCreate(db);
    }

    // ==================== MENU ITEMS METHODS ====================

    public long addMenuItem(MenuItem menuItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", menuItem.getName());
        values.put("price", menuItem.getPrice());
        values.put("image_url", menuItem.getImageUrl());
        values.put("description", menuItem.getDescription());
        values.put("category", menuItem.getCategory());
        long id = db.insert(TABLE_MENU_ITEMS, null, values);
        db.close();
        return id;
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MENU_ITEMS, null);

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem();
                item.setId(cursor.getInt(0));
                item.setName(cursor.getString(1));
                item.setPrice(cursor.getDouble(2));
                item.setImageUrl(cursor.getString(3));
                item.setDescription(cursor.getString(4));

                // Check if category column exists
                if (cursor.getColumnCount() > 5) {
                    item.setCategory(cursor.getString(5));
                }

                menuList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return menuList;
    }

    public int updateMenuItem(MenuItem menuItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", menuItem.getName());
        values.put("price", menuItem.getPrice());
        values.put("image_url", menuItem.getImageUrl());
        values.put("description", menuItem.getDescription());
        values.put("category", menuItem.getCategory());
        int result = db.update(TABLE_MENU_ITEMS, values, "id = ?",
                new String[]{String.valueOf(menuItem.getId())});
        db.close();
        return result;
    }

    public MenuItem getMenuItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        MenuItem item = null;

        Cursor cursor = db.query(
                TABLE_MENU_ITEMS,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            item = new MenuItem();
            item.setId(cursor.getInt(0));
            item.setName(cursor.getString(1));
            item.setPrice(cursor.getDouble(2));
            item.setImageUrl(cursor.getString(3));
            item.setDescription(cursor.getString(4));

            // Check if category column exists
            if (cursor.getColumnCount() > 5) {
                item.setCategory(cursor.getString(5));
            }

            cursor.close();
        }

        db.close();
        return item;
    }

    public void deleteMenuItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MENU_ITEMS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ==================== RESERVATIONS METHODS ====================

    public long addReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_name", reservation.getCustomerName());
        values.put("date", reservation.getDate());
        values.put("time", reservation.getTime());
        values.put("table_number", reservation.getTableNumber());
        values.put("number_of_guests", reservation.getNumberOfGuests());
        values.put("status", reservation.getStatus());
        values.put("user_email", reservation.getUserEmail());
        values.put("special_requests", reservation.getSpecialRequests());
        long id = db.insert(TABLE_RESERVATIONS, null, values);
        db.close();
        return id;
    }

    public List<Reservation> getUserReservations(String userEmail) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.query(
                    TABLE_RESERVATIONS,
                    null,
                    COLUMN_RESERVATION_USER_EMAIL + " = ?",
                    new String[]{userEmail},
                    null, null,
                    COLUMN_RESERVATION_DATE + " DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Reservation res = new Reservation();
                    res.setId(cursor.getInt(0));
                    res.setCustomerName(cursor.getString(1));
                    res.setDate(cursor.getString(2));
                    res.setTime(cursor.getString(3));
                    res.setTableNumber(cursor.getString(4));
                    res.setNumberOfGuests(cursor.getInt(5));
                    res.setStatus(cursor.getString(6));
                    res.setUserEmail(cursor.getString(7));
                    res.setSpecialRequests(cursor.getString(8));

                    reservations.add(res);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return reservations;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> resList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESERVATIONS, null);

        if (cursor.moveToFirst()) {
            do {
                Reservation res = new Reservation();
                res.setId(cursor.getInt(0));
                res.setCustomerName(cursor.getString(1));
                res.setDate(cursor.getString(2));
                res.setTime(cursor.getString(3));
                res.setTableNumber(cursor.getString(4));
                res.setNumberOfGuests(cursor.getInt(5));
                res.setStatus(cursor.getString(6));
                res.setUserEmail(cursor.getString(7));
                res.setSpecialRequests(cursor.getString(8));
                resList.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resList;
    }

    public int updateReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_name", reservation.getCustomerName());
        values.put("date", reservation.getDate());
        values.put("time", reservation.getTime());
        values.put("table_number", reservation.getTableNumber());
        values.put("number_of_guests", reservation.getNumberOfGuests());
        values.put("status", reservation.getStatus());
        values.put("special_requests", reservation.getSpecialRequests());
        int result = db.update(TABLE_RESERVATIONS, values, "id = ?",
                new String[]{String.valueOf(reservation.getId())});
        db.close();
        return result;
    }

    public void deleteReservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESERVATIONS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int cancelReservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "Cancelled");
        int result = db.update(TABLE_RESERVATIONS, values, "id = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // ==================== NOTIFICATION METHODS ====================

    public long addNotification(String title, String message, String timestamp, String type, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("message", message);
        values.put("timestamp", timestamp);
        values.put("is_read", 0);
        values.put("type", type);
        values.put("user_email", userEmail);
        long id = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
        return id;
    }

    public List<Notification> getUserNotifications(String userEmail) {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTIFICATIONS,
                null,
                "user_email = ?",
                new String[]{userEmail},
                null, null,
                "id DESC");

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                notification.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                notification.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                notification.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow("timestamp")));
                notification.setRead(cursor.getInt(cursor.getColumnIndexOrThrow("is_read")) == 1);
                notification.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                notification.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow("user_email")));
                notifications.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notifications;
    }

    public int getUnreadNotificationCount(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS +
                        " WHERE user_email = ? AND is_read = 0",
                new String[]{userEmail});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public void markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        db.update(TABLE_NOTIFICATIONS, values, "id = ?",
                new String[]{String.valueOf(notificationId)});
        db.close();
    }

    public void deleteNotification(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, "id = ?",
                new String[]{String.valueOf(notificationId)});
        db.close();
    }

    public void markAllNotificationsAsRead(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        db.update(TABLE_NOTIFICATIONS, values, "user_email = ?",
                new String[]{userEmail});
        db.close();
    }

    public void clearAllNotifications(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, "user_email = ?", new String[]{userEmail});
        db.close();
    }

    // ==================== HELPER METHODS ====================

    public List<String> getAllStaffEmails() {
        List<String> staffEmails = new ArrayList<>();
        staffEmails.add("admin@dineo.com");
        staffEmails.add("staff@dineo.com");
        return staffEmails;
    }
}