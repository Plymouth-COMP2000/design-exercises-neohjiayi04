package com.example.dineo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

<<<<<<< HEAD
import androidx.annotation.Nullable;

import com.example.dineo.models.Reservation;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dineo.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
=======
import com.example.dineo.models.MenuItem;
import com.example.dineo.models.Reservation;
import com.example.dineo.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dineo_restaurant.db";
    private static final int DATABASE_VERSION = 6; // INCREMENT THIS!

    // ==================== TABLE: USERS ====================
    private static final String TABLE_USERS = "users";
    private static final String USER_ID = "id";
    private static final String USER_USERNAME = "username";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";
    private static final String USER_ROLE = "role";

    // ==================== TABLE: MENU ITEMS ====================
    private static final String TABLE_MENU = "menu_items";
    private static final String MENU_ID = "id";
    private static final String MENU_NAME = "name";
    private static final String MENU_DESCRIPTION = "description";
    private static final String MENU_PRICE = "price";
    private static final String MENU_CATEGORY = "category";
    private static final String MENU_IMAGE_PATH = "image_path";

    // ==================== TABLE: RESERVATIONS ====================
    private static final String TABLE_RESERVATIONS = "reservations";
    private static final String RES_ID = "id";
    private static final String RES_CUSTOMER_NAME = "customer_name";
    private static final String RES_DATE = "date";
    private static final String RES_TIME = "time";
    private static final String RES_PAX = "number_of_pax";
    private static final String RES_STATUS = "status";
    private static final String RES_PHONE = "phone_number";
    private static final String RES_EMAIL = "email";
    private static final String RES_SPECIAL_REQUESTS = "special_requests";
    private static final String RES_TABLE_NUMBER = "table_number";

    public DatabaseHelper(Context context) {
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
<<<<<<< HEAD
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
=======
        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_USERNAME + " TEXT NOT NULL UNIQUE, " +
                USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                USER_PASSWORD + " TEXT NOT NULL, " +
                USER_ROLE + " TEXT NOT NULL, " +
                "phone TEXT)";
        db.execSQL(createUsersTable);

        // Create Menu Items Table
        String createMenuTable = "CREATE TABLE " + TABLE_MENU + " (" +
                MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MENU_NAME + " TEXT NOT NULL, " +
                MENU_DESCRIPTION + " TEXT, " +
                MENU_PRICE + " REAL NOT NULL, " +
                MENU_CATEGORY + " TEXT NOT NULL, " +
                MENU_IMAGE_PATH + " TEXT)";
        db.execSQL(createMenuTable);

        // Create Reservations Table
        String createReservationsTable = "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RES_CUSTOMER_NAME + " TEXT NOT NULL, " +
                RES_DATE + " TEXT NOT NULL, " +
                RES_TIME + " TEXT NOT NULL, " +
                RES_PAX + " INTEGER NOT NULL, " +
                RES_STATUS + " TEXT NOT NULL, " +
                RES_PHONE + " TEXT, " +
                RES_EMAIL + " TEXT, " +
                RES_SPECIAL_REQUESTS + " TEXT, " +
                RES_TABLE_NUMBER + " TEXT)";
        db.execSQL(createReservationsTable);

        // Insert default data
        insertDefaultAdmin(db);
        insertDummyMenuItems(db);
        insertSampleReservations(db);
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
<<<<<<< HEAD
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
=======
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        onCreate(db);
    }

    // ==================== USER AUTHENTICATION ====================

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(USER_USERNAME, "admin");
        values.put(USER_EMAIL, "admin@dineo.com");
        values.put(USER_PASSWORD, hashPassword("admin123"));
        values.put(USER_ROLE, "admin");
        db.insert(TABLE_USERS, null, values);
    }

    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_USERNAME, user.getUsername());
        values.put(USER_EMAIL, user.getEmail());
        values.put(USER_PASSWORD, hashPassword(user.getPassword()));
        values.put(USER_ROLE, user.getRole());

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + USER_EMAIL + " = ? AND " + USER_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, hashedPassword});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(USER_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(USER_ROLE))
            );
        }
        cursor.close();
        return user;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

<<<<<<< HEAD
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
=======
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ==================== DUMMY MENU DATA ====================

    private void insertDummyMenuItems(SQLiteDatabase db) {
        // Rice Dishes
        insertMenuItem(db, "Nasi Lemak", "Fragrant coconut rice with sambal, fried anchovies, peanuts, egg", 12.90, "Rice", "");
        insertMenuItem(db, "Nasi Goreng Kampung", "Traditional village-style fried rice with anchovies and vegetables", 11.50, "Rice", "");
        insertMenuItem(db, "Chicken Rice", "Tender steamed chicken served with fragrant rice and special sauce", 13.90, "Rice", "");
        insertMenuItem(db, "Nasi Ayam Rendang", "Spicy coconut curry chicken with fragrant rice", 14.90, "Rice", "");

        // Noodles
        insertMenuItem(db, "Char Kuey Teow", "Stir-fried flat rice noodles with prawns, egg, and bean sprouts", 12.50, "Noodles", "");
        insertMenuItem(db, "Mee Goreng", "Spicy fried yellow noodles with seafood and vegetables", 11.90, "Noodles", "");
        insertMenuItem(db, "Laksa Johor", "Rich coconut curry noodle soup with fish and vegetables", 13.50, "Noodles", "");
        insertMenuItem(db, "Penang Asam Laksa", "Tangy tamarind fish broth noodle soup", 12.90, "Noodles", "");

        // Meat
        insertMenuItem(db, "Beef Rendang", "Slow-cooked beef in rich coconut curry", 18.90, "Meat", "");
        insertMenuItem(db, "Ayam Percik", "Grilled chicken with spicy coconut gravy", 15.90, "Meat", "");
        insertMenuItem(db, "Satay Platter", "12 skewers of grilled meat with peanut sauce (mixed chicken & beef)", 16.50, "Meat", "");
        insertMenuItem(db, "Lamb Curry", "Tender lamb in aromatic curry sauce", 19.90, "Meat", "");

        // Vegetables
        insertMenuItem(db, "Kangkung Belacan", "Water spinach stir-fried with shrimp paste", 8.90, "Vegetables", "");
        insertMenuItem(db, "Mixed Vegetables", "Seasonal vegetables in garlic sauce", 9.50, "Vegetables", "");
        insertMenuItem(db, "Sayur Lodeh", "Mixed vegetables in coconut curry", 10.90, "Vegetables", "");
        insertMenuItem(db, "Stir-Fried Kailan", "Chinese broccoli with oyster sauce", 9.90, "Vegetables", "");

        // Appetizers
        insertMenuItem(db, "Spring Rolls", "Crispy vegetable spring rolls (5 pcs)", 8.90, "Appetizers", "");
        insertMenuItem(db, "Curry Puff", "Flaky pastry filled with spiced potato (3 pcs)", 6.90, "Appetizers", "");
        insertMenuItem(db, "Samosa", "Crispy triangular pastry with savory filling (4 pcs)", 7.50, "Appetizers", "");
        insertMenuItem(db, "Prawn Fritters", "Deep-fried prawn cakes (6 pcs)", 12.90, "Appetizers", "");

        // Desserts
        insertMenuItem(db, "Cendol", "Shaved ice with coconut milk, palm sugar, and jelly noodles", 6.50, "Desserts", "");
        insertMenuItem(db, "Ais Kacang", "Shaved ice with red beans, jelly, corn, and colorful syrup", 7.50, "Desserts", "");
        insertMenuItem(db, "Kuih Lapis", "Traditional layered steamed cake (3 pcs)", 5.90, "Desserts", "");
        insertMenuItem(db, "Sago Gula Melaka", "Pearl sago with palm sugar and coconut milk", 6.90, "Desserts", "");

        // Drinks
        insertMenuItem(db, "Teh Tarik", "Pulled milk tea", 3.50, "Drinks", "");
        insertMenuItem(db, "Milo Ais", "Iced chocolate malt drink", 4.50, "Drinks", "");
        insertMenuItem(db, "Fresh Coconut", "Young coconut water", 5.90, "Drinks", "");
        insertMenuItem(db, "Lime Juice", "Freshly squeezed lime juice", 4.90, "Drinks", "");
        insertMenuItem(db, "Bandung", "Rose syrup milk drink", 3.90, "Drinks", "");
    }

    private void insertMenuItem(SQLiteDatabase db, String name, String description, double price, String category, String imagePath) {
        ContentValues values = new ContentValues();
        values.put(MENU_NAME, name);
        values.put(MENU_DESCRIPTION, description);
        values.put(MENU_PRICE, price);
        values.put(MENU_CATEGORY, category);
        values.put(MENU_IMAGE_PATH, imagePath);
        db.insert(TABLE_MENU, null, values);
    }

    // ==================== MENU ITEMS ====================

    public long addMenuItem(MenuItem menuItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MENU_NAME, menuItem.getName());
        values.put(MENU_DESCRIPTION, menuItem.getDescription());
        values.put(MENU_PRICE, menuItem.getPrice());
        values.put(MENU_CATEGORY, menuItem.getCategory());
        values.put(MENU_IMAGE_PATH, menuItem.getImagePath());

        return db.insert(TABLE_MENU, null, values);
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MENU + " ORDER BY " + MENU_CATEGORY + ", " + MENU_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(MENU_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_IMAGE_PATH))
                );
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MENU_ID)));
                menuList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return menuList;
    }

    public MenuItem getMenuItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MENU + " WHERE " + MENU_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        MenuItem item = null;
        if (cursor.moveToFirst()) {
            item = new MenuItem(
                    cursor.getString(cursor.getColumnIndexOrThrow(MENU_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MENU_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(MENU_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MENU_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MENU_IMAGE_PATH))
            );
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MENU_ID)));
        }
        cursor.close();
        return item;
    }

    public List<MenuItem> searchMenuItems(String searchQuery) {
        List<MenuItem> menuList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MENU +
                " WHERE " + MENU_NAME + " LIKE ? OR " + MENU_DESCRIPTION + " LIKE ?" +
                " ORDER BY " + MENU_CATEGORY + ", " + MENU_NAME;
        String searchPattern = "%" + searchQuery + "%";
        Cursor cursor = db.rawQuery(query, new String[]{searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(MENU_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_IMAGE_PATH))
                );
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MENU_ID)));
                menuList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return menuList;
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MENU + " WHERE " + MENU_CATEGORY + " = ? ORDER BY " + MENU_NAME;
        Cursor cursor = db.rawQuery(query, new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(MENU_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MENU_IMAGE_PATH))
                );
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MENU_ID)));
                menuList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return menuList;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT " + MENU_CATEGORY + " FROM " + TABLE_MENU + " ORDER BY " + MENU_CATEGORY;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public int updateMenuItem(MenuItem menuItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MENU_NAME, menuItem.getName());
        values.put(MENU_DESCRIPTION, menuItem.getDescription());
        values.put(MENU_PRICE, menuItem.getPrice());
        values.put(MENU_CATEGORY, menuItem.getCategory());
        values.put(MENU_IMAGE_PATH, menuItem.getImagePath());

        return db.update(TABLE_MENU, values, MENU_ID + " = ?",
                new String[]{String.valueOf(menuItem.getId())});
    }

    public int deleteMenuItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MENU, MENU_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ==================== RESERVATIONS ====================

    /**
     * Add a new reservation to the database
     * @param reservation The reservation to add
     * @return The ID of the newly inserted reservation, or -1 if failed
     */
    public long addReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RES_CUSTOMER_NAME, reservation.getCustomerName());
        values.put(RES_DATE, reservation.getDate());
        values.put(RES_TIME, reservation.getTime());
        values.put(RES_PAX, reservation.getNumberOfPax());
        values.put(RES_STATUS, reservation.getStatus());
        values.put(RES_PHONE, reservation.getPhoneNumber());
        values.put(RES_EMAIL, reservation.getEmail());
        values.put(RES_SPECIAL_REQUESTS, reservation.getSpecialRequests());
        values.put(RES_TABLE_NUMBER, reservation.getTableNumber());

        return db.insert(TABLE_RESERVATIONS, null, values);
    }

    /**
     * Get reservations for a specific date
     * @param date Date in format "yyyy-MM-dd"
     * @return List of reservations for that date, sorted by time
     */
    public List<Reservation> getReservationsByDate(String date) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RESERVATIONS,
                null,
                RES_DATE + " = ?",
                new String[]{date},
                null, null,
                RES_TIME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation(
                        cursor.getInt(cursor.getColumnIndexOrThrow(RES_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(RES_PAX)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_SPECIAL_REQUESTS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_TABLE_NUMBER))
                );
                reservations.add(reservation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reservations;
    }

    /**
     * Get a single reservation by its ID
     * @param id The reservation ID
     * @return Reservation object or null if not found
     */
    public Reservation getReservationById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Reservation reservation = null;

        Cursor cursor = db.query(
                TABLE_RESERVATIONS,
                null,
                RES_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            reservation = new Reservation(
                    cursor.getInt(cursor.getColumnIndexOrThrow(RES_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RES_CUSTOMER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RES_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RES_TIME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(RES_PAX)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RES_STATUS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RES_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RES_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RES_SPECIAL_REQUESTS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(RES_TABLE_NUMBER))
            );
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
        }
        cursor.close();
        return reservation;
    }

<<<<<<< HEAD
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
=======
    /**
     * Update an existing reservation with all fields
     * @param reservation The reservation object with updated values
     * @return Number of rows affected (1 if successful, 0 if failed)
     */
    public int updateReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(RES_CUSTOMER_NAME, reservation.getCustomerName());
        values.put(RES_DATE, reservation.getDate());
        values.put(RES_TIME, reservation.getTime());
        values.put(RES_PAX, reservation.getNumberOfPax());
        values.put(RES_STATUS, reservation.getStatus());
        values.put(RES_PHONE, reservation.getPhoneNumber());
        values.put(RES_EMAIL, reservation.getEmail());
        values.put(RES_SPECIAL_REQUESTS, reservation.getSpecialRequests());
        values.put(RES_TABLE_NUMBER, reservation.getTableNumber());

        return db.update(
                TABLE_RESERVATIONS,
                values,
                RES_ID + " = ?",
                new String[]{String.valueOf(reservation.getId())}
        );
    }

    /**
     * Update only the status of a reservation (faster than updating all fields)
     * @param id The reservation ID
     * @param status The new status (e.g., "Confirmed", "Cancelled", "Seated")
     * @return Number of rows affected (1 if successful, 0 if failed)
     */
    public int updateReservationStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RES_STATUS, status);

        return db.update(
                TABLE_RESERVATIONS,
                values,
                RES_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Get all reservations (for admin or guest to see their own)
     * @return List of all reservations, sorted by date and time (newest first)
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RESERVATIONS,
                null, null, null, null, null,
                RES_DATE + " DESC, " + RES_TIME + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation(
                        cursor.getInt(cursor.getColumnIndexOrThrow(RES_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(RES_PAX)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_SPECIAL_REQUESTS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(RES_TABLE_NUMBER))
                );
                reservations.add(reservation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reservations;
    }

    /**
     * Add sample reservations for testing
     * This is called automatically in onCreate() when database is first created
     * @param db The writable database instance
     */
    private void insertSampleReservations(SQLiteDatabase db) {
        // Get today's date and next few days
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        String today = sdf.format(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrow = sdf.format(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, 1);
        String dayAfter = sdf.format(cal.getTime());

        // Sample reservations data
        // Format: {name, date, time, pax, status, phone, email, requests, table}
        String[][] sampleData = {
                // TODAY'S RESERVATIONS (5)
                {"John Smith", today, "18:30", "4", "Upcoming", "+60123456789", "john@email.com", "Window seat preferred", "Table 5"},
                {"Sarah Johnson", today, "19:00", "2", "Confirmed", "+60198765432", "sarah@email.com", null, "Table 3"},
                {"Michael Chen", today, "19:30", "6", "Seated", "+60187654321", "michael@email.com", "Birthday celebration", "Table 8"},
                {"Emma Wilson", today, "20:00", "3", "Upcoming", "+60176543210", "emma@email.com", null, "Table 2"},
                {"David Lee", today, "20:30", "5", "Confirmed", "+60165432109", "david@email.com", "Vegetarian options needed", "Table 7"},

                // TOMORROW'S RESERVATIONS (5)
                {"Lisa Anderson", tomorrow, "18:00", "4", "Upcoming", "+60154321098", "lisa@email.com", null, "Table 4"},
                {"Robert Taylor", tomorrow, "18:30", "2", "Confirmed", "+60143210987", "robert@email.com", "Anniversary dinner", "Table 1"},
                {"Jennifer Brown", tomorrow, "19:00", "8", "Upcoming", "+60132109876", "jennifer@email.com", "High chair needed", "Table 10"},
                {"William Garcia", tomorrow, "19:30", "3", "Confirmed", "+60121098765", "william@email.com", null, "Table 6"},
                {"Maria Rodriguez", tomorrow, "20:00", "6", "Upcoming", "+60110987654", "maria@email.com", "Gluten-free options", "Table 9"},

                // DAY AFTER TOMORROW (5)
                {"James Martinez", dayAfter, "18:30", "2", "Upcoming", "+60109876543", "james@email.com", null, "Table 2"},
                {"Patricia Hernandez", dayAfter, "19:00", "4", "Upcoming", "+60198765430", "patricia@email.com", "Business dinner", "Table 5"},
                {"Christopher Lopez", dayAfter, "19:30", "5", "Upcoming", "+60187654329", "christopher@email.com", null, "Table 7"},
                {"Linda Gonzalez", dayAfter, "20:00", "3", "Upcoming", "+60176543218", "linda@email.com", "Quiet area preferred", "Table 3"},
                {"Daniel Wilson", dayAfter, "20:30", "7", "Upcoming", "+60165432107", "daniel@email.com", "Group celebration", "Table 11"}
        };

        // Insert each sample reservation
        for (String[] data : sampleData) {
            ContentValues values = new ContentValues();
            values.put(RES_CUSTOMER_NAME, data[0]);
            values.put(RES_DATE, data[1]);
            values.put(RES_TIME, data[2]);
            values.put(RES_PAX, Integer.parseInt(data[3]));
            values.put(RES_STATUS, data[4]);
            values.put(RES_PHONE, data[5]);
            values.put(RES_EMAIL, data[6]);
            values.put(RES_SPECIAL_REQUESTS, data[7]);
            values.put(RES_TABLE_NUMBER, data[8]);

            db.insert(TABLE_RESERVATIONS, null, values);
        }
    }

    // ==================== USER PROFILE MANAGEMENT ====================

    /**
     * Get user's phone number by user ID
     * @param userId The user ID
     * @return Phone number or null if not set
     */
    public String getUserPhone(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String phone = null;

        String query = "SELECT phone FROM " + TABLE_USERS + " WHERE " + USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            int phoneIndex = cursor.getColumnIndex("phone");
            if (phoneIndex != -1) {
                phone = cursor.getString(phoneIndex);
            }
        }
        cursor.close();
        return phone;
    }

    /**
     * Update user profile (username, email, phone)
     * @param userId The user ID
     * @param username New username
     * @param email New email
     * @param phone New phone number
     * @return Number of rows affected (1 if successful, 0 if failed)
     */
    public int updateUser(int userId, String username, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_USERNAME, username);
        values.put(USER_EMAIL, email);
        values.put("phone", phone);

        return db.update(
                TABLE_USERS,
                values,
                USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
    }

    /**
     * Update user password
     * @param userId The user ID
     * @param newPassword The new password (will be hashed automatically)
     * @return Number of rows affected (1 if successful, 0 if failed)
     */
    public int updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER_PASSWORD, hashPassword(newPassword));

        return db.update(
                TABLE_USERS,
                values,
                USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
    }

// ========================================================================
// IMPORTANT: UPDATE YOUR TABLE CREATION TO INCLUDE PHONE COLUMN
// ========================================================================
// Modify your onCreate method where you create the users table:
//
// String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
//         USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//         USER_USERNAME + " TEXT NOT NULL UNIQUE, " +
//         USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
//         USER_PASSWORD + " TEXT NOT NULL, " +
//         USER_ROLE + " TEXT NOT NULL, " +
//         "phone TEXT)";  // ADD THIS LINE
// db.execSQL(createUsersTable);
}
>>>>>>> e9babd3d5e6463477cb758221fac66bfffdba5f8
