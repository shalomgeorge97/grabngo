//implemented by Ami
package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.grabngo.entity.CustomerOrder;
import com.example.grabngo.entity.Order;
import com.example.grabngo.util.OrderStatus;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    long addOrder(Order order);

    @Query("SELECT * FROM ORDERS " +
            "WHERE customer_id = :customerId " +
            "AND status IN ('" + OrderStatus.PENDING + "', '" + OrderStatus.ACCEPTED +
            "', '" + OrderStatus.READY + "') " +
            "ORDER BY order_id DESC")
    List<Order> getActiveOrdersByCustomerId(int customerId);

    @Query("SELECT * FROM ORDERS " +
            "WHERE customer_id = :customerId " +
            "AND status IN ('" + OrderStatus.PICKED_UP + "', '" + OrderStatus.DELIVERED +
            "', '" + OrderStatus.REJECTED + "', '" + OrderStatus.CANCELLED + "') " +
            "ORDER BY order_id DESC")
    List<Order> getPastOrdersByCustomerId(int customerId);

    @Query("SELECT * FROM ORDERS WHERE order_id = :orderId")
    Order getOrderById(int orderId);

    @Query("UPDATE ORDERS SET status = :status WHERE order_id = :orderId")
    int updateOrderStatus(int orderId, String status);

    // Get customer info and order information.
    // For the manager's order list and order details screen.
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

}
