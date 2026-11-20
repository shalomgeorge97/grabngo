
package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.util.SessionManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {

    private final ExecutorService startupExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeDatabase();
    }

    private void initializeDatabase() {

        startupExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // Get database instance
                    AppDatabase database =
                            AppDatabase.getDatabase(getApplicationContext());

                    // Insert seed data
                    database.insertSeedData();

                    // Wait for 1.5 seconds
                    TimerTask task = new TimerTask() {

                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkSessionAndNavigate();
                                }
                            });

                        }
                    };

                    Timer timer = new Timer();

                    timer.schedule(task, 1500);

                } catch (Exception exception) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(
                                    SplashActivity.this,
                                    "Failed to initialize the database.",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        }
                    });
                }
            }
        });
    }

    private void checkSessionAndNavigate() {

        // Get saved session
        SessionManager sessionManager =
                new SessionManager(getApplicationContext());

        // Read saved session
        boolean isLoggedIn = sessionManager.hasActiveSession();
        String role = sessionManager.getUserRole();

        Intent intent;

        if (isLoggedIn) {

            if (SessionManager.ROLE_MANAGER.equals(role)) {

                intent = new Intent(
                        SplashActivity.this,
                        FoodList.class
                );

            } else {

                intent = new Intent(
                        SplashActivity.this,
                        Home.class
                );
            }

        } else {

            intent = new Intent(
                    SplashActivity.this,
                    Welcome.class
            );
        }

        //not sure really need this
        // Navigate to the destination
//        intent.addFlags(
//                Intent.FLAG_ACTIVITY_NEW_TASK |
//                        Intent.FLAG_ACTIVITY_CLEAR_TASK
//        );

        startActivity(intent);
        finish();
    }

    //not sure really need this
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        startupExecutor.shutdown();
//    }
}
