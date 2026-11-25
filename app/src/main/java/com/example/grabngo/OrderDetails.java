package com.example.grabngo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.adapter.OrderItemAdapter;
import com.example.grabngo.dao.OrderDao;
import com.example.grabngo.dao.OrderItemDao;
import com.example.grabngo.dao.OrderTransactionDao;
import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.Order;
import com.example.grabngo.entity.OrderItem;
import com.example.grabngo.entity.OrderItemDetails;
import com.example.grabngo.model.OrderLine;
import com.example.grabngo.util.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderDetails extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";

    private static final String SPINNER_PROMPT = "Update status...";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private AppDatabase db;
    private OrderDao orderDao;
    private OrderItemDao orderItemDao;

    private OrderTransactionDao orderTransactionDao;

    private OrderItemAdapter itemAdapter;
    private RecyclerView rvItems;
    private LinearLayout acceptRejectRow;
    private LinearLayout updateStatusRow;
    private Spinner spStatus;
    private Button btnAccept;
    private Button btnReject;
    private Button btnUpdateStatus;

    private int orderId;
    private Order currentOrder;
    private String selectedStatus;
    private List<String> spinnerOptions = new ArrayList<>();
    private boolean isBindingSpinner = false;
    private boolean isMutating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        orderId = getIntent().getIntExtra(EXTRA_ORDER_ID, -1);
        if (orderId <= 0) {
            orderId = getIntent().getIntExtra("orderId", -1);
        }
        if (orderId <= 0) {
            Toast.makeText(this, "Invalid order.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = AppDatabase.getDatabase(this);
        orderDao = db.orderDao();
        orderItemDao = db.orderItemDao();
        orderTransactionDao = db.orderTransactionDao();

        bindViews();
        setupRecyclerView();
        setupListeners();

        loadOrderDetails();
    }

    private void bindViews() {
        rvItems = findViewById(R.id.rvItems);
        acceptRejectRow = findViewById(R.id.acceptRejectRow);
        updateStatusRow = findViewById(R.id.updateStatusRow);
        spStatus = findViewById(R.id.spStatus);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new OrderItemAdapter(new ArrayList<>());
        rvItems.setAdapter(itemAdapter);
    }

    private void setupListeners() {
        btnAccept.setOnClickListener(v -> {
            if (currentOrder == null || isMutating) {
                return;
            }
            if (!OrderStatus.canManagerAcceptOrReject(currentOrder.getStatus())) {
                Toast.makeText(this, "Order can only be accepted while Pending.", Toast.LENGTH_SHORT).show();
                return;
            }
            applyStatusTransition(OrderStatus.ACCEPTED, false);
        });

        btnReject.setOnClickListener(v -> {
            if (currentOrder == null || isMutating) {
                return;
            }
            if (!OrderStatus.canManagerAcceptOrReject(currentOrder.getStatus())) {
                Toast.makeText(this, "Order can only be rejected while Pending.", Toast.LENGTH_SHORT).show();
                return;
            }
            applyStatusTransition(OrderStatus.REJECTED, true);
        });

        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isBindingSpinner || isMutating || currentOrder == null) {
                    return;
                }
                if (position <= 0) {
                    selectedStatus = null;
                    btnUpdateStatus.setEnabled(false);
                    return;
                }
                int optionIndex = position - 1;
                if (optionIndex >= spinnerOptions.size()) {
                    return;
                }
                String targetStatus = spinnerOptions.get(optionIndex);
                if (targetStatus == null || targetStatus.equals(currentOrder.getStatus())) {
                    return;
                }
                if (!isTransitionAllowed(currentOrder, targetStatus)) {
                    Toast.makeText(OrderDetails.this, "Status transition not allowed.", Toast.LENGTH_SHORT).show();
                    bindActionControls(currentOrder);
                    return;
                }
                selectedStatus = targetStatus;
                btnUpdateStatus.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnUpdateStatus.setOnClickListener(v -> {
            if (currentOrder == null || isMutating || selectedStatus == null) {
                return;
            }

            applyStatusTransition(selectedStatus, false);
        });
    }

    private void loadOrderDetails() {
        setActionsEnabled(false);

        executor.execute(() -> {
            try {
                Order order = orderDao.getOrderById(orderId);
                List<OrderItemDetails> details = order != null
                        ? orderItemDao.getOrderItemDetails(orderId)
                        : new ArrayList<>();

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    if (order == null) {
                        Toast.makeText(this, "Order not found.", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    currentOrder = order;
                    renderOrderSummary(order);
                    renderOrderItems(details);
                    bindActionControls(order);
                    setActionsEnabled(!isMutating);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    Toast.makeText(this, "Failed to load order details.", Toast.LENGTH_SHORT).show();
                    setActionsEnabled(!isMutating);
                });
            }
        });
    }

    private void applyStatusTransition(String targetStatus, boolean restoreStock) {
        final int targetOrderId = currentOrder.getOrderId();
        isMutating = true;
        setActionsEnabled(false);

        executor.execute(() -> {
            try {
                boolean updated = db.runInTransaction(() -> {
                    Order latestOrder = orderDao.getOrderById(targetOrderId);
                    if (latestOrder == null) {
                        return false;
                    }
                    if (!isTransitionAllowed(latestOrder, targetStatus)) {
                        return false;
                    }
                    if (restoreStock && !OrderStatus.canManagerAcceptOrReject(latestOrder.getStatus())) {
                        return false;
                    }

                    int rows = orderDao.updateOrderStatus(targetOrderId, targetStatus);
                    if (rows <= 0) {
                        return false;
                    }

                    if (restoreStock) {
                        restoreStockForOrder(targetOrderId);
                    }

                    // >>> START: implemented by Ami - Reminder function
                    // Save the reminder with the status update
                    db.reminderDao().createStatusReminder(targetOrderId, targetStatus);
                    // <<< END: implemented by Ami
                    return true;
                });

                Order refreshedOrder = orderDao.getOrderById(targetOrderId);
                List<OrderItemDetails> refreshedItems = refreshedOrder != null
                        ? orderItemDao.getOrderItemDetails(targetOrderId)
                        : new ArrayList<>();

                runOnUiThread(() -> {
                    isMutating = false;
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    if (!updated || refreshedOrder == null) {
                        Toast.makeText(this, "Could not update order status.", Toast.LENGTH_SHORT).show();
                        if (refreshedOrder != null) {
                            currentOrder = refreshedOrder;
                            bindActionControls(refreshedOrder);
                        }
                        setActionsEnabled(true);
                        return;
                    }

                    currentOrder = refreshedOrder;
                    renderOrderSummary(refreshedOrder);
                    renderOrderItems(refreshedItems);
                    bindActionControls(refreshedOrder);
                    setActionsEnabled(true);
                    Toast.makeText(this, "Order updated to " + targetStatus + ".", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isMutating = false;
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    Toast.makeText(this, "Error saving order update.", Toast.LENGTH_SHORT).show();
                    setActionsEnabled(true);
                });
            }
        });
    }

    private boolean isTransitionAllowed(Order order, String targetStatus) {
        if (order == null || targetStatus == null) {
            return false;
        }
        String fromStatus = order.getStatus();
        String fulfillment = normalizeFulfillmentType(order.getFulfillmentType());
        List<String> allowed = OrderStatus.getAllowedNextStatuses(fromStatus, fulfillment);
        return allowed.contains(targetStatus);
    }

    private String normalizeFulfillmentType(String rawFulfillment) {
        if (rawFulfillment == null) {
            return OrderStatus.FULFILLMENT_PICKUP;
        }
        String upper = rawFulfillment.trim().toUpperCase(Locale.ROOT);
        if (upper.contains("DELIVER")) {
            return OrderStatus.FULFILLMENT_DELIVERY;
        }
        return OrderStatus.FULFILLMENT_PICKUP;
    }

    private void restoreStockForOrder(int targetOrderId) {
        List<OrderItem> items = orderTransactionDao.getOrderItemsByOrderId(targetOrderId);
        if (items == null || items.isEmpty()) {
            return;
        }
        for (OrderItem item : items) {
            if (item != null && item.getQuantity() > 0) {
                orderTransactionDao.increaseAvailableQuantity(item.getFoodId(), item.getQuantity());
            }
        }
    }

    private void renderOrderSummary(Order order) {
        setTextIfPresent(
                R.id.tvOrderId,
                String.format(Locale.getDefault(), "Order #%d", order.getOrderId())
        );
        setTextIfPresent(R.id.tvStatusBadge, order.getStatus());

        List<String> infoParts = new ArrayList<>();
        if (order.getFulfillmentType() != null && !order.getFulfillmentType().trim().isEmpty()) {
            infoParts.add(order.getFulfillmentType().trim());
        }
        if (order.getOrderDate() != null && !order.getOrderDate().trim().isEmpty()) {
            infoParts.add(order.getOrderDate().trim());
        }
        if (order.getDeliveryAddress() != null && !order.getDeliveryAddress().trim().isEmpty()) {
            infoParts.add(order.getDeliveryAddress().trim());
        }
        setTextIfPresent(R.id.tvPartyInfo, String.join("  |  ", infoParts));

        setTextIfPresent(
                R.id.tvTotalAmount,
                String.format(Locale.getDefault(), "$%.2f", order.getTotalAmount())
        );
    }

    private void renderOrderItems(List<OrderItemDetails> details) {
        List<OrderLine> lines = new ArrayList<>();
        if (details != null) {
            for (OrderItemDetails detail : details) {
                lines.add(new OrderLine(
                        detail.getFoodName(),
                        detail.getQuantity(),
                        detail.getPrice()
                ));
            }
        }
        itemAdapter.setItems(lines);
    }

    private void bindActionControls(Order order) {
        String status = order.getStatus();
        selectedStatus = null;
        boolean canAcceptOrReject = OrderStatus.canManagerAcceptOrReject(status);
        acceptRejectRow.setVisibility(canAcceptOrReject ? View.VISIBLE : View.GONE);

        String fulfillment = normalizeFulfillmentType(order.getFulfillmentType());
        List<String> allowedNext = OrderStatus.getAllowedNextStatuses(status, fulfillment);

        spinnerOptions = new ArrayList<>();
        for (String next : allowedNext) {
            if (OrderStatus.PENDING.equals(status)
                    && (OrderStatus.ACCEPTED.equals(next) || OrderStatus.REJECTED.equals(next))) {
                continue;
            }
            spinnerOptions.add(next);
        }

        if (spinnerOptions.isEmpty() || OrderStatus.isFinal(status)) {
            updateStatusRow.setVisibility(View.GONE);
            btnUpdateStatus.setVisibility(View.GONE);
        } else {
            updateStatusRow.setVisibility(View.VISIBLE);
            btnUpdateStatus.setVisibility(View.VISIBLE);
            btnUpdateStatus.setEnabled(false);
            isBindingSpinner = true;

            List<String> displayOptions = new ArrayList<>();
            displayOptions.add(SPINNER_PROMPT);
            displayOptions.addAll(spinnerOptions);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    displayOptions
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStatus.setAdapter(spinnerAdapter);
            spStatus.setSelection(0, false);
            spStatus.post(() -> isBindingSpinner = false);
        }

    }

    private void setActionsEnabled(boolean enabled) {
        btnAccept.setEnabled(enabled);
        btnReject.setEnabled(enabled);
        spStatus.setEnabled(enabled);
        //check if targeted status is selected in spinner
        btnUpdateStatus.setEnabled(enabled && selectedStatus != null);
    }

    private void setTextIfPresent(int viewId, String value) {
        View view = findViewById(viewId);
        if (view instanceof TextView && value != null) {
            ((TextView) view).setText(value);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
