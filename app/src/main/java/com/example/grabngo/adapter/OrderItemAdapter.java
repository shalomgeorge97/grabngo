package com.example.grabngo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.grabngo.R;
import com.example.grabngo.model.OrderLine;
import java.util.List;
import java.util.Locale;

/**
 * Shared by both the manager and customer order details screens, and anywhere else
 * an order's items are listed (checkout, order history detail).
 */
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.LineHolder> {

    private final List<OrderLine> lines;

    public OrderItemAdapter(List<OrderLine> lines) {
        this.lines = lines;
    }

    @NonNull
    @Override
    public LineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_line, parent, false);
        return new LineHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LineHolder holder, int position) {
        OrderLine line = lines.get(position);
        holder.tvLineName.setText(
                String.format(Locale.getDefault(), "%s x %d", line.getFoodName(), line.getQuantity()));
        holder.tvLinePrice.setText(String.format(Locale.getDefault(), "$%.2f", line.getLineTotal()));
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    static class LineHolder extends RecyclerView.ViewHolder {
        final TextView tvLineName;
        final TextView tvLinePrice;

        LineHolder(@NonNull View itemView) {
            super(itemView);
            tvLineName = itemView.findViewById(R.id.tvLineName);
            tvLinePrice = itemView.findViewById(R.id.tvLinePrice);
        }
    }
}