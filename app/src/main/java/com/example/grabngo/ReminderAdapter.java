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

    public ReminderAdapter(Context context, List<Reminder> reminders,
            ItemClickListener clickListener) {
        list = reminders;
        itemClickListener = clickListener;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_reminder, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((ViewHolder) holder).orderId.setText("Order #" + list.get(position).getOrderId());
        ((ViewHolder) holder).message.setText(list.get(position).getMessage());
        ((ViewHolder) holder).dateTime.setText(list.get(position).getReminderDatetime());
    }

    @Override
    public int getItemCount() {return list.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView message;
        TextView dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.tvReminderOrderId);
            message = itemView.findViewById(R.id.tvReminderMessage);
            dateTime = itemView.findViewById(R.id.tvReminderDateTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener
                            .onItemClick(v, getBindingAdapterPosition());
                }
            });
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}