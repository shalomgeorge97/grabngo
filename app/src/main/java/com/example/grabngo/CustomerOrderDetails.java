package com.example.grabngo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.adapter.OrderItemAdapter;
import com.example.grabngo.entity.Order;
import com.example.grabngo.model.OrderLine;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrderDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_customer_order_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Find the RecyclerView (its id is flattened into this Activity's namespace via the <include>)
        RecyclerView rvItems = findViewById(R.id.rvItems);

        // 2. Give it a layout manager (how rows are arranged - here, a simple vertical list)
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // 3. Give it data through the adapter
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("Blueberry muffin", 2, 8.00));
        lines.add(new OrderLine("Croissant", 1, 4.00));
        lines.add(new OrderLine("Chocolate Cookie", 2, 4.00));


        Order order = new Order(1, 2, "11-11-26", "Delivery", "Pending",
                15, "20, Main street");
        order.setOrderId(1);

        OrderItemAdapter adapter = new OrderItemAdapter(lines);
        rvItems.setAdapter(adapter);
    }
}