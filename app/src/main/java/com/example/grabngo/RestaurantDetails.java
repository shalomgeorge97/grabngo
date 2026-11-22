package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    public static final String EXTRA_RESTAURANT_ID = "restaurant_id";
    public static final String EXTRA_FOOD_IDS = "food_ids";
    public static final String EXTRA_QUANTITIES = "quantities";

    private static final boolean USE_DUMMY_DATA = false;

    // Database work must run off the main thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private AppDatabase db;
    private FoodItemAdapter adapter;
    private RecyclerView rvFoodItems;
    private EditText etSearchFood;
    private Button btnReserve;
    private TextView tvRestaurantName;
    private TextView tvRestaurantAddress;
    private TextView tvRestaurantInfo;

    private int restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_details);

        restaurantId = getIntent().getIntExtra(EXTRA_RESTAURANT_ID, -1);
        if (restaurantId <= 0) {
            Toast.makeText(this, R.string.details_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!USE_DUMMY_DATA) {
            db = AppDatabase.getDatabase(this);
        }

        bindViews();
        setupInsets();
        setupFoodList();
        setupSearch();
        setupReserveButton();

        loadRestaurant();
        loadFoodItems("");
    }

    private void bindViews() {
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        tvRestaurantInfo = findViewById(R.id.tvRestaurantInfo);
        rvFoodItems = findViewById(R.id.rvFoodItems);
        etSearchFood = findViewById(R.id.etSearchFood);
        btnReserve = findViewById(R.id.btnReserve);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupInsets() {
        View topBar = findViewById(R.id.topBar);
        ImageView btnBack = findViewById(R.id.btnBack);
        if (topBar == null && btnBack != null && btnBack.getParent() instanceof View) {
            topBar = (View) btnBack.getParent();
        }

        if (topBar != null) {
            int baseTop = topBar.getPaddingTop();
            int baseBottom = topBar.getPaddingBottom();
            int baseStart = topBar.getPaddingStart();
            int baseEnd = topBar.getPaddingEnd();

            ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
                Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                v.setPaddingRelative(
                        baseStart,
                        baseTop + statusBars.top,
                        baseEnd,
                        baseBottom
                );
                return insets;
            });
        }

        View contentContainer = (View) rvFoodItems.getParent();
        if (contentContainer != null) {
            int baseLeft = contentContainer.getPaddingLeft();
            int baseTop = contentContainer.getPaddingTop();
            int baseRight = contentContainer.getPaddingRight();
            int baseBottom = contentContainer.getPaddingBottom();

            ViewCompat.setOnApplyWindowInsetsListener(contentContainer, (v, insets) -> {
                Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                v.setPadding(
                        baseLeft,
                        baseTop,
                        baseRight,
                        baseBottom + navBars.bottom
                );
                return insets;
            });
        }
    }
    private void setupFoodList() {
        rvFoodItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodItemAdapter(new ArrayList<>());
        rvFoodItems.setAdapter(adapter);
    }

    private void setupSearch() {
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
        btnReserve.setOnClickListener(v -> {
            Map<Integer, Integer> selected = adapter.getSelectedQuantities();
            if (selected == null || selected.isEmpty()) {
                Toast.makeText(this, R.string.details_select_item, Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Integer> foodIds = new ArrayList<>();
            ArrayList<Integer> quantities = new ArrayList<>();

            for (Map.Entry<Integer, Integer> entry : selected.entrySet()) {
                Integer qty = entry.getValue();
                if (qty != null && qty > 0) {
                    foodIds.add(entry.getKey());
                    quantities.add(qty);
                }
            }

            if (foodIds.isEmpty()) {
                Toast.makeText(this, R.string.details_select_item, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(RestaurantDetails.this, Payment.class);
            intent.putExtra(EXTRA_RESTAURANT_ID, restaurantId);
            intent.putIntegerArrayListExtra(EXTRA_FOOD_IDS, foodIds);
            intent.putIntegerArrayListExtra(EXTRA_QUANTITIES, quantities);
            startActivity(intent);
        });
    }

    private void loadRestaurant() {
        if (USE_DUMMY_DATA) {
            handleRestaurantResult(DummyData.getRestaurantById(restaurantId));
            return;
        }

        executor.execute(() -> {
            Restaurant restaurant = db.restaurantDao().getRestaurantById(restaurantId);
            runOnUiThread(() -> handleRestaurantResult(restaurant));
        });
    }

    private void handleRestaurantResult(Restaurant restaurant) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (restaurant == null) {
            Toast.makeText(this, R.string.details_not_found, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        showRestaurant(restaurant);
    }

    private void loadFoodItems(String keyword) {
        if (USE_DUMMY_DATA) {
            List<FoodItem> foods = DummyData.getFoodItems(restaurantId, keyword);
            adapter.setItems(foods != null ? foods : new ArrayList<>());
            updateReserveState(foods);
            return;
        }

        executor.execute(() -> {
            List<FoodItem> foods;
            if (keyword.isEmpty()) {
                foods = db.foodItemDao().getFoodItemsByRestaurantId(restaurantId);
            } else {
                foods = db.foodItemDao().searchFoodItemsByName(restaurantId, keyword);
            }
            List<FoodItem> safeFoods = foods != null ? foods : new ArrayList<>();

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                adapter.setItems(safeFoods);
                updateReserveState(safeFoods);
            });
        });
    }

    private void updateReserveState(List<FoodItem> foods) {
        btnReserve.setEnabled(foods != null && !foods.isEmpty());
    }

    private void showRestaurant(Restaurant restaurant) {
        tvRestaurantName.setText(restaurant.getRestaurantName());
        tvRestaurantAddress.setText(
                getString(
                        R.string.restaurant_address_format,
                        restaurant.getAddress(),
                        restaurant.getCity()
                )
        );

        List<String> parts = new ArrayList<>();
        if (restaurant.getOpeningHours() != null && !restaurant.getOpeningHours().trim().isEmpty()) {
            parts.add(restaurant.getOpeningHours().trim());
        }
        if (restaurant.getContactInfo() != null && !restaurant.getContactInfo().trim().isEmpty()) {
            parts.add(restaurant.getContactInfo().trim());
        }
        if (restaurant.getRating() != null) {
            parts.add(getString(R.string.details_rating_format, restaurant.getRating()));
        }

        if (parts.isEmpty()) {
            tvRestaurantInfo.setVisibility(View.GONE);
        } else {
            tvRestaurantInfo.setVisibility(View.VISIBLE);
            tvRestaurantInfo.setText(String.join("  |  ", parts));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}