package com.example.dineo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "DinoSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIRST_NAME = "firstname";
    private static final String KEY_LAST_NAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USER_TYPE = "usertype";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    // Notification preferences
    private static final String KEY_NOTIF_CONFIRMATION = "notif_confirmation";
    private static final String KEY_NOTIF_MODIFICATION = "notif_modification";
    private static final String KEY_NOTIF_CANCELLATION = "notif_cancellation";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Save login session
    public void createLoginSession(String username, String password, String firstname,
                                   String lastname, String email, String contact, String usertype) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_FIRST_NAME, firstname);
        editor.putString(KEY_LAST_NAME, lastname);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_CONTACT, contact);
        editor.putString(KEY_USER_TYPE, usertype);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Update user profile data
    public void updateUserProfile(String firstname, String lastname, String email, String contact) {
        editor.putString(KEY_FIRST_NAME, firstname);
        editor.putString(KEY_LAST_NAME, lastname);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_CONTACT, contact);
        editor.apply();
    }

    // Update password
    public void updatePassword(String password) {
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    // Get user details
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getPassword() {
        return prefs.getString(KEY_PASSWORD, null);
    }

    public String getFirstName() {
        return prefs.getString(KEY_FIRST_NAME, "User");
    }

    public String getLastName() {
        return prefs.getString(KEY_LAST_NAME, "");
    }

    public String getFullName() {
        String firstName = getFirstName();
        String lastName = getLastName();
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "email@example.com");
    }

    public String getContact() {
        return prefs.getString(KEY_CONTACT, "");
    }

    public String getUserType() {
        return prefs.getString(KEY_USER_TYPE, "guest");
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Notification preferences
    public void setNotificationPreference(String type, boolean enabled) {
        switch (type) {
            case "confirmation":
                editor.putBoolean(KEY_NOTIF_CONFIRMATION, enabled);
                break;
            case "modification":
                editor.putBoolean(KEY_NOTIF_MODIFICATION, enabled);
                break;
            case "cancellation":
                editor.putBoolean(KEY_NOTIF_CANCELLATION, enabled);
                break;
        }
        editor.apply();
    }

    public boolean getNotificationPreference(String type) {
        switch (type) {
            case "confirmation":
                return prefs.getBoolean(KEY_NOTIF_CONFIRMATION, true);
            case "modification":
                return prefs.getBoolean(KEY_NOTIF_MODIFICATION, true);
            case "cancellation":
                return prefs.getBoolean(KEY_NOTIF_CANCELLATION, true);
            default:
                return true;
        }
    }

    // Logout - clear all session data
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
