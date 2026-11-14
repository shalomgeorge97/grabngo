package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.entity.Restaurant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {

    // Data shown in the list
    private final List<Restaurant> restaurantList = new ArrayList<>();
    private final Map<Integer, Integer> itemCounts = new HashMap<>();
    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRestaurantList();
        setupBottomNavigation();
    }

    // Sets up the RecyclerView with the adapter
    private void setupRestaurantList() {
        RecyclerView rvRestaurants = findViewById(R.id.rvRestaurants);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));

        loadFakeRestaurants(); // temporary, replaced by database data later

        adapter = new RestaurantAdapter(restaurantList, itemCounts, restaurant -> {
            Intent intent = new Intent(Home.this, RestaurantDetails.class);
            intent.putExtra("restaurant_id", restaurant.getRestaurantId());
            startActivity(intent);
        });
        rvRestaurants.setAdapter(adapter);
    }

    // Handles taps on the bottom bar
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_orders) {
                startActivity(new Intent(Home.this, OrderHistory.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(Home.this, Profile.class));
                return true;
            }
            return true; // Home: stay on this screen
        });
    }

    // Temporary test data only, so the design can be checked
    private void loadFakeRestaurants() {
        Restaurant r1 = new Restaurant(1, "Green Bowl", "123 Main St", "New Westminster",
                "604-555-0101", "9am - 9pm", 4.5, null);
        r1.setRestaurantId(1);
        itemCounts.put(1, 5);

        Restaurant r2 = new Restaurant(2, "Fresh Bakery", "45 Royal Ave", "New Westminster",
                "604-555-0102", "7am - 6pm", 4.2, null);
        r2.setRestaurantId(2);
        itemCounts.put(2, 3);

        Restaurant r3 = new Restaurant(3, "Spice Kitchen", "88 Columbia St", "Vancouver",
                "604-555-0103", "11am - 10pm", 4.7, null);
        r3.setRestaurantId(3);
        itemCounts.put(3, 1);

        restaurantList.add(r1);
        restaurantList.add(r2);
        restaurantList.add(r3);
    }
}