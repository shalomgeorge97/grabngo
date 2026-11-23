//implemented Ami
package com.example.grabngo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabngo.entity.Reminder;

import java.util.List;

public class ReminderAdapter
        extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private final List<Reminder> list;
    private final LayoutInflater layoutInflater;
    private final ItemClickListener itemClickListener;

    public ReminderAdapter(
            Context context,
            List<Reminder> reminders,
            ItemClickListener clickListener) {
        list = reminders;
        itemClickListener = clickListener;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(
                R.layout.item_reminder,
                parent,
                false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = list.get(position);

        holder.orderId.setText("Order #" + reminder.getOrderId());
        holder.message.setText(reminder.getMessage());
        holder.dateTime.setText(reminder.getReminderDatetime());
        holder.itemView.setOnClickListener(v ->
                itemClickListener.onItemClick(
                        v,
                        holder.getBindingAdapterPosition()
                ));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView message;
        TextView dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.tvReminderOrderId);
            message = itemView.findViewById(R.id.tvReminderMessage);
            dateTime = itemView.findViewById(R.id.tvReminderDateTime);
        }
    }
}
