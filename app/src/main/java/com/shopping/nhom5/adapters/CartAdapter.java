package com.shopping.nhom5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shopping.nhom5.adapters.listeners.OnCartClickListener;
import com.shopping.nhom5.adapters.listeners.OnMinusListener;
import com.shopping.nhom5.adapters.listeners.OnPlusListener;
import com.shopping.nhom5.adapters.listeners.OnTrashClickListener;
import com.shopping.nhom5.models.CartItem;
import com.shopping.nhom5.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<CartItem> cartItems;
    OnTrashClickListener trashClickListener;
    OnPlusListener onPlus;
    OnMinusListener onMinus;
    OnCartClickListener listener;

    public CartAdapter(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.trash.setOnClickListener(v -> trashClickListener.onTrashClick(cartItem.getId()));
        holder.plus.setOnClickListener(v -> onPlus.onPlus(cartItem.getId()));
        holder.minus.setOnClickListener(v -> onMinus.onMinus(cartItem.getId()));
        holder.itemView.setOnClickListener(v -> listener.onCartClick(cartItem.getId()));
        holder.bind(cartItem);
    }

    public void onCartClickListener(OnCartClickListener listener) {
        this.listener = listener;
    }

    public void onTrashClickListener(OnTrashClickListener trashClickListener) {
        this.trashClickListener = trashClickListener;
    }

    public void onPlusListener(OnPlusListener onPlus) {
        this.onPlus = onPlus;
    }

    public void onMinusListener(OnMinusListener onMinus) {
        this.onMinus = onMinus;
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, story, price;
        ImageView poster, plus, minus, trash;
        EditText amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cart_tv_title);
            story = itemView.findViewById(R.id.cart_tv_story);
            price = itemView.findViewById(R.id.cart_tv_price);
            poster = itemView.findViewById(R.id.cart_iv_poster);
            plus = itemView.findViewById(R.id.cart_iv_plus);
            minus = itemView.findViewById(R.id.cart_iv_minus);
            trash = itemView.findViewById(R.id.cart_iv_trash);
            amount = itemView.findViewById(R.id.cart_et_amount);
        }

        public void bind(CartItem cartItem) {
            title.setText(cartItem.getTitle());
            story.setText(cartItem.getStory());
            String priceStr = "$"+ cartItem.getPrice();
            price.setText(priceStr);
            Picasso.get().load(cartItem.getImg()).into(poster);
            amount.setText(""+ cartItem.getAmount());
        }
    }
}