package com.example.grabngo.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "FOOD_ITEM",
        foreignKeys = {
                @ForeignKey(
                        entity = Restaurant.class,
                        parentColumns = "restaurant_id",
                        childColumns = "restaurant_id"
                )

        }
)
public class FoodItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "food_id")
    private int foodId;

    @ColumnInfo(name = "restaurant_id")
    private int restaurantId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "ingredients_allergens")
    private String ingredientsAllergens;

    @ColumnInfo(name = "original_price")
    private double originalPrice;

    @ColumnInfo(name = "discounted_price")
    private double discountedPrice;

    @ColumnInfo(name = "available_quantity")
    private int availableQuantity;

    @NonNull
    @ColumnInfo(name = "available_datetime")
    private String availableDatetime;

    // Default Constructor
    public FoodItem() {
    }


    // Constructor
    public FoodItem(int restaurantId,
                    @NonNull String name,
                    String description,
                    @NonNull String category,
                    String ingredientsAllergens,
                    double originalPrice,
                    double discountedPrice,
                    int availableQuantity,
                    @NonNull String availableDatetime
                    ) {

        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.ingredientsAllergens = ingredientsAllergens;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.availableQuantity = availableQuantity;
        this.availableDatetime = availableDatetime;
    }


    // Getters
    public int getFoodId() { return foodId; }
    public int getRestaurantId() { return restaurantId; }
    @NonNull
    public String getName() { return name; }
    public String getDescription() { return description; }
    @NonNull
    public String getCategory() { return category; }
    public String getIngredientsAllergens() { return ingredientsAllergens; }
    public double getOriginalPrice() { return originalPrice; }
    public double getDiscountedPrice() { return discountedPrice; }
    public int getAvailableQuantity() { return availableQuantity; }
    @NonNull
    public String getAvailableDatetime() { return availableDatetime; }



    // Setters
    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    public void setIngredientsAllergens(String ingredientsAllergens) {
        this.ingredientsAllergens = ingredientsAllergens;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public void setAvailableDatetime(@NonNull String availableDatetime) {
        this.availableDatetime = availableDatetime;
    }

}