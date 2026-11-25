//implemented by Ami
package com.example.grabngo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grabngo.database.AppDatabase;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                initializeDatabase();
            }
        });
    }
    private void initializeDatabase() {

        try {

            // Get database instance
            AppDatabase database =
                    AppDatabase.getDatabase(getApplicationContext());

            // Insert seed data
            database.insertSeedData(getApplicationContext());

            // Check session and navigate
            checkSessionAndNavigate();

        } catch (Exception exception) {

            Toast.makeText(
                    SplashActivity.this,
                    "Failed to initialize the database.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }
    }

    private void checkSessionAndNavigate() {

        // Get saved session
        SharedPreferences preferences =
                getSharedPreferences("GrabNGoSession", MODE_PRIVATE);

        int userId = preferences.getInt("user_id", -1);
        String role = preferences.getString("user_role", "");

        Intent intent;

        if (userId > 0 &&
                ("MANAGER".equals(role) || "CUSTOMER".equals(role))) {

            if ("MANAGER".equals(role)) {
                intent = new Intent(SplashActivity.this, FoodList.class);
            } else {
                intent = new Intent(SplashActivity.this, Home.class);
            }

        } else {
            intent = new Intent(SplashActivity.this, Welcome.class);
        }

        // Navigate to the destination
        startActivity(intent);
        finish();
    }
}
