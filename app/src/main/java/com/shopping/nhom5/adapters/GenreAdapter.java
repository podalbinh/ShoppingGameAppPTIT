package com.shopping.nhom5.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.Genre;

public class GenreAdapter extends FirebaseRecyclerAdapter<Genre, GenreAdapter.ViewHolder> {


    OnGenreClickListener listener;

    public GenreAdapter(@NonNull FirebaseRecyclerOptions<Genre> options) {
        super(options);
        Log.i("genreee", options.getSnapshots().toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_layout, parent, false));
    }

    public void setOnGenreClickListener(OnGenreClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, Genre genre) {
        Log.i("genreee", genre.toString());
        holder.title.setText(genre.getTitle());
        CircularProgressDrawable cpd = new CircularProgressDrawable(holder.itemView.getContext());
        cpd.setStrokeWidth(5f);
        cpd.setCenterRadius(30f);
        cpd.start();
        Glide.with(holder.itemView)
                .load(genre.getImgUrl())
                .placeholder(cpd)
                .into(holder.img);
        holder.itemView.setOnClickListener(v -> listener.onGenreClick(genre));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.genre_title);
            img = itemView.findViewById(R.id.genre_img);
        }
    }

    public interface OnGenreClickListener {
        void onGenreClick(Genre genre);
    }
}
