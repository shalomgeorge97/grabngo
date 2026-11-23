package com.example.grabngo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.adapter.OrderItemAdapter;
import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.Order;
import com.example.grabngo.entity.OrderItemDetails;
import com.example.grabngo.entity.Restaurant;
import com.example.grabngo.model.OrderLine;
import com.example.grabngo.util.OrderStatus;
import com.example.grabngo.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import androidx.core.content.ContextCompat;

public class CustomerOrderDetails extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private AppDatabase db;
    private OrderItemAdapter itemAdapter;
    private Button btnCancelOrder;

    private int orderId;
    private Order currentOrder;
    private boolean isCancelling = false;

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

        orderId = getIntent().getIntExtra(EXTRA_ORDER_ID, -1);
        if (orderId <= 0) {
            Toast.makeText(this, "Invalid order.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = AppDatabase.getDatabase(getApplicationContext());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rvItems = findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new OrderItemAdapter(new ArrayList<>());
        rvItems.setAdapter(itemAdapter);

        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnCancelOrder.setOnClickListener(v -> confirmCancel());

        loadOrder();
    }

    // Reads the order, its items and the restaurant name (background thread)
    private void loadOrder() {
        int customerId = new SessionManager(this).getUserId();

        executor.execute(() -> {
            Order order = db.orderDao().getOrderById(orderId);

            // A customer may only see their own orders
            if (order == null || order.getCustomerId() != customerId) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Order not found.", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            List<OrderItemDetails> details = db.orderItemDao().getOrderItemDetails(orderId);
            Restaurant restaurant = db.restaurantDao().getRestaurantById(order.getRestaurantId());
            String restaurantName = restaurant == null ? "" : restaurant.getRestaurantName();

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                currentOrder = order;
                showOrder(order, restaurantName);
                showStatusTrail(order);
                showItems(details);
                btnCancelOrder.setVisibility(
                        OrderStatus.PENDING.equals(order.getStatus())
                                ? android.view.View.VISIBLE : android.view.View.GONE);
            });
        });
    }

    private void showOrder(Order order, String restaurantName) {
        ((TextView) findViewById(R.id.tvOrderId)).setText(
                String.format(Locale.getDefault(), "Order #%d", order.getOrderId()));
        ((TextView) findViewById(R.id.tvStatusBadge)).setText(order.getStatus());

        List<String> parts = new ArrayList<>();
        if (!restaurantName.isEmpty()) parts.add(restaurantName);
        parts.add(order.getFulfillmentType());
        parts.add(order.getOrderDate());
        String address = order.getDeliveryAddress();
        if (address != null && !address.trim().isEmpty()) parts.add(address.trim());
        ((TextView) findViewById(R.id.tvPartyInfo)).setText(String.join("  |  ", parts));

        ((TextView) findViewById(R.id.tvTotalAmount)).setText(
                String.format(Locale.getDefault(), "$%.2f", order.getTotalAmount()));
    }

    private void showItems(List<OrderItemDetails> details) {
        List<OrderLine> lines = new ArrayList<>();
        if (details != null) {
            for (OrderItemDetails d : details) {
                // OrderLine wants the line total, so unit price x quantity
                lines.add(new OrderLine(d.getFoodName(), d.getQuantity(),
                        d.getPrice() * d.getQuantity()));
            }
        }
        itemAdapter.setItems(lines);
    }

    private void confirmCancel() {
        if (currentOrder == null || isCancelling) return;
        new AlertDialog.Builder(this)
                .setMessage(R.string.customer_order_cancel_confirm)
                .setPositiveButton(R.string.customer_order_cancel, (d, w) -> cancelOrder())
                .setNegativeButton(R.string.customer_order_keep, null)
                .show();
    }

    // OrderTransactionDao.cancelOrder only works while Pending and puts the stock back
    private void cancelOrder() {
        isCancelling = true;
        btnCancelOrder.setEnabled(false);

        executor.execute(() -> {
            boolean cancelled;
            try {
                cancelled = db.orderTransactionDao().cancelOrder(orderId);
            } catch (Exception e) {
                cancelled = false;
            }
            boolean ok = cancelled;

            runOnUiThread(() -> {
                isCancelling = false;
                btnCancelOrder.setEnabled(true);
                Toast.makeText(this,
                        ok ? R.string.customer_order_cancelled : R.string.customer_order_cancel_failed,
                        Toast.LENGTH_SHORT).show();
                loadOrder(); // shows the new status and hides the button
            });
        });
    }

    // Shows the steps of the order, highlighting the ones already reached
    private void showStatusTrail(Order order) {
        TextView trail = findViewById(R.id.tvStatusTrail);
        String status = order.getStatus();

        if (OrderStatus.CANCELLED.equals(status)) {
            trail.setText("Order cancelled");
            return;
        }
        if (OrderStatus.REJECTED.equals(status)) {
            trail.setText("Order rejected by the restaurant");
            return;
        }

        boolean delivery = order.getFulfillmentType() != null
                && order.getFulfillmentType().toUpperCase(Locale.ROOT).contains("DELIVER");
        String lastStatus = delivery ? OrderStatus.DELIVERED : OrderStatus.PICKED_UP;
        String[] steps = {OrderStatus.PENDING, OrderStatus.ACCEPTED, OrderStatus.READY, lastStatus};
        String[] labels = {"Pending", "Accepted", "Ready", delivery ? "Delivered" : "Picked Up"};

        int current = -1;
        for (int i = 0; i < steps.length; i++) {
            if (steps[i].equals(status)) current = i;
        }

        SpannableStringBuilder text = new SpannableStringBuilder();
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) text.append("  >  ");
            int start = text.length();
            text.append(labels[i]);
            if (i <= current) { // reached steps are bold and green
                text.setSpan(new StyleSpan(Typeface.BOLD), start, text.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(this, R.color.primary)),
                        start, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        trail.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}