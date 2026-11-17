package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.grabngo.entity.FoodItem;

import java.util.List;

@Dao
public interface FoodItemDao {

    // Add a new food item
    @Insert
    void addFoodItem(FoodItem foodItem);


    // Update an existing food item
    @Update
    void updateFoodItem(FoodItem foodItem);

    // Delete a food item
    @Delete
    void deleteFoodItem(FoodItem foodItem);

    // Get all food items from a specific restaurant
    @Query("SELECT * FROM FOOD_ITEM WHERE restaurant_id = :restaurantId")
    List<FoodItem> getFoodItemsByRestaurantId(int restaurantId);

    // Get a food item by ID
    @Query("SELECT * FROM FOOD_ITEM WHERE food_id = :foodId")
    FoodItem getFoodItemById(int foodId);

    // Search food items by name within a specific restaurant
    @Query("SELECT * FROM FOOD_ITEM WHERE restaurant_id = :restaurantId AND name LIKE '%' || :keyword || '%'")
    List<FoodItem> searchFoodItemsByName(int restaurantId, String keyword);

    @Query("UPDATE FOOD_ITEM SET available_quantity = available_quantity - :quantity WHERE food_id = :foodId AND :quantity > 0 AND available_quantity >= :quantity")
    int decreaseAvailableQuantity(int foodId, int quantity);

    @Query("UPDATE FOOD_ITEM SET available_quantity = available_quantity + :quantity WHERE food_id = :foodId AND :quantity > 0")
    int increaseAvailableQuantity(int foodId, int quantity);

    @Query("SELECT * FROM FOOD_ITEM " +
            "WHERE restaurant_id = :restaurantId " +
            "AND available_quantity > 0 " +
            "AND available_datetime >= :currentDateTime " +
            "AND (:keyword IS NULL OR name LIKE '%' || :keyword || '%') " +
            "AND (:category IS NULL OR category = :category) " +
            "AND (:minPrice IS NULL OR discounted_price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR discounted_price <= :maxPrice)")
    List<FoodItem> getAvailableFoodItems(
            int restaurantId,
            String currentDateTime,
            String keyword,
            String category,
            Double minPrice,
            Double maxPrice
    );
}