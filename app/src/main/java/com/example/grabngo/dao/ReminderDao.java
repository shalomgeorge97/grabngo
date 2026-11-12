package com.example.grabngo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.grabngo.entity.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    // Save a reminder
    @Insert
    long addReminder(Reminder reminder);

    // Get reminders associated with a specific order
    @Query("SELECT * FROM REMINDER WHERE order_id = :orderId")
    List<Reminder> getRemindersByOrderId(int orderId);

    // Mark a reminder as sent
    @Query("UPDATE REMINDER SET is_sent = 1 WHERE reminder_id = :reminderId")
    void markReminderAsSent(int reminderId);
}