package com.example.grabngo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.entity.CustomerOrder;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(CustomerOrder order);
    }

    private final List<CustomerOrder> orders = new ArrayList<>();
    private final OnOrderClickListener listener;

    public OrderAdapter(List<CustomerOrder> initialOrders, OnOrderClickListener listener) {
        this.listener = listener;
        setData(initialOrders);
    }

    public void setData(List<CustomerOrder> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
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
        CustomerOrder order = orders.get(position);
        Context context = holder.itemView.getContext();

        holder.orderName.setText(context.getString(
                R.string.order_line_name_format,
                order.orderId));

        String customerLabel = (order.customerName != null && !order.customerName.trim().isEmpty())
                ? order.customerName.trim()
                : "Customer #" + order.customerId;

        holder.orderLine1.setText(context.getString(
                R.string.order_line_one_format,
                customerLabel,
                order.fulfillmentType != null ? order.fulfillmentType : ""));

        holder.orderLine2.setText(context.getString(
                R.string.order_line_two_format,
                order.totalAmount,
                order.status != null ? order.status : ""));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

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