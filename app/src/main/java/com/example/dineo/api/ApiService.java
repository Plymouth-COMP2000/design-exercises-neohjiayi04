package com.example.dineo.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiService {
    private static final String STUDENT_ID = "BSSE2506008";
    private static final String BASE_URL = "http://10.240.72.69/comp2000/coursework";
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    // Login user by EMAIL instead of username
    public static void loginUserByEmail(Context context, String email, String password, ApiCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/read_all_users/" + STUDENT_ID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray users = jsonResponse.getJSONArray("users");

                    boolean found = false;
                    JSONObject matchedUser = null;

                    // Search by EMAIL and password
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        String dbEmail = user.getString("email");
                        String dbPassword = user.getString("password");

                        if (dbEmail.equalsIgnoreCase(email) && dbPassword.equals(password)) {
                            found = true;
                            matchedUser = user;
                            break;
                        }
                    }

                    if (found) {
                        JSONObject loginResponse = new JSONObject();
                        loginResponse.put("success", true);
                        loginResponse.put("user", matchedUser);

                        mainHandler.post(() -> callback.onSuccess(loginResponse));
                    } else {
                        mainHandler.post(() -> callback.onError("Invalid email or password"));
                    }
                } else {
                    mainHandler.post(() -> callback.onError("Failed to connect to server"));
                }

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        });
    }

    // Get user profile
    public static void getUserProfile(Context context, String username, ApiCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/read_all_users/" + STUDENT_ID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray users = jsonResponse.getJSONArray("users");

                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        if (user.getString("username").equals(username)) {
                            JSONObject profileResponse = new JSONObject();
                            profileResponse.put("success", true);
                            profileResponse.put("user", user);

                            mainHandler.post(() -> callback.onSuccess(profileResponse));
                            return;
                        }
                    }

                    mainHandler.post(() -> callback.onError("User not found"));
                } else {
                    mainHandler.post(() -> callback.onError("Failed to fetch profile"));
                }

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        });
    }

    // Update user profile
    public static void updateUserProfile(Context context, String username, String firstname,
                                         String lastname, String email, String contact,
                                         String password, String usertype, ApiCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/update_user/" + STUDENT_ID + "/" + username);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", username);
                jsonBody.put("password", password);
                jsonBody.put("firstname", firstname);
                jsonBody.put("lastname", lastname);
                jsonBody.put("email", email);
                jsonBody.put("contact", contact);
                jsonBody.put("usertype", usertype);

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader reader;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                mainHandler.post(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("detail", "Failed to update profile"));
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        });
    }

    // Create new user
    public static void createUser(Context context, String username, String password,
                                  String firstname, String lastname, String email,
                                  String contact, String usertype, ApiCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/create_user/" + STUDENT_ID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", username);
                jsonBody.put("password", password);
                jsonBody.put("firstname", firstname);
                jsonBody.put("lastname", lastname);
                jsonBody.put("email", email);
                jsonBody.put("contact", contact);
                jsonBody.put("usertype", usertype);

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader reader;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                mainHandler.post(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("detail", "Failed to create user"));
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        });
    }
}