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
 * DatabaseHelper - SQLite DB for Dineo app
 * Handles menu items, reservations, and notifications
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dineo.db";
    private static final int DATABASE_VERSION = 2; // Increase when schema changes

    // Menu table
    private static final String TABLE_MENU = "menu";
    private static final String COL_MENU_ID = "id";
    private static final String COL_MENU_NAME = "name";
    private static final String COL_MENU_PRICE = "price";
    private static final String COL_MENU_DESC = "description";
    private static final String COL_MENU_CATEGORY = "category";
    private static final String COL_MENU_IMAGE = "imageUrl";

    // Reservation table
    private static final String TABLE_RESERVATION = "reservation";
    private static final String COL_RES_ID = "id";
    private static final String COL_RES_CUSTOMER = "customerName";
    private static final String COL_RES_DATE = "date";
    private static final String COL_RES_TIME = "time";
    private static final String COL_RES_GUESTS = "numberOfGuests";
    private static final String COL_RES_TABLE = "tableNumber";
    private static final String COL_RES_REQUESTS = "specialRequests";
    private static final String COL_RES_STATUS = "status";
    private static final String COL_RES_USER_EMAIL = "userEmail";

    // Notification table
    private static final String TABLE_NOTIFICATION = "notification";
    private static final String COL_NOTIF_ID = "id";
    private static final String COL_NOTIF_TITLE = "title";
    private static final String COL_NOTIF_MESSAGE = "message";
    private static final String COL_NOTIF_TIMESTAMP = "timestamp";
    private static final String COL_NOTIF_TYPE = "type";
    private static final String COL_NOTIF_USER_EMAIL = "userEmail";
    private static final String COL_NOTIF_READ = "isRead";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Menu table
        String createMenuTable = "CREATE TABLE " + TABLE_MENU + " (" +
                COL_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MENU_NAME + " TEXT, " +
                COL_MENU_PRICE + " REAL, " +
                COL_MENU_DESC + " TEXT, " +
                COL_MENU_CATEGORY + " TEXT, " +
                COL_MENU_IMAGE + " TEXT)";
        db.execSQL(createMenuTable);

        // Reservation table
        String createReservationTable = "CREATE TABLE " + TABLE_RESERVATION + " (" +
                COL_RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RES_CUSTOMER + " TEXT, " +
                COL_RES_DATE + " TEXT, " +
                COL_RES_TIME + " TEXT, " +
                COL_RES_GUESTS + " INTEGER, " +
                COL_RES_TABLE + " TEXT, " +
                COL_RES_REQUESTS + " TEXT, " +
                COL_RES_STATUS + " TEXT, " +
                COL_RES_USER_EMAIL + " TEXT)";
        db.execSQL(createReservationTable);

        // Notification table
        String createNotificationTable = "CREATE TABLE " + TABLE_NOTIFICATION + " (" +
                COL_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTIF_TITLE + " TEXT, " +
                COL_NOTIF_MESSAGE + " TEXT, " +
                COL_NOTIF_TIMESTAMP + " TEXT, " +
                COL_NOTIF_TYPE + " TEXT, " +
                COL_NOTIF_USER_EMAIL + " TEXT, " +
                COL_NOTIF_READ + " INTEGER DEFAULT 0)";
        db.execSQL(createNotificationTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        onCreate(db);
    }

    // ================= MenuItem Methods =================
    public long addMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MENU_NAME, item.getName());
        values.put(COL_MENU_PRICE, item.getPrice());
        values.put(COL_MENU_DESC, item.getDescription());
        values.put(COL_MENU_CATEGORY, item.getCategory());
        values.put(COL_MENU_IMAGE, item.getImageUrl());
        return db.insert(TABLE_MENU, null, values);
    }

    public int updateMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MENU_NAME, item.getName());
        values.put(COL_MENU_PRICE, item.getPrice());
        values.put(COL_MENU_DESC, item.getDescription());
        values.put(COL_MENU_CATEGORY, item.getCategory());
        values.put(COL_MENU_IMAGE, item.getImageUrl());
        return db.update(TABLE_MENU, values, COL_MENU_ID + "=?", new String[]{String.valueOf(item.getId())});
    }

    public int deleteMenuItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MENU, COL_MENU_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MENU, null);
        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MENU_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_MENU_NAME)));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MENU_PRICE)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_MENU_DESC)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_MENU_CATEGORY)));
                item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_MENU_IMAGE)));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    // ================= Reservation Methods =================
    public long addReservation(Reservation res) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RES_CUSTOMER, res.getCustomerName());
        values.put(COL_RES_DATE, res.getDate());
        values.put(COL_RES_TIME, res.getTime());
        values.put(COL_RES_GUESTS, res.getNumberOfGuests());
        values.put(COL_RES_TABLE, res.getTableNumber());
        values.put(COL_RES_REQUESTS, res.getSpecialRequests());
        values.put(COL_RES_STATUS, res.getStatus());
        values.put(COL_RES_USER_EMAIL, res.getUserEmail());
        return db.insert(TABLE_RESERVATION, null, values);
    }

    public int updateReservation(Reservation res) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RES_CUSTOMER, res.getCustomerName());
        values.put(COL_RES_DATE, res.getDate());
        values.put(COL_RES_TIME, res.getTime());
        values.put(COL_RES_GUESTS, res.getNumberOfGuests());
        values.put(COL_RES_TABLE, res.getTableNumber());
        values.put(COL_RES_REQUESTS, res.getSpecialRequests());
        values.put(COL_RES_STATUS, res.getStatus());
        values.put(COL_RES_USER_EMAIL, res.getUserEmail());
        return db.update(TABLE_RESERVATION, values, COL_RES_ID + "=?", new String[]{String.valueOf(res.getId())});
    }

    public int cancelReservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RES_STATUS, "Cancelled");
        return db.update(TABLE_RESERVATION, values, COL_RES_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteReservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RESERVATION, COL_RES_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<Reservation> getUserReservations(String email) {
        List<Reservation> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESERVATION + " WHERE " + COL_RES_USER_EMAIL + "=?", new String[]{email});
        if (cursor.moveToFirst()) {
            do {
                Reservation res = new Reservation();
                res.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RES_ID)));
                res.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_CUSTOMER)));
                res.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_DATE)));
                res.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_TIME)));
                res.setNumberOfGuests(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RES_GUESTS)));
                res.setTableNumber(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_TABLE)));
                res.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_REQUESTS)));
                res.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_STATUS)));
                res.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_USER_EMAIL)));
                list.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESERVATION, null);
        if (cursor.moveToFirst()) {
            do {
                Reservation res = new Reservation();
                res.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RES_ID)));
                res.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_CUSTOMER)));
                res.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_DATE)));
                res.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_TIME)));
                res.setNumberOfGuests(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RES_GUESTS)));
                res.setTableNumber(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_TABLE)));
                res.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_REQUESTS)));
                res.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_STATUS)));
                res.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_RES_USER_EMAIL)));
                list.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ================= Notification Methods =================
    public long addNotification(String title, String message, String timestamp, String type, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIF_TITLE, title);
        values.put(COL_NOTIF_MESSAGE, message);
        values.put(COL_NOTIF_TIMESTAMP, timestamp);
        values.put(COL_NOTIF_TYPE, type);
        values.put(COL_NOTIF_USER_EMAIL, userEmail);
        values.put(COL_NOTIF_READ, 0);
        return db.insert(TABLE_NOTIFICATION, null, values);
    }

    public List<Notification> getUserNotifications(String userEmail) {
        List<Notification> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION + " WHERE " + COL_NOTIF_USER_EMAIL + "=? ORDER BY " + COL_NOTIF_ID + " DESC", new String[]{userEmail});
        if (cursor.moveToFirst()) {
            do {
                Notification n = new Notification();
                n.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTIF_ID)));
                n.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIF_TITLE)));
                n.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIF_MESSAGE)));
                n.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIF_TIMESTAMP)));
                n.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIF_TYPE)));
                n.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTIF_USER_EMAIL)));
                n.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTIF_READ)) == 1);
                list.add(n);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int markNotificationAsRead(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTIF_READ, 1);
        return db.update(TABLE_NOTIFICATION, values, COL_NOTIF_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTIFICATION, COL_NOTIF_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int getUnreadNotificationCount(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTIFICATION + " WHERE " + COL_NOTIF_USER_EMAIL + "=? AND " + COL_NOTIF_READ + "=0", new String[]{userEmail});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}
