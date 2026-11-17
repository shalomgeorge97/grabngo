
package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.grabngo.entity.Restaurant;
import com.example.grabngo.entity.RestaurantWithItemCount;

import java.util.List;

@Dao
public interface RestaurantDao {

    // Insert a new restaurant
    @Insert
    long addRestaurant(Restaurant restaurant);

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
    //@Query("SELECT * FROM RESTAURANT WHERE city = :city")
    //List<Restaurant> searchRestaurantsByCity(String city);

    @Query("SELECT " +
            "R.restaurant_id AS restaurantId, " +
            "R.restaurant_name AS restaurantName, " +
            "R.address AS address, " +
            "R.city AS city, " +
            "COUNT(F.food_id) AS availableItemCount " +
            "FROM RESTAURANT AS R " +
            "LEFT JOIN FOOD_ITEM AS F " +
            "ON R.restaurant_id = F.restaurant_id " +
            "AND F.available_quantity > 0 " +
            "AND F.available_datetime >= :currentDateTime " +
            "WHERE (:city IS NULL OR R.city LIKE '%' || :city || '%') " +
            "GROUP BY R.restaurant_id, R.restaurant_name, R.address, R.city")
    List<RestaurantWithItemCount> getRestaurantsWithAvailableItemCount(
            String currentDateTime,
            String city
    );
}
