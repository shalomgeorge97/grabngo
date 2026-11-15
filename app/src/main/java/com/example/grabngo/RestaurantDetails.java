package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.FoodItem;
import com.example.grabngo.entity.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestaurantDetails extends AppCompatActivity {

    // Database work must run off the main thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private AppDatabase db;
    private FoodItemAdapter adapter;
    private int restaurantId;

    private static final boolean USE_DUMMY_DATA = true;  // testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        if (restaurantId == -1) {
            finish(); // no restaurant was passed in
            return;
        }

        db = AppDatabase.getDatabase(this);

        if (!USE_DUMMY_DATA) {
            db = AppDatabase.getDatabase(this);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupFoodList();
        setupSearch();
        setupReserveButton();

        loadRestaurant();
        loadFoodItems("");
    }

    private void setupFoodList() {
        RecyclerView rvFoodItems = findViewById(R.id.rvFoodItems);
        rvFoodItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodItemAdapter(new ArrayList<>());
        rvFoodItems.setAdapter(adapter);
    }

    // Search the food list as the user types
    private void setupSearch() {
        EditText etSearchFood = findViewById(R.id.etSearchFood);
        etSearchFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                loadFoodItems(s.toString().trim());
            }
        });
    }

    private void setupReserveButton() {
        findViewById(R.id.btnReserve).setOnClickListener(v -> {
            Map<Integer, Integer> selected = adapter.getSelectedQuantities();
            if (selected.isEmpty()) {
                Toast.makeText(this, R.string.details_select_item, Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Integer> foodIds = new ArrayList<>(selected.keySet());
            ArrayList<Integer> quantities = new ArrayList<>();
            for (Integer foodId : foodIds) {
                quantities.add(selected.get(foodId));
            }

            Intent intent = new Intent(RestaurantDetails.this, Payment.class);
            intent.putExtra("restaurant_id", restaurantId);
            intent.putIntegerArrayListExtra("food_ids", foodIds);
            intent.putIntegerArrayListExtra("quantities", quantities);
            startActivity(intent);
        });
    }

    // Loads the restaurant info shown at the top
//    private void loadRestaurant() {
//        executor.execute(() -> {
//            Restaurant restaurant = db.restaurantDao().getRestaurantById(restaurantId);
//            runOnUiThread(() -> {
//                if (restaurant == null) {
//                    finish();
//                    return;
//                }
//                showRestaurant(restaurant);
//            });
//        });
//    }
//
//    // Loads all food items, or only those matching the search keyword
//    private void loadFoodItems(String keyword) {
//        executor.execute(() -> {
//            List<FoodItem> foods;
//            if (keyword.isEmpty()) {
//                foods = db.foodItemDao().getFoodItemsByRestaurantId(restaurantId);
//            } else {
//                foods = db.foodItemDao().searchFoodItemsByName(restaurantId, keyword);
//            }
//            runOnUiThread(() -> adapter.setItems(foods));
//        });
//    }

    //testing dummy

    private void loadRestaurant() {
        if (USE_DUMMY_DATA) {
            showRestaurantOrMessage(DummyData.getRestaurantById(restaurantId));
            return;
        }
        executor.execute(() -> {
            Restaurant restaurant = db.restaurantDao().getRestaurantById(restaurantId);
            runOnUiThread(() -> showRestaurantOrMessage(restaurant));
        });
    }

    private void showRestaurantOrMessage(Restaurant restaurant) {
        if (restaurant == null) {
            Toast.makeText(this, R.string.details_not_found, Toast.LENGTH_LONG).show();
            return;
        }
        showRestaurant(restaurant);
    }

    private void loadFoodItems(String keyword) {
        if (USE_DUMMY_DATA) {
            adapter.setItems(DummyData.getFoodItems(restaurantId, keyword));
            return;
        }
        executor.execute(() -> {
            List<FoodItem> foods;
            if (keyword.isEmpty()) {
                foods = db.foodItemDao().getFoodItemsByRestaurantId(restaurantId);
            } else {
                foods = db.foodItemDao().searchFoodItemsByName(restaurantId, keyword);
            }
            runOnUiThread(() -> adapter.setItems(foods));
        });
    }


    private void showRestaurant(Restaurant restaurant) {
        ((TextView) findViewById(R.id.tvRestaurantName)).setText(restaurant.getRestaurantName());
        ((TextView) findViewById(R.id.tvRestaurantAddress)).setText(
                getString(R.string.restaurant_address_format,
                        restaurant.getAddress(), restaurant.getCity()));

        // Hours, phone and rating are optional, so only show what exists
        List<String> parts = new ArrayList<>();
        if (restaurant.getOpeningHours() != null) parts.add(restaurant.getOpeningHours());
        if (restaurant.getContactInfo() != null) parts.add(restaurant.getContactInfo());
        if (restaurant.getRating() != null) {
            parts.add(getString(R.string.details_rating_format, restaurant.getRating()));
        }
        ((TextView) findViewById(R.id.tvRestaurantInfo))
                .setText(String.join("  |  ", parts));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}