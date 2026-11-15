package com.example.grabngo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.entity.Order;

import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    // Lets the screen decide what happens when a card is tapped
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private List<Order> orders;
    private final OnOrderClickListener listener;

    public OrderAdapter(List<Order> orders,
                             OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    // Used later when search results or database data change
    public void setData(List<Order> orders, Map<Integer, Integer> itemCounts) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        Context context = holder.itemView.getContext();

        holder.orderName.setText(context.getString(
                R.string.order_line_name_format,
                order.getOrderId()));
        holder.orderLine1.setText(context.getString(
                R.string.order_line_one_format,
               "John Doe", order.getFulfillmentType()));

        holder.orderLine2.setText(context.getString(
                R.string.order_line_two_format,
                order.getTotalAmount(), order.getStatus()));

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // Holds the views of one row so they are found only once
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final TextView orderName;
        final TextView orderLine1;
        final TextView orderLine2;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.orderName);
            orderLine1 = itemView.findViewById(R.id.orderLine1);
            orderLine2 = itemView.findViewById(R.id.orderLine2);
        }
    }
}