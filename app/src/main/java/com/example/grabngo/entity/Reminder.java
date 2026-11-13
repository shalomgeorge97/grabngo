package com.example.grabngo.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "REMINDER",
        foreignKeys = {
                @ForeignKey(
                        entity = Order.class,
                        parentColumns = "order_id",
                        childColumns = "order_id"
                )
        }
)
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reminder_id")
    private int reminderId;

    @ColumnInfo(name = "order_id")
    private int orderId;

    @NonNull
    @ColumnInfo(name = "reminder_type")
    private String reminderType;
    @NonNull
    @ColumnInfo(name = "message")
    private String message;

    @NonNull
    @ColumnInfo(name = "reminder_datetime")
    private String reminderDatetime;

    @ColumnInfo(name = "is_sent")
    private boolean isSent = false;


    // Default Constructor
    public Reminder() {
    }


    // Constructor
    public Reminder(int orderId,
                    @NonNull String reminderType,
                    @NonNull String message,
                    @NonNull String reminderDatetime) {

        this.orderId = orderId;
        this.reminderType = reminderType;
        this.message = message;
        this.reminderDatetime = reminderDatetime;
    }


    // Getters
    public int getReminderId() { return reminderId; }
    public int getOrderId() { return orderId; }

    @NonNull
    public String getReminderType() { return reminderType; }

    @NonNull
    public String getMessage() { return message; }

    @NonNull
    public String getReminderDatetime() { return reminderDatetime; }

    public boolean isSent() { return isSent; }


    // Setters
    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setReminderType(@NonNull String reminderType) {
        this.reminderType = reminderType;
    }
    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    public void setReminderDatetime(@NonNull String reminderDatetime) {
        this.reminderDatetime = reminderDatetime;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }
}
