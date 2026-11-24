package com.example.grabngo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grabngo.database.AppDatabase;
import com.example.grabngo.dao.FoodItemDao;
import com.example.grabngo.entity.FoodItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditFood extends AppCompatActivity {

    // Database
    private FoodItemDao foodItemDao;
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    // Form fields
    private TextInputEditText etName, etDescription,
            etOriginalPrice, etDiscountedPrice,
            etQuantity, etIngredients;

    private AutoCompleteTextView actCategory;

    private TextInputLayout tilName, tilCategory,
            tilOriginalPrice, tilDiscountedPrice, tilQuantity;

    private TextView titleDisplay;
    private MaterialButton btnSave;

    // Food information
    private int foodId = -1;
    private int restaurantId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_food);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        // Database initialization
        foodItemDao = AppDatabase.getDatabase(this).foodItemDao();

        // Get restaurant ID and food ID
        restaurantId = getIntent().getIntExtra("restaurantId", -1);
        foodId = getIntent().getIntExtra("foodId", -1);

        isEditMode = foodId != -1;

        initializeViews();
        setupCategoryDropdown();
        setupScreen();

        btnSave.setOnClickListener(v -> saveFoodItem());
    }

    // Initialize views
    private void initializeViews() {

        titleDisplay = findViewById(R.id.tvTitleDisplay);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etOriginalPrice = findViewById(R.id.etOriginalPrice);
        etDiscountedPrice = findViewById(R.id.etDiscountedPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etIngredients = findViewById(R.id.etIngredients);

        actCategory = findViewById(R.id.actCategory);

        tilName = findViewById(R.id.tilName);
        tilCategory = findViewById(R.id.tilCategory);
        tilOriginalPrice = findViewById(R.id.tilOriginalPrice);
        tilDiscountedPrice = findViewById(R.id.tilDiscountedPrice);
        tilQuantity = findViewById(R.id.tilQuantity);

        btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // Setup category dropdown
    private void setupCategoryDropdown() {

        String[] categories = {
                "Bakery",
                "Meals",
                "Desserts",
                "Beverages",
                "Snacks",
                "Others"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );

        actCategory.setAdapter(adapter);
    }

    // Configure Add/Edit screen
    private void setupScreen() {

        if (isEditMode) {

            titleDisplay.setText(R.string.food_edit_title);
            btnSave.setText(R.string.food_edit_update);

            loadFoodItem();

        } else {

            titleDisplay.setText(R.string.food_add_title);
            btnSave.setText(R.string.food_add_save);

            if (restaurantId == -1) {
                Toast.makeText(this,
                        "Restaurant not found",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Load existing food item
    private void loadFoodItem() {

        executor.execute(() -> {

            FoodItem food = foodItemDao.getFoodItemById(foodId);

            runOnUiThread(() -> {

                if (food == null ||
                        food.getRestaurantId() != restaurantId) {

                    Toast.makeText(this,
                            "Food item not found",
                            Toast.LENGTH_SHORT).show();

                    finish();
                    return;
                }

                etName.setText(food.getName());
                etDescription.setText(food.getDescription());
                actCategory.setText(food.getCategory(), false);

                etOriginalPrice.setText(
                        String.valueOf(food.getOriginalPrice()));

                etDiscountedPrice.setText(
                        String.valueOf(food.getDiscountedPrice()));

                etQuantity.setText(
                        String.valueOf(food.getAvailableQuantity()));

                etIngredients.setText(
                        food.getIngredientsAllergens());
            });
        });
    }

    // Validate form
    private boolean validateForm() {

        clearErrors();

        String name = getText(etName);
        String category = getText(actCategory);
        String original = getText(etOriginalPrice);
        String discounted = getText(etDiscountedPrice);
        String quantity = getText(etQuantity);

        boolean valid = true;

        if (name.isEmpty()) {
            tilName.setError("Food name is required");
            valid = false;
        }

        if (category.isEmpty()) {
            tilCategory.setError("Select a category");
            valid = false;
        }

        if (original.isEmpty()) {
            tilOriginalPrice.setError("Enter original price");
            valid = false;
        }

        if (discounted.isEmpty()) {
            tilDiscountedPrice.setError("Enter discounted price");
            valid = false;
        }

        if (quantity.isEmpty()) {
            tilQuantity.setError("Enter available quantity");
            valid = false;
        }

        if (!valid) return false;

        try {

            double originalPrice = Double.parseDouble(original);
            double discountedPrice = Double.parseDouble(discounted);
            int availableQuantity = Integer.parseInt(quantity);

            if (!Double.isFinite(originalPrice) ||
                    !Double.isFinite(discountedPrice) ||
                    originalPrice <= 0 ||
                    discountedPrice <= 0) {

                tilOriginalPrice.setError("Prices must be positive");
                tilDiscountedPrice.setError("Prices must be positive");
                return false;
            }

            if (discountedPrice > originalPrice) {
                tilDiscountedPrice.setError(
                        "Discounted price cannot exceed original price");
                return false;
            }

            if (availableQuantity <= 0) {
                tilQuantity.setError(
                        "Quantity must be greater than zero");
                return false;
            }

        } catch (NumberFormatException e) {

            Toast.makeText(this,
                    "Enter valid prices and quantity",
                    Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

    // Clear validation errors
    private void clearErrors() {

        tilName.setError(null);
        tilCategory.setError(null);
        tilOriginalPrice.setError(null);
        tilDiscountedPrice.setError(null);
        tilQuantity.setError(null);
    }

    // Get text safely
    private String getText(TextInputEditText editText) {
        return editText.getText() == null
                ? "" : editText.getText().toString().trim();
    }

    private String getText(AutoCompleteTextView editText) {
        return editText.getText().toString().trim();
    }

    // Return to FoodList screen
    private void goToFoodList() {
        Intent intent = new Intent(AddEditFood.this, FoodList.class);
        intent.putExtra("restaurantId", restaurantId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // Save or update food item
    private void saveFoodItem() {

        if (!validateForm()) return;

        String name = getText(etName);
        String description = getText(etDescription);
        String category = getText(actCategory);
        String ingredients = getText(etIngredients);

        double originalPrice = Double.parseDouble(
                getText(etOriginalPrice));

        double discountedPrice = Double.parseDouble(
                getText(etDiscountedPrice));

        int quantity = Integer.parseInt(
                getText(etQuantity));

        if (!isEditMode) {

            // Add new food item
            FoodItem food;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                food = new FoodItem(
                        restaurantId,
                        name,
                        description,
                        category,
                        ingredients,
                        originalPrice,
                        discountedPrice,
                        quantity,
                        LocalDateTime.now()
                                .format(DateTimeFormatter
                                        .ofPattern("yyyy-MM-dd HH:mm"))
                );
            } else {
                food = null;
            }

            executor.execute(() -> {

                foodItemDao.addFoodItem(food);

                runOnUiThread(() -> {

                    Toast.makeText(this,
                            "Food added successfully",
                            Toast.LENGTH_SHORT).show();

                    goToFoodList();
                });
            });

        } else {

            // Update existing food item
            executor.execute(() -> {

                FoodItem food = foodItemDao.getFoodItemById(foodId);

                if (food == null ||
                        food.getRestaurantId() != restaurantId) {

                    runOnUiThread(() -> Toast.makeText(this,
                            "Food item not found",
                            Toast.LENGTH_SHORT).show());

                    return;
                }

                food.setName(name);
                food.setDescription(description);
                food.setCategory(category);
                food.setIngredientsAllergens(ingredients);
                food.setOriginalPrice(originalPrice);
                food.setDiscountedPrice(discountedPrice);
                food.setAvailableQuantity(quantity);

                foodItemDao.updateFoodItem(food);

                runOnUiThread(() -> {

                    Toast.makeText(this,
                            "Food updated successfully",
                            Toast.LENGTH_SHORT).show();

                    goToFoodList();
                });
            });
        }
    }

    // Confirm before deleting
    private void confirmDelete() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Food")
                .setMessage("Are you sure you want to delete this food item?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) ->
                        deleteFoodItem())
                .show();
    }

    // Delete food item
    private void deleteFoodItem() {

        executor.execute(() -> {

            FoodItem food = foodItemDao.getFoodItemById(foodId);

            if (food == null ||
                    food.getRestaurantId() != restaurantId) {

                runOnUiThread(() -> Toast.makeText(this,
                        "Food item not found",
                        Toast.LENGTH_SHORT).show());

                return;
            }

            foodItemDao.deleteFoodItem(food);

            runOnUiThread(() -> {

                Toast.makeText(this,
                        "Food deleted successfully",
                        Toast.LENGTH_SHORT).show();

                goToFoodList();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}