package com.shopping.nhom5.frags;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.*;
import com.shopping.nhom5.adapters.DiscountAdapter;
import com.shopping.nhom5.adapters.GameAdapter;
import com.shopping.nhom5.adapters.GenreAdapter;
import com.shopping.nhom5.models.CartItem;
import com.shopping.nhom5.models.Game;
import com.shopping.nhom5.models.Genre;
import com.shopping.nhom5.viewmodels.HomeFragmentViewModel;
import com.shopping.nhom5.R;
public class HomeFragment extends Fragment {

    View v;
    ImageSlider imageSlider;
    ArrayList<SlideModel> slideImgs;
    RecyclerView genresRV, discountsRV, newReleasesRV;
    ShimmerFrameLayout shimmerGenre, shimmerNewReleases, shimmerSlider;
    NavController navController;
    GenreAdapter genreAdapter;
    GameAdapter gameAdapter;


    DatabaseReference genresDR, gamesDR, cartDR, cartItemDR;

    HomeFragmentViewModel viewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        navController = Navigation.findNavController(v);
        genresDR = FirebaseDatabase.getInstance().getReference("genres");
        gamesDR = FirebaseDatabase.getInstance().getReference("games");
        cartDR = FirebaseDatabase.getInstance().getReference("carts");
        initView();
        //searchBar();
        sliderImage();
        genres();
        discounts();
        latestGames();

        viewModel.getFinishedLoadingGenres().observe(requireActivity(), b -> {
            if (b) {
                shimmerGenre.stopShimmer();
                shimmerGenre.setVisibility(View.GONE);
                genresRV.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getFinishedLoadingNewReleases().observe(requireActivity(), b -> {
            if (b) {
                shimmerNewReleases.stopShimmer();
                shimmerNewReleases.setVisibility(View.GONE);
                newReleasesRV.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getFinishedLoadingSlider().observe(requireActivity(), b -> {
            if (b) {
                shimmerSlider.stopShimmer();
                shimmerSlider.setVisibility(View.GONE);
                imageSlider.setVisibility(View.VISIBLE);
            }
        });


    }

    public void initView() {
        imageSlider = v.findViewById(R.id.image_slider);
        genresRV = v.findViewById(R.id.recycler_view_genres);
        discountsRV = v.findViewById(R.id.recycler_view_discounts);
        newReleasesRV = v.findViewById(R.id.recycler_view_new_releases);
        shimmerGenre = v.findViewById(R.id.genre_shimmer);
        shimmerNewReleases = v.findViewById(R.id.new_releases_shimmer);
        shimmerSlider = v.findViewById(R.id.slider_shimmer);
    }

    public void sliderImage() {
        viewModel.navController = navController;
        viewModel.sliderImage(imageSlider);

    }

    private void genres() {
        FirebaseRecyclerOptions<Genre> options = new FirebaseRecyclerOptions.Builder<Genre>()
                .setSnapshotArray(viewModel.genres)
                .build();
        genreAdapter = new GenreAdapter(options);
        genresRV
                .setLayoutManager(
                        new LinearLayoutManager(getActivity(),
                                LinearLayoutManager.HORIZONTAL,
                                false)
                );
        genresRV.setAdapter(genreAdapter);
        genreAdapter.setOnGenreClickListener(this::navigateToGenreGames);
    }


    private void discounts() {
        List<Game> discounts = new ArrayList<>();
        DiscountAdapter adapter = new DiscountAdapter(discounts);
        discountsRV
                .setLayoutManager(
                        new LinearLayoutManager(getActivity(),
                                LinearLayoutManager.HORIZONTAL,
                                false)
                );
        discountsRV.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference("games").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                discounts.clear();
                Log.i("33333",snapshot.toString());
//                writeSnapshotToFileGames(snapshot);

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Game game = ds.getValue(Game.class);
                    if (game.getDiscount() != null && Double.parseDouble(game.getDiscount()) > 0.0)
                        discounts.add(ds.getValue(Game.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("genres").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("33333",snapshot.toString());
                writeSnapshotToFileGenres(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("slider");
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.i("33333",snapshot.toString());
//                writeSnapshotToFile(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setOnGameClickListener(this::navigateToGameDetail);
    }



    private void writeSnapshotToFileGames(DataSnapshot snapshotData) {
        try (FileOutputStream fos = getActivity().openFileOutput("games-json", Context.MODE_APPEND)) {
            StringBuilder json = new StringBuilder("{");

            for (DataSnapshot gameSnapshot : snapshotData.getChildren()) {
                json.append("\"").append(gameSnapshot.getKey()).append("\":{");

                for (DataSnapshot field : gameSnapshot.getChildren()) {
                    json.append("\"").append(field.getKey()).append("\":\"")
                            .append(field.getValue()).append("\",");
                }

                json.deleteCharAt(json.length() - 1);
                json.append("},");
            }

            json.deleteCharAt(json.length() - 1);
            json.append("}");
            fos.write(json.toString().getBytes());
        } catch (IOException e) {
            // Log the error if something goes wrong
            Log.e("FileWriteError", "Error writing to file", e);
        }
    }
    private void writeSnapshotToFileGenres(DataSnapshot snapshotData) {
        try (FileOutputStream fos = getActivity().openFileOutput("genres-json", Context.MODE_APPEND)) {
            StringBuilder json = new StringBuilder("{");

            for (DataSnapshot gameSnapshot : snapshotData.getChildren()) {
                json.append("\"").append(gameSnapshot.getKey()).append("\":{");

                for (DataSnapshot field : gameSnapshot.getChildren()) {
                    json.append("\"").append(field.getKey()).append("\":\"")
                            .append(field.getValue()).append("\",");
                }

                json.deleteCharAt(json.length() - 1);
                json.append("},");
            }

            json.deleteCharAt(json.length() - 1);
            json.append("}");
            fos.write(json.toString().getBytes());
        } catch (IOException e) {
            // Log the error if something goes wrong
            Log.e("FileWriteError", "Error writing to file", e);
        }
    }

    private void latestGames() {
        FirebaseRecyclerOptions<Game> options = new FirebaseRecyclerOptions.Builder<Game>()
                .setSnapshotArray(viewModel.games)
                .build();
        gameAdapter = new GameAdapter(options);
        gameAdapter.setOnGameClickListener(this::navigateToGameDetail);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        gameAdapter.setOnCartClickListener((game, cartBtn, checkBtn) -> {
            if (user != null) {
                cartItemDR = cartDR.child(user.getUid()).child(game.getId());
                addToCart(game, cartBtn, checkBtn);
            } else {
                Toast.makeText(getContext(), "You have to login first", Toast.LENGTH_SHORT)
                        .show();
                navigateToUserLogin();
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setReverseLayout(true);
        newReleasesRV.setLayoutManager(layoutManager);
        newReleasesRV.setAdapter(gameAdapter);
    }

    private void addToCart(Game game, Button cartBtn, Button checkBtn) {
        cartItemDR.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CartItem cartItem = task.getResult().getValue(CartItem.class);
                if (cartItem != null)
                    cartItem.plus();
                else {
                    cartItem = new CartItem(
                            game.getId(),
                            game.getTitle(),
                            game.getDescription(),
                            game.getPrice(),
                            game.getPosterUrl()
                    );
                }
                cartItemDR.setValue(cartItem).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        cartBtn.setVisibility(View.INVISIBLE);
                        checkBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Added Successfuly!", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });
    }


    private void navigateToGameDetail(Game game) {
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToGameDetailFragment(game, game.getTitle());
        navController.navigate(action);
    }

    private void navigateToUserLogin() {
        NavDirections action = HomeFragmentDirections.actionGlobalUserFragment();
        navController.navigate(action);
    }

    private void navigateToGenreGames(Genre genre) {
        NavDirections action = HomeFragmentDirections.actionHomeFragmentToGenreGamesFragment(genre.getTitle(), genre);
        navController.navigate(action);
    }

    @Override
    public void onStart() {
        super.onStart();
        gameAdapter.startListening();
        genreAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        gameAdapter.stopListening();
        genreAdapter.stopListening();
    }

}