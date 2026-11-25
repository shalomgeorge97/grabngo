package com.example.grabngo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.entity.FoodItem;
import com.example.grabngo.entity.Restaurant;
import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.util.BottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.view.View;

public class Home extends AppCompatActivity {

    // Data shown in the list
    private final List<Restaurant> restaurantList = new ArrayList<>();
    private final Map<Integer, Integer> itemCounts = new HashMap<>();
    private RestaurantAdapter adapter;
    private final List<Restaurant> allRestaurants = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );
            return insets;
        });

        setupRestaurantList();
        setupBottomNavigation();

        // >>> START: Ami's implementation - Reminder functionality
        updateUnreadReminderDot();

        ImageView reminderButton = findViewById(R.id.btnReminders);
        findViewById(R.id.btnReminders).setOnClickListener(v -> {
            reminderButton.setImageResource(R.drawable.bell);
            startActivity(new Intent(Home.this, ReminderActivity.class));
        });
        // <<< END: Ami's implementation
    }

    // Implemented by Ami
    private void updateUnreadReminderDot() {
        SharedPreferences preferences = getSharedPreferences(
                "GrabNGoSession", MODE_PRIVATE);
        int customerId = preferences.getInt("user_id", -1);

        boolean hasUnreadReminder = false;
        if (customerId > 0) {
            hasUnreadReminder = AppDatabase.getDatabase(getApplicationContext())
                    .reminderDao().hasUnreadLatestReminder(customerId);
        }

        ImageView reminderButton = findViewById(R.id.btnReminders);
        if (hasUnreadReminder) {
            reminderButton.setImageResource(R.drawable.bell_unread);
        } else {
            reminderButton.setImageResource(R.drawable.bell);
        }
    }

    // Sets up the RecyclerView with the adapter
    private void setupRestaurantList() {
        RecyclerView rvRestaurants = findViewById(R.id.rvRestaurants);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RestaurantAdapter(restaurantList, itemCounts, restaurant -> {
            Intent intent = new Intent(Home.this, RestaurantDetails.class);
            intent.putExtra("restaurant_id", restaurant.getRestaurantId());
            startActivity(intent);
        });
        rvRestaurants.setAdapter(adapter);

        // Search box: filters by restaurant name or city while typing
        TextInputEditText search = findViewById(R.id.etSearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { }
            @Override public void afterTextChanged(Editable s) {
                searchText = s.toString().trim().toLowerCase();
                applyFilter();
            }
        });

    }

    // Reads restaurants and their food counts from the database (background thread)
    private void loadRestaurants() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Restaurant> restaurants = db.restaurantDao().getAllRestaurants();
            Map<Integer, Integer> counts = new HashMap<>();
            for (Restaurant r : restaurants) {
                int available = 0;
                for (FoodItem f : db.foodItemDao().getFoodItemsByRestaurantId(r.getRestaurantId())) {
                    if (f.getAvailableQuantity() > 0) {
                        available++;
                    }
                }
                counts.put(r.getRestaurantId(), available);
            }
            runOnUiThread(() -> {
                allRestaurants.clear();
                allRestaurants.addAll(restaurants);
                itemCounts.clear();
                itemCounts.putAll(counts);
                applyFilter();
            });
        });
    }

    // Shows only the restaurants that match the search text
    private void applyFilter() {
        restaurantList.clear();
        for (Restaurant r : allRestaurants) {
            String name = r.getRestaurantName() == null ? "" : r.getRestaurantName().toLowerCase();
            String city = r.getCity() == null ? "" : r.getCity().toLowerCase();
            String address = r.getAddress() == null ? "" : r.getAddress().toLowerCase();
            if (searchText.isEmpty() || name.contains(searchText)
                    || city.contains(searchText)
                    || address.contains(searchText)) {
                restaurantList.add(r);
            }
        }
        adapter.notifyDataSetChanged();
        boolean empty = restaurantList.isEmpty();
        findViewById(R.id.tvEmpty).setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    // Handles taps on the bottom bar
    private void setupBottomNavigation() {

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNav);

        BottomNavigationHelper.configure(
                this,
                bottomNav,
                R.id.nav_home
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BottomNavigationView) findViewById(R.id.bottomNav)).setSelectedItemId(R.id.nav_home);
        loadRestaurants();
    }
}
