package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.entity.Order;
import com.example.grabngo.entity.Restaurant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.util.SessionManager;
import com.example.grabngo.util.BottomNavigationHelper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderHistory extends AppCompatActivity {

    private final List<Order> orderList = new ArrayList<>();
    private final Map<Integer, String> restaurantNames = new HashMap<>();
    private OrderHistoryAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
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

        setupOrderList();
        setupBottomNavigation();
    }

    private void setupOrderList() {
        RecyclerView rvOrderHistory = findViewById(R.id.rvOrderHistory);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderHistoryAdapter(orderList, restaurantNames, order -> {
            Intent intent = new Intent(OrderHistory.this, CustomerOrderDetails.class);
            intent.putExtra("order_id", order.getOrderId());
            startActivity(intent);
        });
        rvOrderHistory.setAdapter(adapter);
    }

    // Reads the logged-in customer's orders: active ones first, then past ones
    private void loadOrders() {
        int customerId = new SessionManager(this).getUserId();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<Order> loaded = new ArrayList<>();
            Map<Integer, String> names = new HashMap<>();

            if (customerId > 0) {
                loaded.addAll(db.orderDao().getActiveOrdersByCustomerId(customerId));
                loaded.addAll(db.orderDao().getPastOrdersByCustomerId(customerId));

                for (Order o : loaded) {
                    if (!names.containsKey(o.getRestaurantId())) {
                        Restaurant r = db.restaurantDao().getRestaurantById(o.getRestaurantId());
                        if (r != null) {
                            names.put(r.getRestaurantId(), r.getRestaurantName());
                        }
                    }
                }
            }

            runOnUiThread(() -> {
                orderList.clear();
                orderList.addAll(loaded);
                restaurantNames.clear();
                restaurantNames.putAll(names);
                adapter.setData(orderList, restaurantNames);
                updateEmptyState();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNav);

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_orders);
        }

        loadOrders();
    }

    // Shows a message instead of an empty screen when there are no orders
    private void updateEmptyState() {
        findViewById(R.id.tvEmpty).setVisibility(
                orderList.isEmpty() ? View.VISIBLE : View.GONE);
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

    // Temporary test data only, so the design can be checked


}