package com.example.dineo.api;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {

    // Base URL for API
    private static final String BASE_URL = "http://10.240.72.69/comp2000/coursework";

    // Student ID
    private static final String STUDENT_ID = "BSSE2506008";

    // Create user
    public static String createUser(String username, String password, String firstname,
                                    String lastname, String email, String contact, String usertype) {
        try {
            String urlString = BASE_URL + "/create_user/" + STUDENT_ID;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonData = new JSONObject();
            jsonData.put("username", username);
            jsonData.put("password", password);
            jsonData.put("firstname", firstname);
            jsonData.put("lastname", lastname);
            jsonData.put("email", email);
            jsonData.put("contact", contact);
            jsonData.put("usertype", usertype);

            OutputStream os = conn.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();
            os.close();

            return readResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Get all users
    public static String getAllUsers() {
        try {
            String urlString = BASE_URL + "/read_all_users/" + STUDENT_ID;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            return readResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Login user
    public static String loginUser(String username, String password) {
        try {
            String usersJson = getAllUsers();
            if (usersJson.startsWith("Error")) return usersJson;

            JSONObject jsonResponse = new JSONObject(usersJson);
            if (jsonResponse.has("users")) {
                org.json.JSONArray users = jsonResponse.getJSONArray("users");
                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    String dbUsername = user.getString("username");
                    String dbPassword = user.getString("password");
                    if (dbUsername.equals(username) && dbPassword.equals(password)) {
                        return user.toString(); // Return full user object
                    }
                }
            }
            return "Error: Invalid username or password";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Update user by _id
    public static String updateUserById(String userId, String firstname, String lastname,
                                        String email, String contact, String usertype) {
        try {
            String urlString = BASE_URL + "/update_user/" + STUDENT_ID + "/" + userId;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonData = new JSONObject();
            jsonData.put("firstname", firstname);
            jsonData.put("lastname", lastname);
            jsonData.put("email", email);
            jsonData.put("contact", contact);
            jsonData.put("usertype", usertype);

            OutputStream os = conn.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();
            os.close();

            return readResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Reset password by _id
    public static String resetPasswordById(String userId, String currentPassword, String newPassword) {
        try {
            String urlString = BASE_URL + "/reset_password/" + STUDENT_ID + "/" + userId;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject jsonData = new JSONObject();
            jsonData.put("currentPassword", currentPassword);
            jsonData.put("newPassword", newPassword);

            OutputStream os = conn.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();
            os.close();

            return readResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Delete user by _id
    public static String deleteUser(String userId) {
        try {
            String urlString = BASE_URL + "/delete_user/" + STUDENT_ID + "/" + userId;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            return readResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Helper method to read API response
    private static String readResponse(HttpURLConnection conn) {
        try {
            int responseCode = conn.getResponseCode();
            BufferedReader in;
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
                return "Error: " + response.toString();
            }

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
