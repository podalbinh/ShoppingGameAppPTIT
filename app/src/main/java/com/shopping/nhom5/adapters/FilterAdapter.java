package com.shopping.nhom5.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.shopping.nhom5.R;
import com.shopping.nhom5.adapters.listeners.OnItemClickListener;
import com.shopping.nhom5.models.Game;
import com.shopping.nhom5.models.Genre;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    List<Game> games;
    OnItemClickListener listener;

    public FilterAdapter(List<Game> games) {
        this.games = games;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(List<Game> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.bindView(game);
        holder.itemView.setOnClickListener(v -> listener.onGameClick(game));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_layout, parent, false));
    }

    public void setOnClickGameListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, genres, price;
        ImageView poster, platforms;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.iv_poster);
            title = itemView.findViewById(R.id.tv_title);
            genres = itemView.findViewById(R.id.tv_genres);
            platforms = itemView.findViewById(R.id.iv_platforms);
            price = itemView.findViewById(R.id.tv_price);
        }

        public void bindView(Game game) {
            CircularProgressDrawable cpd = new CircularProgressDrawable(itemView.getContext());
            cpd.setStrokeWidth(5f);
            cpd.setCenterRadius(30f);
            cpd.start();
            Picasso.get().load(game.getPosterUrl()).placeholder(cpd).into(poster);
            title.setText(game.getTitle());
            if (game.getPlatforms().toLowerCase().contains("windows")) {
                platforms.setImageResource(R.drawable.ic_windows);
            }
            StringBuilder fullText = new StringBuilder();
            for (Genre genre : game.getGenres()) {
                fullText.append(genre.getTitle()).append(", ");
            }
            genres.setText(fullText.substring(0, fullText.length()-2));
            price.setText("$"+game.getPrice());
        }
    }
}
