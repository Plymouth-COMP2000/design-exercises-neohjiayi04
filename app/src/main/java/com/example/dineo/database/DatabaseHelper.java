package com.example.dineo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.dineo.models.MenuItem;
import com.example.dineo.models.Notification;
import com.example.dineo.models.Reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dineo.db";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_RESERVATION = "reservation";
    private static final String TABLE_MENU = "menu";
    private static final String TABLE_NOTIFICATION = "notification";
    private static final String TABLE_USER = "user";

    // Reservation Columns
    private static final String RES_ID = "id";
    private static final String RES_NAME = "customerName";
    private static final String RES_EMAIL = "userEmail";
    private static final String RES_DATE = "date";
    private static final String RES_TIME = "time";
    private static final String RES_GUESTS = "numberOfGuests";
    private static final String RES_TABLE = "tableNumber";
    private static final String RES_SPECIAL = "specialRequests";
    private static final String RES_STATUS = "status";

    // Menu Columns
    private static final String MENU_ID = "id";
    private static final String MENU_NAME = "name";
    private static final String MENU_CATEGORY = "category";
    private static final String MENU_PRICE = "price";
    private static final String MENU_DESC = "description";
    private static final String MENU_IMAGE = "imageUrl";

    // Notification Columns
    private static final String NOTIF_ID = "id";
    private static final String NOTIF_TITLE = "title";
    private static final String NOTIF_MESSAGE = "message";
    private static final String NOTIF_TIMESTAMP = "timestamp";
    private static final String NOTIF_TYPE = "type";
    private static final String NOTIF_EMAIL = "userEmail";
    private static final String NOTIF_READ = "isRead";

    // User Columns
    private static final String USER_ID = "id";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";
    private static final String USER_NAME = "name";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Reservation Table
        String CREATE_RESERVATION_TABLE = "CREATE TABLE " + TABLE_RESERVATION + " (" +
                RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RES_NAME + " TEXT," +
                RES_EMAIL + " TEXT," +
                RES_DATE + " TEXT," +
                RES_TIME + " TEXT," +
                RES_GUESTS + " INTEGER," +
                RES_TABLE + " TEXT," +
                RES_SPECIAL + " TEXT," +
                RES_STATUS + " TEXT)";
        db.execSQL(CREATE_RESERVATION_TABLE);

        // Create Menu Table
        String CREATE_MENU_TABLE = "CREATE TABLE " + TABLE_MENU + " (" +
                MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MENU_NAME + " TEXT NOT NULL," +
                MENU_CATEGORY + " TEXT," +
                MENU_PRICE + " REAL," +
                MENU_DESC + " TEXT," +
                MENU_IMAGE + " TEXT)";
        db.execSQL(CREATE_MENU_TABLE);

        // Create Notification Table
        String CREATE_NOTIF_TABLE = "CREATE TABLE " + TABLE_NOTIFICATION + " (" +
                NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NOTIF_TITLE + " TEXT," +
                NOTIF_MESSAGE + " TEXT," +
                NOTIF_TIMESTAMP + " TEXT," +
                NOTIF_TYPE + " TEXT," +
                NOTIF_EMAIL + " TEXT," +
                NOTIF_READ + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_NOTIF_TABLE);

        // Create User Table
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USER_EMAIL + " TEXT UNIQUE," +
                USER_PASSWORD + " TEXT," +
                USER_NAME + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // ---------------- RESERVATION METHODS ----------------

    public long addReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RES_NAME, reservation.getCustomerName());
        values.put(RES_EMAIL, reservation.getUserEmail());
        values.put(RES_DATE, reservation.getDate());
        values.put(RES_TIME, reservation.getTime());
        values.put(RES_GUESTS, reservation.getNumberOfGuests());
        values.put(RES_TABLE, reservation.getTableNumber());
        values.put(RES_SPECIAL, reservation.getSpecialRequests());
        values.put(RES_STATUS, reservation.getStatus());

        long id = db.insert(TABLE_RESERVATION, null, values);
        db.close();
        return id;
    }

    public List<Reservation> getUserReservations(String email) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESERVATION, null, RES_EMAIL + "=?",
                new String[]{email}, null, null, RES_DATE + " ASC, " + RES_TIME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Reservation res = new Reservation();
                res.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RES_ID)));
                res.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(RES_NAME)));
                res.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(RES_EMAIL)));
                res.setDate(cursor.getString(cursor.getColumnIndexOrThrow(RES_DATE)));
                res.setTime(cursor.getString(cursor.getColumnIndexOrThrow(RES_TIME)));
                res.setNumberOfGuests(cursor.getInt(cursor.getColumnIndexOrThrow(RES_GUESTS)));
                res.setTableNumber(cursor.getString(cursor.getColumnIndexOrThrow(RES_TABLE)));
                res.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(RES_SPECIAL)));
                res.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RES_STATUS)));

                reservations.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reservations;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESERVATION, null, null, null, null, null,
                RES_DATE + " ASC, " + RES_TIME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Reservation res = new Reservation();
                res.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RES_ID)));
                res.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(RES_NAME)));
                res.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(RES_EMAIL)));
                res.setDate(cursor.getString(cursor.getColumnIndexOrThrow(RES_DATE)));
                res.setTime(cursor.getString(cursor.getColumnIndexOrThrow(RES_TIME)));
                res.setNumberOfGuests(cursor.getInt(cursor.getColumnIndexOrThrow(RES_GUESTS)));
                res.setTableNumber(cursor.getString(cursor.getColumnIndexOrThrow(RES_TABLE)));
                res.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(RES_SPECIAL)));
                res.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RES_STATUS)));

                reservations.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reservations;
    }

    /**
     * NEW METHOD: Get reservation by ID
     */
    public Reservation getReservationById(int reservationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Reservation reservation = null;

        Cursor cursor = db.query(TABLE_RESERVATION, null, RES_ID + "=?",
                new String[]{String.valueOf(reservationId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            reservation = new Reservation();
            reservation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RES_ID)));
            reservation.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(RES_NAME)));
            reservation.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(RES_EMAIL)));
            reservation.setDate(cursor.getString(cursor.getColumnIndexOrThrow(RES_DATE)));
            reservation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(RES_TIME)));
            reservation.setNumberOfGuests(cursor.getInt(cursor.getColumnIndexOrThrow(RES_GUESTS)));
            reservation.setTableNumber(cursor.getString(cursor.getColumnIndexOrThrow(RES_TABLE)));
            reservation.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(RES_SPECIAL)));
            reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RES_STATUS)));
            cursor.close();
        }

        db.close();
        return reservation;
    }

    /**
     * NEW METHOD: Get upcoming reservations (limited)
     */
    public List<Reservation> getUpcomingReservations(int limit) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String currentDate = getCurrentDate();

        Cursor cursor = db.query(
                TABLE_RESERVATION,
                null,
                RES_DATE + " >= ? AND " + RES_STATUS + " != ?",
                new String[]{currentDate, "Cancelled"},
                null,
                null,
                RES_DATE + " ASC, " + RES_TIME + " ASC",
                String.valueOf(limit)
        );

        if (cursor.moveToFirst()) {
            do {
                Reservation res = new Reservation();
                res.setId(cursor.getInt(cursor.getColumnIndexOrThrow(RES_ID)));
                res.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(RES_NAME)));
                res.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(RES_EMAIL)));
                res.setDate(cursor.getString(cursor.getColumnIndexOrThrow(RES_DATE)));
                res.setTime(cursor.getString(cursor.getColumnIndexOrThrow(RES_TIME)));
                res.setNumberOfGuests(cursor.getInt(cursor.getColumnIndexOrThrow(RES_GUESTS)));
                res.setTableNumber(cursor.getString(cursor.getColumnIndexOrThrow(RES_TABLE)));
                res.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(RES_SPECIAL)));
                res.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RES_STATUS)));

                reservations.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reservations;
    }

    /**
     * NEW METHOD: Get count of upcoming reservations
     */
    public int getUpcomingReservationsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = getCurrentDate();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RESERVATION +
                        " WHERE " + RES_DATE + " >= ? AND " + RES_STATUS + " != ?",
                new String[]{currentDate, "Cancelled"}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int updateReservationStatus(int reservationId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RES_STATUS, newStatus);
        int rows = db.update(TABLE_RESERVATION, values, RES_ID + "=?",
                new String[]{String.valueOf(reservationId)});
        db.close();
        return rows;
    }

    public int cancelReservation(int reservationId) {
        return updateReservationStatus(reservationId, "Cancelled");
    }

    public int markReservationSeated(int reservationId) {
        return updateReservationStatus(reservationId, "Seated");
    }

    public int updateReservation(Reservation res) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RES_NAME, res.getCustomerName());
        values.put(RES_DATE, res.getDate());
        values.put(RES_TIME, res.getTime());
        values.put(RES_GUESTS, res.getNumberOfGuests());
        values.put(RES_TABLE, res.getTableNumber());
        values.put(RES_SPECIAL, res.getSpecialRequests());
        values.put(RES_STATUS, res.getStatus());

        int rows = db.update(TABLE_RESERVATION, values, RES_ID + "=?",
                new String[]{String.valueOf(res.getId())});
        db.close();
        return rows;
    }

    // ---------------- MENU METHODS ----------------

    public long addMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MENU_NAME, item.getName());
        values.put(MENU_CATEGORY, item.getCategory());
        values.put(MENU_PRICE, item.getPrice());
        values.put(MENU_DESC, item.getDescription());
        values.put(MENU_IMAGE, item.getImageUrl());

        long id = db.insert(TABLE_MENU, null, values);
        db.close();
        return id;
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MENU, null);

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MENU_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(MENU_NAME)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(MENU_CATEGORY)));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(MENU_PRICE)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(MENU_DESC)));
                item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(MENU_IMAGE)));

                menuItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return menuItems;
    }

    /**
     * NEW METHOD: Get menu items count
     */
    public int getMenuItemsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MENU, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int updateMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MENU_NAME, item.getName());
        values.put(MENU_CATEGORY, item.getCategory());
        values.put(MENU_PRICE, item.getPrice());
        values.put(MENU_DESC, item.getDescription());
        values.put(MENU_IMAGE, item.getImageUrl());

        int rows = db.update(TABLE_MENU, values, MENU_ID + "=?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public int deleteMenuItem(int menuItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_MENU, MENU_ID + "=?",
                new String[]{String.valueOf(menuItemId)});
        db.close();
        return rows;
    }

    // ---------------- NOTIFICATION METHODS ----------------

    public long addNotification(String title, String message, String timestamp, String type, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTIF_TITLE, title);
        values.put(NOTIF_MESSAGE, message);
        values.put(NOTIF_TIMESTAMP, timestamp);
        values.put(NOTIF_TYPE, type);
        values.put(NOTIF_EMAIL, userEmail);
        values.put(NOTIF_READ, 0);

        long id = db.insert(TABLE_NOTIFICATION, null, values);
        db.close();
        return id;
    }

    public int getUnreadNotificationCount(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_NOTIFICATION +
                        " WHERE " + NOTIF_EMAIL + "=? AND " + NOTIF_READ + "=0",
                new String[]{userEmail});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public List<Notification> getUserNotifications(String userEmail) {
        List<Notification> notificationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NOTIFICATION,
                null,
                NOTIF_EMAIL + "=?",
                new String[]{userEmail},
                null,
                null,
                NOTIF_TIMESTAMP + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NOTIF_ID)));
                notification.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NOTIF_TITLE)));
                notification.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(NOTIF_MESSAGE)));
                notification.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(NOTIF_READ)) == 1);
                notification.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(NOTIF_TIMESTAMP)));
                notification.setType(cursor.getString(cursor.getColumnIndexOrThrow(NOTIF_TYPE)));

                notificationList.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notificationList;
    }

    public int markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTIF_READ, 1);
        int rows = db.update(TABLE_NOTIFICATION, values, NOTIF_ID + "=?",
                new String[]{String.valueOf(notificationId)});
        db.close();
        return rows;
    }

    public int deleteNotification(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NOTIFICATION, NOTIF_ID + "=?",
                new String[]{String.valueOf(notificationId)});
        db.close();
        return rows;
    }

    // ---------------- USER METHODS ----------------

    public long addUser(String email, String password, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, password);
        values.put(USER_NAME, name);

        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null,
                USER_EMAIL + "=? AND " + USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // ---------------- UTILITY METHODS ----------------

    /**
     * NEW METHOD: Get current timestamp in standard format
     */
    public String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * NEW METHOD: Get current date in standard format
     */
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}