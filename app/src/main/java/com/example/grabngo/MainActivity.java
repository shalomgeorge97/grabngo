package com.example.grabngo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grabngo.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        AppDatabase db = AppDatabase.getDatabase(this);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            // Insert seed data
            db.insertSeedData();

            // Continue after insertion is completed
            runOnUiThread(() -> {
                setContentView(R.layout.activity_main);
                ViewCompat.setOnApplyWindowInsetsListener(
                        findViewById(R.id.main), (v, insets) -> {
                            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                            return insets;
                        });
            });
        });
    }
}