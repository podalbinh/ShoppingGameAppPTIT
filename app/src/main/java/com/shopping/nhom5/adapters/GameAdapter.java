package com.shopping.nhom5.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.shopping.nhom5.R;
import com.shopping.nhom5.adapters.listeners.OnCartBtnClickListener;
import com.shopping.nhom5.adapters.listeners.OnItemClickListener;
import com.shopping.nhom5.models.Game;
import com.squareup.picasso.Picasso;

public class GameAdapter extends FirebaseRecyclerAdapter<Game, GameAdapter.ViewHolder> {

    OnItemClickListener clickListener;
    OnCartBtnClickListener cartClickListener;


    public GameAdapter(@NonNull FirebaseRecyclerOptions<Game> options) {
        super(options);
        Log.i("gameee", options.getSnapshots().toString());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_release_layout, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Game game) {
        holder.bind(game);
        holder.itemView.setOnClickListener(v -> {
            clickListener.onGameClick(game);
        });
        holder.cartBtn.setOnClickListener(v -> cartClickListener.onCartClick(game, ((Button) v), holder.check));
    }

    public void setOnGameClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnCartClickListener(OnCartBtnClickListener cartClickListener) {
        this.cartClickListener = cartClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, price;
        ImageView img;
        Button cartBtn, check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_new_release_title);
            img = itemView.findViewById(R.id.image_view_new_release_img);
            price = itemView.findViewById(R.id.text_view_new_release_price);
            cartBtn = itemView.findViewById(R.id.button_new_release_cart);
            check = itemView.findViewById(R.id.button_new_release_valid);
        }

        public void bind(Game game) {
            Log.i("gameee", game.toString());
            title.setText(game.getTitle());
            String price = "$"+game.getPrice();
            this.price.setText(price);
            CircularProgressDrawable cpd = new CircularProgressDrawable(itemView.getContext());
            cpd.setStrokeWidth(5f);
            cpd.setCenterRadius(30f);
            cpd.start();
            Picasso.get().load(game.getPosterUrl())
                    .placeholder(cpd)
                    .into(img);
        }
    }
}
