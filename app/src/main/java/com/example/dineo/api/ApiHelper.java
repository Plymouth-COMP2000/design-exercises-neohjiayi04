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
            // API endpoint
            String urlString = BASE_URL + "/create_user/" + STUDENT_ID;
            URL url = new URL(urlString);

            // Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create JSON data
            JSONObject jsonData = new JSONObject();
            jsonData.put("username", username);
            jsonData.put("password", password);
            jsonData.put("firstname", firstname);
            jsonData.put("lastname", lastname);
            jsonData.put("email", email);
            jsonData.put("contact", contact);
            jsonData.put("usertype", usertype);

            // Send data
            OutputStream os = conn.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();
            os.close();

            // Get response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                return "Error: " + responseCode;
            }

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

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                return "Error: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Login user
    public static String loginUser(String username, String password) {
        try {
            // Get all users
            String usersJson = getAllUsers();

            if (usersJson.startsWith("Error")) {
                return usersJson;
            }

            // Check credentials
            JSONObject jsonResponse = new JSONObject(usersJson);
            if (jsonResponse.has("users")) {
                org.json.JSONArray users = jsonResponse.getJSONArray("users");

                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    String dbUsername = user.getString("username");
                    String dbPassword = user.getString("password");

                    if (dbUsername.equals(username) && dbPassword.equals(password)) {
                        // Login successful
                        return user.toString();
                    }
                }
            }

            return "Error: Invalid username or password";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Update user
    public static String updateUser(int userId, String username, String password, String firstname,
                                    String lastname, String email, String contact, String usertype) {
        try {
            String urlString = BASE_URL + "/update_user/" + STUDENT_ID + "/" + userId;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create JSON data
            JSONObject jsonData = new JSONObject();
            jsonData.put("username", username);
            jsonData.put("password", password);
            jsonData.put("firstname", firstname);
            jsonData.put("lastname", lastname);
            jsonData.put("email", email);
            jsonData.put("contact", contact);
            jsonData.put("usertype", usertype);

            // Send data
            OutputStream os = conn.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();
            os.close();

            // Get response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                return "Error: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Delete user
    public static String deleteUser(int userId) {
        try {
            String urlString = BASE_URL + "/delete_user/" + STUDENT_ID + "/" + userId;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                return "Error: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}