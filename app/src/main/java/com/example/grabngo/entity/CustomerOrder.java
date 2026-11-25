//implemented by Ami
package com.example.grabngo.entity;

import androidx.room.Ignore;

import java.util.List;

public class CustomerOrder {

    public int orderId;
    public int customerId;
    public String customerName;
    public String orderDate;
    public String fulfillmentType;
    public String status;
    public double totalAmount;
    public String deliveryAddress;

    @Ignore
    public List<OrderItemDetails> items;
}