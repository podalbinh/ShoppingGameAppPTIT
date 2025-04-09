package com.shopping.nhom5.frags;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopping.nhom5.R;
import com.shopping.nhom5.adapters.FilterAdapter;
import com.shopping.nhom5.models.Game;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    NavController navController;
    View v;
    FilterAdapter adapter;
    RecyclerView rc;
    ArrayList<Game> games;
    DatabaseReference mRef;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        navController = Navigation.findNavController(v);
        rc = view.findViewById(R.id.rc_search);
        adapter = new FilterAdapter(new ArrayList<Game>());
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(adapter);
        EditText et = getActivity().findViewById(R.id.edit_text_main_search);
        et.setEnabled(false);
        games = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference("games");
        adapter.setOnClickGameListener(game -> navigateToGameDetail(game));
        mRef.keepSynced(true);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                games.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    games.add(ds.getValue(Game.class));
                }
                et.setEnabled(true);
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        processSearch(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void navigateToGameDetail(Game game) {
        NavDirections action = SearchFragmentDirections.actionSearchFragmentToGameDetailFragment(game, game.getTitle());
        navController.navigate(action);

    }
    private void processSearch(String s) {
        ArrayList<Game> filteredList = new ArrayList<>();
        if(!s.trim().isEmpty()) {
            for(Game game: games) {
                if (game.getTitle().toLowerCase().contains(s.trim().toLowerCase())) {
                    filteredList.add(game);
                }
            }
        }
        adapter.filter(filteredList);

    }
}