package com.example.grabngo.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ORDERS",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "customer_id"
                ),
                @ForeignKey(
                        entity = Restaurant.class,
                        parentColumns = "restaurant_id",
                        childColumns = "restaurant_id"
                )

        }
)
public class Order {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_id")
    private int orderId;

    @ColumnInfo(name = "customer_id")
    private int customerId;

    @ColumnInfo(name = "restaurant_id")
    private int restaurantId;

    @NonNull
    @ColumnInfo(name = "order_date")
    private String orderDate;

    @NonNull
    @ColumnInfo(name = "fulfillment_type")
    private String fulfillmentType;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "total_amount")
    private double totalAmount;

    @ColumnInfo(name = "delivery_address")
    private String deliveryAddress;


    // Default Constructor
    public Order() {
    }


    // Constructor
    public Order(int customerId,
                  int restaurantId,
                  @NonNull String orderDate,
                  @NonNull String fulfillmentType,
                  @NonNull String status,
                  double totalAmount,
                  String deliveryAddress) {

        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.orderDate = orderDate;
        this.fulfillmentType = fulfillmentType;
        this.status = status;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
    }


    // Getters
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public int getRestaurantId() { return restaurantId; }
    @NonNull
    public String getOrderDate() { return orderDate; }
    @NonNull
    public String getFulfillmentType() { return fulfillmentType; }
    @NonNull
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public String getDeliveryAddress() { return deliveryAddress; }


    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setOrderDate(@NonNull String orderDate) {
        this.orderDate = orderDate;
    }

    public void setFulfillmentType(@NonNull String fulfillmentType) {
        this.fulfillmentType = fulfillmentType;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}