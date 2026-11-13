package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.grabngo.entity.Order;

import java.util.List;

@Dao
public interface OrderDao {

    // Create a new order
    @Insert
    long addOrder(Order order);

    // Update an existing order
    @Update
    void updateOrder(Order order);

    // Get all orders placed by a specific customer
    @Query("SELECT * FROM ORDERS WHERE customer_id = :customerId ORDER BY order_id DESC")
    List<Order> getOrdersByCustomerId(int customerId);

    // Get all orders received by a specific restaurant
    @Query("SELECT * FROM ORDERS WHERE restaurant_id = :restaurantId ORDER BY order_id DESC")
    List<Order> getOrdersByRestaurantId(int restaurantId);

    // Get a specific order by ID
    @Query("SELECT * FROM ORDERS WHERE order_id = :orderId")
    Order getOrderById(int orderId);

    // Get orders by status for a specific restaurant
    @Query("SELECT * FROM ORDERS WHERE restaurant_id = :restaurantId AND status = :status")
    List<Order> getOrdersByStatus(int restaurantId, String status);

    @Query("UPDATE `ORDERS` SET status = :status WHERE order_id = :orderId")
    int updateOrderStatus(int orderId, String status);
}
