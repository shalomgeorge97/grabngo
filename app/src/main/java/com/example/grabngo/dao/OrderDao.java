package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.grabngo.entity.CustomerOrder;
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

    //Get active orders
    @Query("SELECT * FROM ORDERS " +
            "WHERE customer_id = :customerId " +
            "AND status IN ('PENDING', 'ACCEPTED', 'READY') " +
            "ORDER BY order_id DESC")
    List<Order> getActiveOrdersByCustomerId(int customerId);

    //Get past orders
    @Query("SELECT * FROM ORDERS " +
            "WHERE customer_id = :customerId " +
            "AND status IN ('PICKED_UP', 'DELIVERED', 'REJECTED', 'CANCELLED') " +
            "ORDER BY order_id DESC")
    List<Order> getPastOrdersByCustomerId(int customerId);

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

    // Get customer and order information for the manager's order list and order details.
    @Query("SELECT O.order_id AS orderId, " +
            "O.customer_id AS customerId, " +
            "U.name AS customerName, " +
            "O.order_date AS orderDate, " +
            "O.fulfillment_type AS fulfillmentType, " +
            "O.status AS status, " +
            "O.total_amount AS totalAmount, " +
            "O.delivery_address AS deliveryAddress " +
            "FROM ORDERS AS O " +
            "INNER JOIN USER AS U ON O.customer_id = U.user_id " +
            "WHERE O.restaurant_id = :restaurantId " +
            "ORDER BY O.order_id DESC")
    List<CustomerOrder> getCustomerOrdersByRestaurantId(int restaurantId);

    // Get customer and order information for a specific order
    @Query("SELECT O.order_id AS orderId, " +
            "O.customer_id AS customerId, " +
            "U.name AS customerName, " +
            "O.order_date AS orderDate, " +
            "O.fulfillment_type AS fulfillmentType, " +
            "O.status AS status, " +
            "O.total_amount AS totalAmount, " +
            "O.delivery_address AS deliveryAddress " +
            "FROM ORDERS AS O " +
            "INNER JOIN USER AS U ON O.customer_id = U.user_id " +
            "WHERE O.order_id = :orderId "+
            "AND O.restaurant_id = :restaurantId")
    CustomerOrder getCustomerOrderById(int orderId, int restaurantId);
}
