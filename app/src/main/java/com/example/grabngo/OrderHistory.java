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

public class OrderHistory extends AppCompatActivity {

    private final List<Order> orderList = new ArrayList<>();
    private final Map<Integer, String> restaurantNames = new HashMap<>();
    private OrderHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupOrderList();
        setupBottomNavigation();
    }

    private void setupOrderList() {
        RecyclerView rvOrderHistory = findViewById(R.id.rvOrderHistory);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        loadFakeOrders(); // temporary, replaced by database data later

        adapter = new OrderHistoryAdapter(orderList, restaurantNames, order -> {
            Intent intent = new Intent(OrderHistory.this, OrderDetails.class);
            intent.putExtra("order_id", order.getOrderId());
            startActivity(intent);
        });
        rvOrderHistory.setAdapter(adapter);

        updateEmptyState();
    }

    // Shows a message instead of an empty screen when there are no orders
    private void updateEmptyState() {
        findViewById(R.id.tvEmpty).setVisibility(
                orderList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_orders);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(OrderHistory.this, Home.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(OrderHistory.this, Profile.class));
                return true;
            }
            return true; // Orders: stay on this screen
        });
    }

    // Temporary test data only, so the design can be checked
    private void loadFakeOrders() {
        addFakeOrder(1042, 1, "2026-11-12", "Pickup", "Picked Up", 16.00);
        addFakeOrder(1031, 2, "2026-11-08", "Pickup", "Picked Up", 9.50);
        addFakeOrder(1020, 3, "2026-11-03", "Delivery", "Rejected", 12.00);
        addFakeOrder(1008, 1, "2026-10-28", "Pickup", "Cancelled", 7.00);

        // Turn restaurant ids into names for the list
        for (Order order : orderList) {
            Restaurant restaurant = DummyData.getRestaurantById(order.getRestaurantId());
            if (restaurant != null) {
                restaurantNames.put(restaurant.getRestaurantId(),
                        restaurant.getRestaurantName());
            }
        }
    }

    private void addFakeOrder(int orderId, int restaurantId, String date,
                              String type, String status, double total) {
        Order order = new Order(1, restaurantId, date, type, status, total, null);
        order.setOrderId(orderId);
        orderList.add(order);
    }
}