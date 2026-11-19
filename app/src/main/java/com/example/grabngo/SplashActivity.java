package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grabngo.database.AppDatabase;

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

                    // Start timer
                    Timer timer = new Timer();

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    openMainActivity();
                                }
                            });
                        }
                    };

                    timer.schedule(task, 3000);

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

    private void openMainActivity() {

        Intent intent = new Intent(
                SplashActivity.this,
                MainActivity.class
        );

        startActivity(intent);
        finish();
    }



//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        startupExecutor.shutdown();
//    }
}