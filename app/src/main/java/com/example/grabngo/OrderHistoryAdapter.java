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

public class OrderHistoryAdapter
        extends RecyclerView.Adapter<OrderHistoryAdapter.HistoryViewHolder> {

    // Lets the screen decide what happens when an order is tapped
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private List<Order> orders;
    private Map<Integer, String> restaurantNames; // restaurantId -> name
    private final OnOrderClickListener listener;

    public OrderHistoryAdapter(List<Order> orders,
                               Map<Integer, String> restaurantNames,
                               OnOrderClickListener listener) {
        this.orders = orders;
        this.restaurantNames = restaurantNames;
        this.listener = listener;
    }

    public void setData(List<Order> orders, Map<Integer, String> restaurantNames) {
        this.orders = orders;
        this.restaurantNames = restaurantNames;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reuses the shared order card layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Order order = orders.get(position);
        Context context = holder.itemView.getContext();

        String name = restaurantNames.get(order.getRestaurantId());
        if (name == null) {
            name = context.getString(R.string.history_unknown_restaurant);
        }

        holder.orderName.setText(name);
        holder.orderLine1.setText(context.getString(
                R.string.history_line_one_format,
                order.getOrderId(), order.getOrderDate()));
        holder.orderLine2.setText(context.getString(
                R.string.history_line_two_format,
                order.getTotalAmount(), order.getStatus()));

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        final TextView orderName;
        final TextView orderLine1;
        final TextView orderLine2;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.orderName);
            orderLine1 = itemView.findViewById(R.id.orderLine1);
            orderLine2 = itemView.findViewById(R.id.orderLine2);
        }
    }
}