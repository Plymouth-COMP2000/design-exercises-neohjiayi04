package com.example.dineo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import com.example.dineo.R;
import com.example.dineo.api.ApiHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResetPasswordActivity extends BaseActivity {

    private TextInputEditText editCurrent, editNew, editConfirm;
    private MaterialButton btnReset;
    private ExecutorService executor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_user);

        setupBottomNavigation(R.id.nav_profile);

        sharedPreferences = getSharedPreferences("DinoPrefs", MODE_PRIVATE);
        executor = Executors.newSingleThreadExecutor();

        editCurrent = findViewById(R.id.editTextCurrentPassword);
        editNew = findViewById(R.id.editTextNewPassword);
        editConfirm = findViewById(R.id.editTextConfirmPassword);
        btnReset = findViewById(R.id.btnResetPassword);

        findViewById(R.id.imageViewBack).setOnClickListener(v -> finish());

        btnReset.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String current = editCurrent.getText().toString().trim();
        String newPass = editNew.getText().toString().trim();
        String confirm = editConfirm.getText().toString().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String userJsonStr = sharedPreferences.getString("user_json", "");
            JSONObject userJson = new JSONObject(userJsonStr);
            String userId = userJson.getString("_id");

            btnReset.setEnabled(false);

            executor.execute(() -> {
                String response = ApiHelper.resetPasswordById(userId, current, newPass);

                runOnUiThread(() -> {
                    btnReset.setEnabled(true);
                    if (response.startsWith("Error")) {
                        Toast.makeText(this, "Reset failed: "+response, Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            JSONObject updatedJson = new JSONObject(response);
                            updatedJson.put("password", newPass);
                            sharedPreferences.edit().putString("user_json", updatedJson.toString()).apply();
                            Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(this, "Reset but failed to save locally", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error resetting password", Toast.LENGTH_SHORT).show();
        }
    }
}
