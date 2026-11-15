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

import com.example.grabngo.entity.Order;
import com.example.grabngo.entity.Restaurant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderList extends AppCompatActivity {

//    should be using CustomerOrder Entity as we need customer name to be shown here
    private final List<Order> orderList = new ArrayList<>();

    private final Map<Integer, Integer> itemCounts = new HashMap<>();
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.setupBottomNavigation();
        setupOrderList();
    }

    private void setupOrderList() {
        RecyclerView rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        loadFakeOrrders(); // temporary, replaced by database data later

        adapter = new OrderAdapter(orderList, order -> {
            Intent intent = new Intent(OrderList.this, OrderDetails.class);
            intent.putExtra("order_id", order.getOrderId());
            startActivity(intent);
        });
        rvOrders.setAdapter(adapter);
    }


    // Temporary test data only, so the design can be checked
    private void loadFakeOrrders() {
        Order r1 = new Order(1, 2, "11-11-26", "Pickup", "Pending",
                  15, "20, Main street");
        r1.setOrderId(1);

        Order r2 = new Order(2, 2, "11-11-26", "Delivery", "Accepted",
                20, "20, Main street");
        r2.setOrderId(2);

        Order r3 = new Order(3, 2, "11-11-26", "Pickup", "Ready",
                25, "20, Main street");
        r3.setOrderId(3);

        orderList.add(r1);
        orderList.add(r2);
        orderList.add(r3);
    }



    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_orders);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(OrderList.this, Home.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(OrderList.this, Profile.class));
                return true;
            }
            return true; // stay on this screen
        });
    }
}