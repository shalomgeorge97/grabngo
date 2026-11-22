package com.example.grabngo.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.R;
import com.example.grabngo.entity.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewHolder> {

    public interface OnFoodActionListener {
        void onFoodClick(@NonNull FoodItem item);
        void onQuantityChange(@NonNull FoodItem item, int newQuantity);
    }

    private final List<FoodItem> items = new ArrayList<>();
    private final OnFoodActionListener listener;

    public FoodListAdapter(@NonNull OnFoodActionListener listener) {
        this.listener = listener;
    }

    public void setItems(@NonNull List<FoodItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.tvFoodName.setText(item.getName());

        holder.tvOriginalPrice.setText(
                context.getString(R.string.price_format, item.getOriginalPrice()));
        holder.tvOriginalPrice.setPaintFlags(
                holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.tvDiscountedPrice.setText(
                context.getString(R.string.price_format, item.getDiscountedPrice()));

        int qty = item.getAvailableQuantity();
        holder.tvQtyLeft.setText(context.getString(R.string.details_qty_left, qty));
        holder.tvQuantity.setText(String.valueOf(qty));

        holder.itemView.setOnClickListener(v -> listener.onFoodClick(item));

        holder.btnMinus.setOnClickListener(v -> {
            if (qty > 0) {
                listener.onQuantityChange(item, qty - 1);
            }
        });

        holder.btnPlus.setOnClickListener(v ->
                listener.onQuantityChange(item, qty + 1));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        final TextView tvFoodName;
        final TextView tvOriginalPrice;
        final TextView tvDiscountedPrice;
        final TextView tvQtyLeft;
        final TextView tvQuantity;
        final ImageView btnMinus;
        final ImageView btnPlus;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountedPrice = itemView.findViewById(R.id.tvDiscountedPrice);
            tvQtyLeft = itemView.findViewById(R.id.tvQtyLeft);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}