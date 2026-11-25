//implemented Ami
package com.example.grabngo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderActivity extends AppCompatActivity
        implements ReminderAdapter.ItemClickListener {

    private ReminderAdapter adapter;
    private List<Reminder> reminderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminders);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);
                    return insets;
                });

        RecyclerView recyclerView = findViewById(R.id.rvReminders);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        SharedPreferences preferences =
                getSharedPreferences("GrabNGoSession", MODE_PRIVATE);
        int customerId = preferences.getInt("user_id", -1);

        if (customerId > 0) {
            AppDatabase database =
                    AppDatabase.getDatabase(getApplicationContext());
            reminderList = database.reminderDao()
                    .getLatestRemindersByCustomerId(customerId);
            database.reminderDao()
                    .markCustomerRemindersAsRead(customerId);
        }

        adapter = new ReminderAdapter(
                this,
                reminderList,
                this::onItemClick);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Reminder reminder = reminderList.get(position);

        Intent intent = new Intent(
                ReminderActivity.this,
                CustomerOrderDetails.class);
        intent.putExtra("order_id", reminder.getOrderId());
        startActivity(intent);
    }
}
