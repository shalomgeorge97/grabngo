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

}