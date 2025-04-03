package com.shopping.nhom5.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopping.nhom5.R;
import com.shopping.nhom5.adapters.listeners.OnItemClickListener;
import com.shopping.nhom5.models.CartItem;
import com.shopping.nhom5.models.Game;
import com.shopping.nhom5.models.Genre;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameDetailFragment extends Fragment {
    View view;
    Game game;
    DatabaseReference cartItemDR = null;
    FirebaseUser user;
    NavController navController;
    RecyclerView similarGamesRV;

    public GameDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        navController = Navigation.findNavController(view);
        game = GameDetailFragmentArgs.fromBundle(getArguments()).getGame();
        initView();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            cartItemDR = FirebaseDatabase.getInstance().getReference("carts").child(user.getUid()).child(game.getId());
        }

        setupImageSlider();
        setupSimilarGames();
    }

    private void initView() {
        ((TextView) view.findViewById(R.id.detail_tv_title)).setText(game.getTitle());
        ((TextView) view.findViewById(R.id.detail_tv_price)).setText("$"+game.getPrice());
        ((TextView) view.findViewById(R.id.detail_tv_developer)).setText(game.getDeveloper());
        ((TextView) view.findViewById(R.id.detail_tv_publisher)).setText(game.getPublisher());
        ((TextView) view.findViewById(R.id.detail_tv_platform)).setText(game.getPlatforms());
        ((TextView) view.findViewById(R.id.detail_tv_description)).setText(game.getDescription());
        ((TextView) view.findViewById(R.id.detail_tv_release_date)).setText(formatedDate(game.getReleaseDate()));
        ((Button) view.findViewById(R.id.detail_button_add_to_cart)).setOnClickListener(v -> addToCart(game, (Button) v));
        CircularProgressDrawable cpd = new CircularProgressDrawable(getContext());
        cpd.setStrokeWidth(5f);
        cpd.setCenterRadius(30f);
        cpd.start();
        Picasso.get().load(game.getThumbnailUrl()).placeholder(cpd).into(((ImageView) view.findViewById(R.id.detail_iv_thumbnail)));
        GridLayout genresGrid = view.findViewById(R.id.genres_grid);
        for(Genre genre : game.getGenres()) {
            MaterialCardView cardView = new MaterialCardView(getContext());
            cardView.setClickable(true);
            cardView.setOnClickListener(v -> navigateToGnereGames(genre));

            TextView textView = new TextView(getContext());
            textView.setText(genre.getTitle());
            textView.setTextSize(17);
            textView.setPadding(16,6,16,6);
            textView.setTextColor(getResources().getColor(R.color.text_color_primary));

            cardView.addView(textView);
            genresGrid.addView(cardView);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
            layoutParams.setMargins(0, 0, 8, 0);
            cardView.setLayoutParams(layoutParams);
        }
        similarGamesRV = view.findViewById(R.id.similar_games_rc);
    }

    private void addToCart(Game game, Button cartBtn) {
        if (cartItemDR == null) {
            navigateToUserLogin();
            return;
        }
        cartItemDR.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CartItem cartItem = task.getResult().getValue(CartItem.class);
                if (cartItem != null)
                    cartItem.plus();
                else {
                    cartItem = new CartItem(game.getId(), game.getTitle(), game.getDescription(), game.getPrice(), game.getPosterUrl());
                }
                cartItemDR.setValue(cartItem).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        cartBtn.setText("Added");
                        cartBtn.setEnabled(false);
                        Toast.makeText(getContext(), "Added Successfuly!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupImageSlider() {
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        for (String imgUrl: game.getImages())
            slideModels.add(new SlideModel(imgUrl, ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels);
    }

    private String formatedDate(Long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return simpleDateFormat.format(date);
    }

    private void navigateToUserLogin() {
        NavDirections action = GameDetailFragmentDirections.actionGlobalUserFragment();
        navController.navigate(action);
    }

    private void navigateToGnereGames(Genre genre) {
        navController.navigate(GameDetailFragmentDirections.actionGameDetailFragmentToGenreGamesFragment(genre.getTitle(), genre));
    }

    private void setupSimilarGames() {
        List<Game> similarGames = new ArrayList<>();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("games");
        mRef.orderByChild("title").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                similarGames.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Game game = s.getValue(Game.class);
                    similarGames.add(game);

                }
                SimilarGamesAdapter adapter = new SimilarGamesAdapter(similarGames);
                adapter.setOnClickListener(game -> navigateToGameDetail(game));
                similarGamesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                similarGamesRV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToGameDetail(Game game) {
        NavDirections action = GameDetailFragmentDirections.actionGameDetailFragmentSelf(game, game.getTitle());
        navController.navigate(action);
    }
}

class SimilarGamesAdapter extends RecyclerView.Adapter<SimilarGamesAdapter.ViewHolder> {

    List<Game> games;
    OnItemClickListener listener;

    public SimilarGamesAdapter(List<Game> games) {
        this.games = games;
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_games_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.title.setText(game.getTitle());
        holder.price.setText("$"+game.getPrice());
        CircularProgressDrawable cpd = new CircularProgressDrawable(holder.itemView.getContext());
        cpd.setStrokeWidth(5f);
        cpd.setCenterRadius(30f);
        cpd.start();
        Picasso.get().load(game.getPosterUrl()).placeholder(cpd).into(holder.image);
        holder.itemView.setOnClickListener(v -> listener.onGameClick(game));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, price;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_title);
            price = itemView.findViewById(R.id.text_view_price);
            image = itemView.findViewById(R.id.imgae_view_img);
        }
    }
}
