package com.example.grabngo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.entity.FoodItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodItemAdapter
        extends RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder> {

    private List<FoodItem> items;

    // foodId -> quantity the customer has chosen
    private final Map<Integer, Integer> quantities = new HashMap<>();

    public FoodItemAdapter(List<FoodItem> items) {
        this.items = items;
    }

    // Called when the list changes (first load, or after a search)
    public void setItems(List<FoodItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    // Only the foods the customer actually chose (quantity above 0)
    public Map<Integer, Integer> getSelectedQuantities() {
        Map<Integer, Integer> selected = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            if (entry.getValue() > 0) {
                selected.put(entry.getKey(), entry.getValue());
            }
        }
        return selected;
    }

    private int getQuantity(int foodId) {
        Integer stored = quantities.get(foodId);
        return (stored == null) ? 0 : stored;
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
        FoodItem food = items.get(position);
        Context context = holder.itemView.getContext();

        holder.tvFoodName.setText(food.getName());

        // Original price with a line through it
        holder.tvOriginalPrice.setText(
                context.getString(R.string.price_format, food.getOriginalPrice()));
        holder.tvOriginalPrice.setPaintFlags(
                holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.tvDiscountedPrice.setText(
                context.getString(R.string.price_format, food.getDiscountedPrice()));
        // Quantity left = stock minus what the customer has already chosen
        int selected = getQuantity(food.getFoodId());
        holder.tvQtyLeft.setText(context.getString(
                R.string.details_qty_left, food.getAvailableQuantity() - selected));

        holder.tvQuantity.setText(String.valueOf(selected));

        // + : cannot go above the quantity left
        holder.btnPlus.setOnClickListener(v -> {
            int current = getQuantity(food.getFoodId());
            if (current < food.getAvailableQuantity()) {
                quantities.put(food.getFoodId(), current + 1);
                notifyItemChanged(holder.getBindingAdapterPosition());
            }
        });

        // - : cannot go below 0
        holder.btnMinus.setOnClickListener(v -> {
            int current = getQuantity(food.getFoodId());
            if (current > 0) {
                quantities.put(food.getFoodId(), current - 1);
                notifyItemChanged(holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Forgets all selected quantities (used when returning to the screen)
    public void clearSelection() {
        quantities.clear();
        notifyDataSetChanged();
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