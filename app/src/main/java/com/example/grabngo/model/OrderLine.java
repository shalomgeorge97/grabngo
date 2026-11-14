package com.example.grabngo.model;

/** One line of an order, for display in the order details screen. */
public class OrderLine {
    private final String foodName;
    private final int quantity;
    private final double lineTotal;

    public OrderLine(String foodName, int quantity, double lineTotal) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public String getFoodName() { return foodName; }
    public int getQuantity() { return quantity; }
    public double getLineTotal() { return lineTotal; }
}
