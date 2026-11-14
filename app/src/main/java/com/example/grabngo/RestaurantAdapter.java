package com.example.grabngo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.entity.Restaurant;

import java.util.List;
import java.util.Map;

public class RestaurantAdapter
        extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    // Lets the screen decide what happens when a card is tapped
    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    private List<Restaurant> restaurants;
    private Map<Integer, Integer> itemCounts; // restaurantId -> number of food items
    private final OnRestaurantClickListener listener;

    public RestaurantAdapter(List<Restaurant> restaurants,
                             Map<Integer, Integer> itemCounts,
                             OnRestaurantClickListener listener) {
        this.restaurants = restaurants;
        this.itemCounts = itemCounts;
        this.listener = listener;
    }

    // Used later when search results or database data change
    public void setData(List<Restaurant> restaurants, Map<Integer, Integer> itemCounts) {
        this.restaurants = restaurants;
        this.itemCounts = itemCounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        Context context = holder.itemView.getContext();

        holder.tvName.setText(restaurant.getRestaurantName());
        holder.tvAddress.setText(context.getString(
                R.string.restaurant_address_format,
                restaurant.getAddress(), restaurant.getCity()));

        Integer stored = itemCounts.get(restaurant.getRestaurantId());
        int count = (stored == null) ? 0 : stored;
        holder.tvItems.setText(context.getResources()
                .getQuantityString(R.plurals.items_available, count, count));

        holder.itemView.setOnClickListener(v -> listener.onRestaurantClick(restaurant));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    // Holds the views of one row so they are found only once
    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvAddress;
        final TextView tvItems;

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvItems = itemView.findViewById(R.id.tvItems);
        }
    }
}