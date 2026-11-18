package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.util.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GrabNGoSession";

    private final ExecutorService startupExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SessionManager sessionManager =
                new SessionManager(getApplicationContext());

        Log.d(TAG, "App started.");
        Log.d(TAG, "User ID: " + sessionManager.getUserId());
        Log.d(TAG, "User Role: " + sessionManager.getUserRole());
        Log.d(TAG, "Active Session: "
                + sessionManager.hasActiveSession());

        startupExecutor.execute(() -> {

            // Prepare the database and seed accounts.
            try {
                AppDatabase database =
                        AppDatabase.getDatabase(getApplicationContext());

                database.insertSeedData();

            } catch (Exception exception) {
                Log.e(TAG, "Database initialization failed", exception);
            }

            // Read saved session.
            boolean isLoggedIn =
                    sessionManager.hasActiveSession();

            String role = sessionManager.getUserRole();

            Class<?> destination;

            if (isLoggedIn) {

                if (SessionManager.ROLE_MANAGER.equals(role)) {
                    destination = FoodList.class;
                } else {
                    destination = Home.class;
                }

                Log.d(TAG, "Restoring session for: " + role);

            } else {

                destination = Welcome.class;

                Log.d(TAG, "No active session. Opening Welcome.");
            }

            runOnUiThread(() -> {

                Intent intent = new Intent(
                        MainActivity.this, destination);

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startupExecutor.shutdown();
    }
}
