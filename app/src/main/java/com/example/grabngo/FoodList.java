package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.adapter.FoodListAdapter;
import com.example.grabngo.dao.FoodItemDao;
import com.example.grabngo.dao.RestaurantDao;
import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.FoodItem;
import com.example.grabngo.entity.Restaurant;
import com.example.grabngo.util.BottomNavigationHelper;
import com.example.grabngo.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodList extends AppCompatActivity implements FoodListAdapter.OnFoodActionListener {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<FoodItem> allFoodItems = new ArrayList<>();

    private SessionManager sessionManager;
    private RestaurantDao restaurantDao;
    private FoodItemDao foodItemDao;

    private FoodListAdapter adapter;
    private TextView txtFoodListTitle;
    private TextView txtEmptyFoodList;
    private RecyclerView rvManagerFoodList;
    private ExtendedFloatingActionButton fabAddFood;

    private int restaurantId = -1;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        if (!sessionManager.hasActiveSession()
                || !SessionManager.ROLE_MANAGER.equals(sessionManager.getUserRole())) {
            openWelcome();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_list);

        View topBarContainer = findViewById(R.id.topBarContainer);
        if (topBarContainer != null) {
            ViewCompat.setOnApplyWindowInsetsListener(topBarContainer, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        BottomNavigationHelper.configure(this, bottomNav, R.id.nav_food);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        restaurantDao = db.restaurantDao();
        foodItemDao = db.foodItemDao();

        initViews();
        setupSearch();
        setupAddButton();

        loadManagerRestaurantAndFoods();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (restaurantId != -1) {
            reloadFoods();
        }
    }

    private void initViews() {
        txtFoodListTitle = findViewById(R.id.txtFoodListTitle);
        txtEmptyFoodList = findViewById(R.id.txtEmptyFoodList);
        rvManagerFoodList = findViewById(R.id.rvManagerFoodList);
        fabAddFood = findViewById(R.id.fabAddFood);

        adapter = new FoodListAdapter(this);
        rvManagerFoodList.setLayoutManager(new LinearLayoutManager(this));
        rvManagerFoodList.setAdapter(adapter);
    }

    private void setupSearch() {
        EditText etSearchFood = findViewById(R.id.etSearchFood);
        etSearchFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s != null ? s.toString().trim().toLowerCase(Locale.ROOT) : "";
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setupAddButton() {
        fabAddFood.setOnClickListener(v -> {
            if (restaurantId == -1) {
                Toast.makeText(this, "No restaurant assigned to this manager", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, AddEditFood.class);
            intent.putExtra("restaurantId", restaurantId);
            startActivity(intent);
        });
    }

    private void loadManagerRestaurantAndFoods() {
        int managerId = sessionManager.getUserId();
        executor.execute(() -> {
            Restaurant restaurant = restaurantDao.getRestaurantByManagerId(managerId);
            if (restaurant == null) {
                runOnUiThread(() -> {
                    restaurantId = -1;
                    fabAddFood.setEnabled(false);
                    txtEmptyFoodList.setText("No restaurant found for this manager account.");
                    txtEmptyFoodList.setVisibility(View.VISIBLE);
                    rvManagerFoodList.setVisibility(View.GONE);
                });
                return;
            }

            restaurantId = restaurant.getRestaurantId();
            List<FoodItem> items = foodItemDao.getFoodItemsByRestaurantId(restaurantId);

            runOnUiThread(() -> {
                fabAddFood.setEnabled(true);
                txtFoodListTitle.setText(restaurant.getRestaurantName());
                allFoodItems.clear();
                allFoodItems.addAll(items);
                applyFilter();
            });
        });
    }

    private void reloadFoods() {
        int targetRestaurantId = restaurantId;
        executor.execute(() -> {
            List<FoodItem> items = foodItemDao.getFoodItemsByRestaurantId(targetRestaurantId);
            runOnUiThread(() -> {
                allFoodItems.clear();
                allFoodItems.addAll(items);
                applyFilter();
            });
        });
    }

    private void applyFilter() {
        List<FoodItem> filtered = new ArrayList<>();
        for (FoodItem item : allFoodItems) {
            boolean matchesName = currentQuery.isEmpty()
                    || item.getName().toLowerCase(Locale.ROOT).contains(currentQuery)
                    || item.getCategory().toLowerCase(Locale.ROOT).contains(currentQuery);
            if (matchesName) {
                filtered.add(item);
            }
        }

        adapter.setItems(filtered);
        boolean isEmpty = filtered.isEmpty();
        txtEmptyFoodList.setText(currentQuery.isEmpty()
                ? "No food items yet. Tap Add Food to create one."
                : "No matching food items found.");
        txtEmptyFoodList.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvManagerFoodList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onFoodClick(@NonNull FoodItem item) {
        Intent intent = new Intent(this, AddEditFood.class);
        intent.putExtra("restaurantId", item.getRestaurantId());
        intent.putExtra("foodId", item.getFoodId());
        startActivity(intent);
    }

    @Override
    public void onQuantityChange(@NonNull FoodItem item, int newQuantity) {
        executor.execute(() -> {
            item.setAvailableQuantity(newQuantity);
            foodItemDao.updateFoodItem(item);
            List<FoodItem> updated = foodItemDao.getFoodItemsByRestaurantId(item.getRestaurantId());
            runOnUiThread(() -> {
                allFoodItems.clear();
                allFoodItems.addAll(updated);
                applyFilter();
            });
        });
    }

    private void openWelcome() {
        Intent intent = new Intent(this, Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}