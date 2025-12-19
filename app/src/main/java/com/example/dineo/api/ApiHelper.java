package com.example.dineo.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * API Helper - Connects to COMP2000 Coursework API
 * Student ID: BSSE2506008
 *
 * API Base: http://10.240.72.69/comp2000/coursework/
 */
public class ApiHelper {

    private static final String TAG = "ApiHelper";
    private static final String BASE_URL = "http://10.240.72.69/comp2000/coursework/";
    private static final String STUDENT_ID = "BSSE2506008";

    /**
     * Create student database (call this once at app first run)
     */
    public static String createStudentDatabase() {
        try {
            URL url = new URL(BASE_URL + "create_student/" + STUDENT_ID);
            Log.d(TAG, "Creating database: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "Database created: " + response.toString());
                return response.toString();
            } else {
                return "Error: HTTP " + responseCode;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating database: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Create new user (Register)
     */
    public static String createUser(String username, String password, String firstname,
                                    String lastname, String email, String contact, String usertype) {
        try {
            URL url = new URL(BASE_URL + "create_user/" + STUDENT_ID);
            Log.d(TAG, "Creating user: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            // Create JSON body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("firstname", firstname);
            jsonBody.put("lastname", lastname);
            jsonBody.put("email", email);
            jsonBody.put("contact", contact);
            jsonBody.put("usertype", usertype);

            Log.d(TAG, "Request body: " + jsonBody.toString());

            // Send request
            OutputStream os = connection.getOutputStream();
            os.write(jsonBody.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "User created: " + response.toString());
                return response.toString();
            } else {
                return "Error: HTTP " + responseCode;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating user: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Login user via username and password directly
     */
    public static String loginUser(String username, String password) {
        try {
            URL url = new URL(BASE_URL + "login_user/" + STUDENT_ID); // Make sure API supports this endpoint
            Log.d(TAG, "Logging in user: " + username);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            // JSON body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);

            OutputStream os = connection.getOutputStream();
            os.write(jsonBody.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "Login response: " + response.toString());
                return response.toString(); // Should return user JSON
            } else {
                return "Error: HTTP " + responseCode;
            }

        } catch (Exception e) {
            Log.e(TAG, "Login error: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Get user by email (for profile/settings)
     */
    public static String getUserByEmail(String email) {
        try {
            URL url = new URL(BASE_URL + "get_user_by_email/" + STUDENT_ID + "?email=" + email);
            Log.d(TAG, "Getting user by email: " + email);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "User retrieved: " + response.toString());
                return response.toString();
            } else {
                return "Error: HTTP " + responseCode;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting user: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Update user by ID
     */
    public static String updateUser(String userId, String username, String password,
                                    String firstname, String lastname, String email,
                                    String contact, String usertype) {
        try {
            URL url = new URL(BASE_URL + "update_user/" + STUDENT_ID + "/" + userId);
            Log.d(TAG, "Updating user: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("firstname", firstname);
            jsonBody.put("lastname", lastname);
            jsonBody.put("email", email);
            jsonBody.put("contact", contact);
            jsonBody.put("usertype", usertype);

            OutputStream os = connection.getOutputStream();
            os.write(jsonBody.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "User updated: " + response.toString());
                return response.toString();
            } else {
                return "Error: HTTP " + responseCode;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating user: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Delete user by ID
     */
    public static String deleteUser(String userId) {
        try {
            URL url = new URL(BASE_URL + "delete_user/" + STUDENT_ID + "/" + userId);
            Log.d(TAG, "Deleting user: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.d(TAG, "User deleted: " + response.toString());
                return response.toString();
            } else {
                return "Error: HTTP " + responseCode;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
