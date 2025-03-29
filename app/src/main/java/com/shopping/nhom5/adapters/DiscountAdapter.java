package com.shopping.nhom5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.shopping.nhom5.R;
import com.shopping.nhom5.models.Game;
import com.shopping.nhom5.adapters.listeners.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {

    OnItemClickListener clickListener;

    List<Game> games;

    public DiscountAdapter(List<Game> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.bind(game);
        holder.itemView.setOnClickListener(v -> clickListener.onGameClick(game));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }


    public void setOnGameClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, price, discount, newPrice;
        ImageView img;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.text_view_title);
            img = itemView.findViewById(R.id.imgae_view_img);
            price = itemView.findViewById(R.id.text_view_price);
            discount = itemView.findViewById(R.id.text_view_discount_perc);
            newPrice = itemView.findViewById(R.id.text_view_discount_new_price);
        }

        public void bind(Game game) {
            title.setText(game.getTitle());
            price.setText("$"+String.format("%.2f", (Double.parseDouble(game.getPrice()) * (1+Double.parseDouble(game.getDiscount())))));
            this.discount.setText("-"+Double.parseDouble(game.getDiscount())*100+"%");
            newPrice.setText("$"+game.getPrice());
            CircularProgressDrawable cpd = new CircularProgressDrawable(itemView.getContext());
            cpd.setStrokeWidth(5f);
            cpd.setCenterRadius(30f);
            cpd.start();
            Picasso.get().load(game.getThumbnailUrl()).placeholder(cpd).into(img);
        }
    }
}
