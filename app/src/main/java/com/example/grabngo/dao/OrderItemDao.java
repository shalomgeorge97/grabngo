//implemented by Ami
package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.grabngo.entity.OrderItem;
import com.example.grabngo.entity.OrderItemDetails;

import java.util.List;

@Dao
public interface OrderItemDao {

    // Add food items to an order
    @Insert
    void addOrderItems(List<OrderItem> orderItems);

    // Get food item details within a specific order. For Order Details screen
    @Query("SELECT OI.food_id AS foodId, " +
            "FI.name AS foodName, " +
            "OI.quantity AS quantity, " +
            "OI.price AS price " +
            "FROM ORDER_ITEM AS OI " +
            "INNER JOIN FOOD_ITEM AS FI ON OI.food_id = FI.food_id " +
            "WHERE OI.order_id = :orderId")
    List<OrderItemDetails> getOrderItemDetails(int orderId);

}
