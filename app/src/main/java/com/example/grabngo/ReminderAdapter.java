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

public class ReminderAdapter extends RecyclerView.Adapter {

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
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {
        View view = layoutInflater.inflate(
                R.layout.item_reminder,
                parent,
                false
        );
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position) {
        ViewHolder reminderHolder = (ViewHolder) holder;
        Reminder reminder = list.get(position);

        reminderHolder.orderId.setText("Order #" + reminder.getOrderId());
        reminderHolder.message.setText(reminder.getMessage());
        reminderHolder.dateTime.setText(reminder.getReminderDatetime());
        reminderHolder.itemView.setOnClickListener(v ->
                itemClickListener.onItemClick(
                        v,
                        reminderHolder.getBindingAdapterPosition()
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