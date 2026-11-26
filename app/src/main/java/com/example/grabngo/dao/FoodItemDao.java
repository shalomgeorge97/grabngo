//implemented by Ami
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

    @Insert
    void addFoodItem(FoodItem foodItem);

    @Update
    void updateFoodItem(FoodItem foodItem);

    @Delete
    void deleteFoodItem(FoodItem foodItem);

    // Get food items from specific restaurant. For manager's food list.
    @Query("SELECT * FROM FOOD_ITEM WHERE restaurant_id = :restaurantId")
    List<FoodItem> getFoodItemsByRestaurantId(int restaurantId);

    // Get food item by food ID.
    @Query("SELECT * FROM FOOD_ITEM WHERE food_id = :foodId")
    FoodItem getFoodItemById(int foodId);

    // Search food items by name within specific restaurant
    @Query("SELECT * FROM FOOD_ITEM WHERE restaurant_id = :restaurantId AND name LIKE '%' || :keyword || '%'")
    List<FoodItem> searchFoodItemsByName(int restaurantId, String keyword);

}