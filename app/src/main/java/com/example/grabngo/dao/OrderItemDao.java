package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.grabngo.entity.OrderItem;

import java.util.List;

@Dao
public interface OrderItemDao {

    // Add food items to an order
    @Insert
    void addOrderItems(List<OrderItem> orderItems);

    // Update an existing order item
    @Update
    void updateOrderItem(OrderItem orderItem);

    // Delete an order item
    @Delete
    void deleteOrderItem(OrderItem orderItem);

    // Get all food items included in a specific order
    @Query("SELECT * FROM ORDER_ITEM WHERE order_id = :orderId")
    List<OrderItem> getOrderItemsByOrderId(int orderId);

}
