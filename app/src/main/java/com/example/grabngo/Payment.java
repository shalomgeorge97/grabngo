package com.example.grabngo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grabngo.entity.FoodItem;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.entity.Order;
import com.example.grabngo.entity.OrderItem;
import com.example.grabngo.util.OrderStatus;
import com.example.grabngo.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Payment extends AppCompatActivity {

    private TextInputLayout tilAddress, tilCardName, tilCardNumber;
    private TextInputEditText etAddress, etCardName, etCardNumber;
    private MaterialButtonToggleGroup toggleOrderType;

    private int restaurantId;
    private ArrayList<Integer> foodIds;
    private ArrayList<Integer> quantities;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<FoodItem> foods = new ArrayList<>(); // same order as foodIds (can hold null)
    private double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Data sent by the Restaurant Details screen
        restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        foodIds = getIntent().getIntegerArrayListExtra("food_ids");
        quantities = getIntent().getIntegerArrayListExtra("quantities");
        if (foodIds == null || quantities == null || foodIds.isEmpty()) {
            finish(); // nothing to pay for
            return;
        }

        findViews();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        showOrderSummary();
        setupOrderTypeToggle();
        findViewById(R.id.btnConfirmPay).setOnClickListener(v -> confirmOrder());
    }

    private void findViews() {
        tilAddress = findViewById(R.id.tilAddress);
        tilCardName = findViewById(R.id.tilCardName);
        tilCardNumber = findViewById(R.id.tilCardNumber);
        etAddress = findViewById(R.id.etAddress);
        etCardName = findViewById(R.id.etCardName);
        etCardNumber = findViewById(R.id.etCardNumber);
        toggleOrderType = findViewById(R.id.toggleOrderType);
    }

    // Adds one row per food and calculates the total

    // Loads each food from the database, then shows one row per food and the total
    private void showOrderSummary() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<FoodItem> loaded = new ArrayList<>();
            for (int id : foodIds) {
                loaded.add(db.foodItemDao().getFoodItemById(id));
            }

            runOnUiThread(() -> {
                LinearLayout llOrderLines = findViewById(R.id.llOrderLines);
                foods.clear();
                foods.addAll(loaded);
                total = 0;

                for (int i = 0; i < foods.size(); i++) {
                    FoodItem food = foods.get(i);
                    if (food == null) continue;

                    int quantity = quantities.get(i);
                    double linePrice = food.getDiscountedPrice() * quantity;
                    total += linePrice;

                    View line = LayoutInflater.from(this)
                            .inflate(R.layout.item_order_line, llOrderLines, false);
                    ((TextView) line.findViewById(R.id.tvLineName))
                            .setText(getString(R.string.checkout_line_format, food.getName(), quantity));
                    ((TextView) line.findViewById(R.id.tvLinePrice))
                            .setText(getString(R.string.price_format, linePrice));
                    llOrderLines.addView(line);
                }
                ((TextView) findViewById(R.id.tvTotal))
                        .setText(getString(R.string.price_format, total));
            });
        });
    }

    // Shows the address field only when Delivery is selected
    private void setupOrderTypeToggle() {
        toggleOrderType.check(R.id.btnPickup);
        toggleOrderType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                tilAddress.setVisibility(
                        checkedId == R.id.btnDelivery ? View.VISIBLE : View.GONE);
            }
        });
    }

    private boolean isDelivery() {
        return toggleOrderType.getCheckedButtonId() == R.id.btnDelivery;
    }

    private String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    // Checks the form and shows an error on each field that is wrong
    private boolean validateForm() {
        boolean valid = true;
        tilAddress.setError(null);
        tilCardName.setError(null);
        tilCardNumber.setError(null);

        if (isDelivery() && text(etAddress).isEmpty()) {
            tilAddress.setError(getString(R.string.checkout_error_address));
            valid = false;
        }
        if (text(etCardName).isEmpty()) {
            tilCardName.setError(getString(R.string.checkout_error_card_name));
            valid = false;
        }
        String digits = text(etCardNumber).replaceAll("\\s", "");
        if (!digits.matches("\\d{16}")) {
            tilCardNumber.setError(getString(R.string.checkout_error_card_number));
            valid = false;
        }
        return valid;
    }

    private void confirmOrder() {
        if (!validateForm()) {
            return;
        }

        int customerId = new SessionManager(this).getUserId();
        if (customerId <= 0) {
            Toast.makeText(this, R.string.checkout_error_login, Toast.LENGTH_SHORT).show();
            return;
        }
        if (foods.isEmpty() || total <= 0) {
            return; // summary not loaded yet
        }

        View btn = findViewById(R.id.btnConfirmPay);
        btn.setEnabled(false); // stops double taps creating two orders

        String fulfillment = isDelivery() ? "DELIVERY" : "PICKUP"; // match OrderStatus/teammates' values
        String address = isDelivery() ? text(etAddress) : null;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());

        // Note: card details are only validated here, never saved anywhere.
        executor.execute(() -> {
            List<OrderItem> items = new ArrayList<>();
            for (int i = 0; i < foods.size(); i++) {
                FoodItem food = foods.get(i);
                if (food == null) continue;
                items.add(new OrderItem(0, food.getFoodId(),
                        quantities.get(i), food.getDiscountedPrice()));
            }
            Order order = new Order(customerId, restaurantId, date, fulfillment,
                    OrderStatus.PENDING, total, address);

            try {
                long orderId = AppDatabase.getDatabase(getApplicationContext())
                        .orderTransactionDao().placeOrder(order, items);

                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.checkout_order_placed, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Payment.this, OrderDetails.class);
                    intent.putExtra("order_id", (int) orderId);
                    startActivity(intent);
                    finish();
                });
            } catch (IllegalStateException e) {   // not enough stock, everything rolled back
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.checkout_error_stock, Toast.LENGTH_LONG).show();
                    btn.setEnabled(true);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.checkout_error_generic, Toast.LENGTH_LONG).show();
                    btn.setEnabled(true);
                });
            }
        });
    }
}