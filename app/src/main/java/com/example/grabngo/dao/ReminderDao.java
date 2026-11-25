//implemented by Ami
package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.grabngo.entity.Reminder;
import com.example.grabngo.util.OrderStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    long addReminder(Reminder reminder);

    // Create and save reminder with a message.
    default void createStatusReminder(int orderId, String status) {
        String message;
        switch (status) {
            case OrderStatus.ACCEPTED:
                message = "Your order has been accepted.";
                break;
            case OrderStatus.REJECTED:
                message = "Sorry, your order has been rejected.";
                break;
            case OrderStatus.READY:
                message = "Your order is ready.";
                break;
            case OrderStatus.PICKED_UP:
                message = "Your order has been picked up.";
                break;
            case OrderStatus.DELIVERED:
                message = "Your order has been delivered.";
                break;
            case OrderStatus.CANCELLED:
                message = "Your order has been cancelled.";
                break;
            default:
                message = "Your order status changed to " + status + ".";
                break;
        }

        String dateTime = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss").format(new Date());
        addReminder(new Reminder(orderId, status, message, dateTime));
    }

    // Show only the newest reminder for each order
    @Query("SELECT R.* FROM REMINDER AS R " +
            "INNER JOIN (SELECT order_id, MAX(reminder_id) AS latest_id " +
            "FROM REMINDER GROUP BY order_id) AS L ON R.reminder_id = L.latest_id " +
            "INNER JOIN ORDERS AS O ON O.order_id = R.order_id " +
            "WHERE O.customer_id = :customerId " +
            "ORDER BY R.reminder_id DESC")
    List<Reminder> getLatestRemindersByCustomerId(int customerId);

    // Check whether any newest reminder is unread.
    // one per order for switching bell icon.
    @Query("SELECT EXISTS(SELECT 1 FROM REMINDER AS R " +
            "INNER JOIN (SELECT order_id, MAX(reminder_id) AS latest_id " +
            "FROM REMINDER GROUP BY order_id) AS L ON R.reminder_id = L.latest_id " +
            "INNER JOIN ORDERS AS O ON O.order_id = R.order_id " +
            "WHERE O.customer_id = :customerId AND R.is_read = 0)")
    boolean hasUnreadLatestReminder(int customerId);

    // When the cutomer opens the reminders screen, marks this customer's reminders as read.
    @Query("UPDATE REMINDER SET is_read = 1 " +
            "WHERE order_id IN (SELECT order_id FROM ORDERS WHERE customer_id = :customerId)")
    void markCustomerRemindersAsRead(int customerId);

}
