package com.example.grabngo.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "ORDER_ITEM",
        primaryKeys = {"order_id", "food_id"},
        foreignKeys = {
                @ForeignKey(
                        entity = Order.class,
                        parentColumns = "order_id",
                        childColumns = "order_id"
                ),
                @ForeignKey(
                        entity = FoodItem.class,
                        parentColumns = "food_id",
                        childColumns = "food_id"
                )

        }
)
public class OrderItem {

    @ColumnInfo(name = "order_id")
    private int orderId;

    @ColumnInfo(name = "food_id")
    private int foodId;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "price")
    private double price;


    // Default Constructor
    public OrderItem() {
    }


    // Constructor
    public OrderItem(int orderId, int foodId,
                     int quantity, double price) {

        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
    }


    // Getters
    public int getOrderId() { return orderId; }
    public int getFoodId() { return foodId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }


    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}