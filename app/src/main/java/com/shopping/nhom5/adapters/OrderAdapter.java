package com.shopping.nhom5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderAdapter extends FirebaseRecyclerAdapter<Order, OrderAdapter.ViewHolder> {

    public OrderAdapter(@NonNull FirebaseRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Order model) {
        holder.bindView(model);
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnLongItemClickListener != null) {
                mOnLongItemClickListener.itemLongClicked(v, model);
            }

            return true;
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ref, totalPrice, orderDate;
        ImageView statusImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ref = itemView.findViewById(R.id.order_ref);
            totalPrice = itemView.findViewById(R.id.order_total_price);
            orderDate = itemView.findViewById(R.id.order_date);
            statusImg = itemView.findViewById(R.id.order_img_status);
        }

        public void bindView(Order order) {
            ref.setText("Order: " + getFourDigitNumber(order.getCreatedAt()));
            totalPrice.setText("Total pirce: " + order.getTotalPrice());
            orderDate.setText("Ordered at: " + formatedDate(order.getCreatedAt()));
            switch (order.getStatus()) {
                case Order.PENDING:
                    statusImg.setImageResource(R.drawable.s_pending);
                    break;
                case Order.COMPLETED:
                    statusImg.setImageResource(R.drawable.s_completed);
                    break;
                default:
                    statusImg.setImageResource(R.drawable.s_canceled);
            }
        }

        private String formatedDate(Long time) {
            Date date = new Date(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return simpleDateFormat.format(date);
        }
    }

    OnLongItemClickListener mOnLongItemClickListener;

    public void setOnLongItemClickListener(OnLongItemClickListener onLongItemClickListener) {
        mOnLongItemClickListener = onLongItemClickListener;
    }

    public interface OnLongItemClickListener {
        void itemLongClicked(View v, Order order);
    }

    public static int getFourDigitNumber(long dateMs) {
        if (dateMs <= 0) {
            throw new IllegalArgumentException("Invalid date in milliseconds");
        }

        // Simple hash function to compress the milliseconds
        long hash = Math.abs(
                Long.toString(dateMs)
                        .chars()
                        .reduce(0, (acc, charCode) -> acc * 31 + charCode)
        );

        // Ensure the number is 4 digits
        int uniqueNumber = (int) (hash % 9000) + 1000; // Force range to 1000-9999
        return uniqueNumber;
    }
}
