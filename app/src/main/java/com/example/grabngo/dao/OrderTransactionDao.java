//implemented by Ami
package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.grabngo.entity.Order;
import com.example.grabngo.entity.OrderItem;
import com.example.grabngo.util.OrderStatus;

import java.util.List;

@Dao
public interface OrderTransactionDao {

    @Insert
    long addOrder(Order order);

    @Insert
    void addOrderItems(List<OrderItem> items);

    // Decrease stock
    @Query("UPDATE FOOD_ITEM SET available_quantity = available_quantity - :quantity " +
            "WHERE food_id = :foodId AND :quantity > 0 " +
            "AND available_quantity >= :quantity")
    int decreaseAvailableQuantity(int foodId, int quantity);

    // Update order status only if it is pending
    @Query("UPDATE ORDERS SET status = :newStatus " +
            "WHERE order_id = :orderId AND status = '" + OrderStatus.PENDING + "'")
    int updatePendingOrderStatus(int orderId, String newStatus);

    @Query("SELECT * FROM ORDER_ITEM WHERE order_id = :orderId")
    List<OrderItem> getOrderItemsByOrderId(int orderId);

    // Restore stock
    @Query("UPDATE FOOD_ITEM SET available_quantity = available_quantity + :quantity " +
            "WHERE food_id = :foodId AND :quantity > 0")
    int increaseAvailableQuantity(int foodId, int quantity);

    // Place order and decrease stock
    @Transaction
    default long placeOrder(Order order, List<OrderItem> items) {

        long orderId = addOrder(order);

        for (OrderItem item : items) {
            item.setOrderId((int) orderId);
        }

        addOrderItems(items);

        for (OrderItem item : items) {
            int result = decreaseAvailableQuantity(
                    item.getFoodId(),
                    item.getQuantity()
            );

            if (result == 0) {
                throw new IllegalStateException("Not enough food stock.");
            }
        }

        return orderId;
    }

    // Reject order and restore stock
    @Transaction
    default boolean rejectOrder(int orderId) {

        int result = updatePendingOrderStatus(orderId, OrderStatus.REJECTED);

        if (result == 0) {
            return false;
        }

        List<OrderItem> items = getOrderItemsByOrderId(orderId);

        for (OrderItem item : items) {
            int stockResult = increaseAvailableQuantity(
                    item.getFoodId(),
                    item.getQuantity()
            );

            if (stockResult == 0) {
                throw new IllegalStateException("Failed to restore food stock.");
            }
        }

        return true;
    }

    // Cancel order and restore stock
    @Transaction
    default boolean cancelOrder(int orderId) {

        int result = updatePendingOrderStatus(orderId, OrderStatus.CANCELLED);

        if (result == 0) {
            return false;
        }

        List<OrderItem> items = getOrderItemsByOrderId(orderId);

        for (OrderItem item : items) {
            int stockResult = increaseAvailableQuantity(
                    item.getFoodId(),
                    item.getQuantity()
            );

            if (stockResult == 0) {
                throw new IllegalStateException("Failed to restore food stock.");
            }
        }

        return true;
    }
}
