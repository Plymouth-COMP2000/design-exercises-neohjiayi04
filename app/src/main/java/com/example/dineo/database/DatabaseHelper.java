package com.example.dineo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.dineo.models.Reservation;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dineo.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users table
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "fullname TEXT," +
                "email TEXT UNIQUE" +
                ");");

        // Reservations table
        db.execSQL("CREATE TABLE IF NOT EXISTS reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userEmail TEXT," +
                "date TEXT," +
                "time TEXT," +
                "numberOfGuests INTEGER," +
                "tableNumber TEXT," +
                "specialRequests TEXT," +
                "status TEXT DEFAULT 'Pending'" +
                ");");

        // Notifications table
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "message TEXT," +
                "timestamp TEXT," +
                "type TEXT," +
                "userEmail TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle DB upgrade if needed
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS reservations");
        db.execSQL("DROP TABLE IF EXISTS notifications");
        onCreate(db);
    }

    // -------------------- USER METHODS --------------------

    public boolean isUserExists(String usernameOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE username = ? OR email = ?",
                new String[]{usernameOrEmail, usernameOrEmail});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUserName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT fullname FROM users WHERE username = ?",
                new String[]{username});
        String name = "";
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    public long addUser(String username, String password, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("fullname", fullName);
        return db.insert("users", null, values);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // -------------------- RESERVATION METHODS --------------------

    public Reservation getReservationByIdAndEmail(int reservationId, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM reservations WHERE id = ? AND userEmail = ?",
                new String[]{String.valueOf(reservationId), email});
        Reservation reservation = null;
        if (cursor.moveToFirst()) {
            reservation = new Reservation();
            reservation.setId(cursor.getInt(cursor.getColumnIndex("id")));
            reservation.setDate(cursor.getString(cursor.getColumnIndex("date")));
            reservation.setTime(cursor.getString(cursor.getColumnIndex("time")));
            reservation.setNumberOfGuests(cursor.getInt(cursor.getColumnIndex("numberOfGuests")));
            reservation.setTableNumber(cursor.getString(cursor.getColumnIndex("tableNumber")));
            reservation.setSpecialRequests(cursor.getString(cursor.getColumnIndex("specialRequests")));
            reservation.setStatus(cursor.getString(cursor.getColumnIndex("status")));
        }
        cursor.close();
        return reservation;
    }

    public int updateReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", reservation.getDate());
        values.put("time", reservation.getTime());
        values.put("numberOfGuests", reservation.getNumberOfGuests());
        values.put("tableNumber", reservation.getTableNumber());
        values.put("specialRequests", reservation.getSpecialRequests());
        return db.update("reservations", values, "id = ?", new String[]{String.valueOf(reservation.getId())});
    }

    // -------------------- NOTIFICATION METHODS --------------------

    public void addNotification(String title, String message, String timestamp, String type, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("message", message);
        values.put("timestamp", timestamp);
        values.put("type", type);
        values.put("userEmail", email);
        db.insert("notifications", null, values);
    }
}
