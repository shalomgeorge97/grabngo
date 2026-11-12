
package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.grabngo.entity.Restaurant;

import java.util.List;

@Dao
public interface RestaurantDao {


    // Get all restaurants
    @Query("SELECT * FROM RESTAURANT")
    List<Restaurant> getAllRestaurants();

    // Get a restaurant by ID
    @Query("SELECT * FROM RESTAURANT WHERE restaurant_id = :restaurantId")
    Restaurant getRestaurantById(int restaurantId);

    // Get the restaurant assigned to a specific manager
    @Query("SELECT * FROM RESTAURANT WHERE manager_id = :managerId")
    Restaurant getRestaurantByManagerId(int managerId);

    // Search restaurants by city
    @Query("SELECT * FROM RESTAURANT WHERE city = :city")
    List<Restaurant> searchRestaurantsByCity(String city);
}
