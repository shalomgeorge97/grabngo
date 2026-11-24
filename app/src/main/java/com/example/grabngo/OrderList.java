package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.dao.OrderDao;
import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.CustomerOrder;
import com.example.grabngo.util.OrderStatus;
import com.example.grabngo.util.SessionManager;
import com.example.grabngo.util.BottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderList extends AppCompatActivity {

    private static final int DEFAULT_RESTAURANT_ID = 1;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<CustomerOrder> allOrders = new ArrayList<>();

    private AppDatabase db;
    private OrderDao orderDao;
    private SessionManager sessionManager;

    private OrderAdapter adapter;
    private MaterialButtonToggleGroup filterToggleGroup;
    private EditText etSearch;

    private int activeFilterButtonId = R.id.btnPending;
    private String searchQuery = "";
    private boolean hasLoadedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_list);
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

        db = AppDatabase.getDatabase(this);
        orderDao = db.orderDao();
        sessionManager = new SessionManager(this);

        bindViews();
        setupRecyclerView();
        setupFiltersAndSearch();
        setupBottomNavigation();

        loadOrdersFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNav);

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_orders);
        }

        if (hasLoadedOnce) {
            loadOrdersFromDatabase();
        }
    }

    private void bindViews() {
        filterToggleGroup = findViewById(R.id.filterToggleGroup);
        etSearch = findViewById(R.id.etSearch);

        int checkedId = filterToggleGroup.getCheckedButtonId();
        if (checkedId != View.NO_ID) {
            activeFilterButtonId = checkedId;
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderAdapter(new ArrayList<>(), order -> {
            Intent intent = new Intent(OrderList.this, OrderDetails.class);
            intent.putExtra("order_id", order.orderId);
            startActivity(intent);
        });
        rvOrders.setAdapter(adapter);
    }

    private void setupFiltersAndSearch() {
        filterToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            activeFilterButtonId = checkedId;
            applyFilters();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s != null ? s.toString().trim().toLowerCase(Locale.ROOT) : "";
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void loadOrdersFromDatabase() {
        executor.execute(() -> {
            try {
                int restaurantId = resolveRestaurantId();
                List<CustomerOrder> loaded = orderDao.getCustomerOrdersByRestaurantId(restaurantId);

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    hasLoadedOnce = true;
                    allOrders.clear();
                    if (loaded != null) {
                        allOrders.addAll(loaded);
                    }
                    applyFilters();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    hasLoadedOnce = true;
                    allOrders.clear();
                    adapter.setData(new ArrayList<>());
                    Toast.makeText(OrderList.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private int resolveRestaurantId() {
        int extraRestaurantId = getIntent().getIntExtra("restaurant_id", -1);
        if (extraRestaurantId > 0) {
            return extraRestaurantId;
        }
        // If your Restaurant entity/DAO links a manager userId to a restaurantId, query it here;
        // otherwise use the seeded restaurant ID for testing.
        if (sessionManager.hasActiveSession()
                && SessionManager.ROLE_MANAGER.equals(sessionManager.getUserRole())) {
            return DEFAULT_RESTAURANT_ID;
        }
        return DEFAULT_RESTAURANT_ID;
    }

    private void applyFilters() {
        List<CustomerOrder> filtered = new ArrayList<>();

        for (CustomerOrder order : allOrders) {
            if (order == null) {
                continue;
            }
            if (!matchesStatusFilter(order.status, activeFilterButtonId)) {
                continue;
            }
            if (!matchesSearchQuery(order, searchQuery)) {
                continue;
            }
            filtered.add(order);
        }

        adapter.setData(filtered);

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No matching orders found.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean matchesStatusFilter(String rawStatus, int filterButtonId) {
        if (filterButtonId == R.id.btnAll) {
            return true;
        }
        String normalized = rawStatus != null ? rawStatus.trim().toUpperCase(Locale.ROOT) : "";

        if (filterButtonId == R.id.btnPending) {
            return OrderStatus.PENDING.equalsIgnoreCase(normalized);
        }
        if (filterButtonId == R.id.btnAccepted) {
            return "ACCEPTED".equals(normalized) || OrderStatus.ACCEPTED.equalsIgnoreCase(normalized);
        }
        if (filterButtonId == R.id.btnRejected) {
            return "REJECTED".equals(normalized) || OrderStatus.REJECTED.equalsIgnoreCase(normalized);
        }
        if (filterButtonId == R.id.btnCompleted) {
            return "COMPLETED".equals(normalized)
                    || OrderStatus.PICKED_UP.equalsIgnoreCase(normalized)
                    || OrderStatus.DELIVERED.equalsIgnoreCase(normalized);
        }
        return true;
    }

    private boolean matchesSearchQuery(CustomerOrder order, String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }
        String orderIdText = String.valueOf(order.orderId);
        String customerText = order.customerName != null ? order.customerName.toLowerCase(Locale.ROOT) : "";
        String statusText = order.status != null ? order.status.toLowerCase(Locale.ROOT) : "";
        String fulfillmentText = order.fulfillmentType != null ? order.fulfillmentType.toLowerCase(Locale.ROOT) : "";

        return orderIdText.contains(query)
                || customerText.contains(query)
                || statusText.contains(query)
                || fulfillmentText.contains(query);
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNav);

        BottomNavigationHelper.configure(
                this,
                bottomNav,
                R.id.nav_orders
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}