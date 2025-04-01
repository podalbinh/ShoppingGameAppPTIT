package com.shopping.nhom5.frags;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopping.nhom5.adapters.CartAdapter;
import com.shopping.nhom5.models.CartItem;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.Game;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    View v;
    TextView counts, totalGames, subTotal, totalP, subTotalLabel;
    EditText couponEt;
    Button couponConfirmBtn, checkoutBtn;
    ArrayList<CartItem> cartItems;
    FirebaseUser user;
    DatabaseReference mRef;
    NavController navController;
    int allGamesCount = 0;
    Double subTotalPrice = 0.0;
    final static int PLUS_NUMBER = 0;
    final static int MINUS_NUMBER = 1;
    Double theDiscount = 0.0, totalPrice;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_cart, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(v);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mRef = FirebaseDatabase.getInstance().getReference("carts").child(user.getUid());
            cartList();
            initView();
            setupCoupon();
            checkoutBtnClickListener();
        } else {
            Toast.makeText(getContext(), "You have to login first", Toast.LENGTH_SHORT).show();
            navigateToUserLogin();
        }
    }

    private void initView() {
        counts = v.findViewById(R.id.cart_et_games_count);
        totalGames = v.findViewById(R.id.cart_tv_total_games);
        subTotal = v.findViewById(R.id.cart_tv_sub_total);
        totalP = v.findViewById(R.id.cart_tv_total);
        couponEt = v.findViewById(R.id.edit_text_coupon);
        couponConfirmBtn = v.findViewById(R.id.button_confirm);
        subTotalLabel = v.findViewById(R.id.cart_tv_sub_total_label);
        checkoutBtn = v.findViewById(R.id.button_checkout);
    }

    private void setupCoupon() {
        couponConfirmBtn.setOnClickListener(v -> {
            if (couponEt.getText().toString().equalsIgnoreCase("NAKAMA2024")) {
                theDiscount = 0.2;
                manageTotalPrice();
                String perc = theDiscount*100+"%";
                subTotalLabel.setText("Subtotal\nDiscount");
                String subTotalPriceFormated = String.format("%.2f", subTotalPrice);
                subTotal.setText("$"+subTotalPriceFormated+"\n"+perc);
                Toast.makeText(getContext(), "Coupon is valid", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Coupon is not valid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cartList() {
        cartItems = new ArrayList<>();
        RecyclerView rc = v.findViewById(R.id.rc_cart_list);
        CartAdapter adapter = new CartAdapter(cartItems);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        rc.setAdapter(adapter);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                allGamesCount = 0;
                subTotalPrice = 0.0;
                for (DataSnapshot s: snapshot.getChildren()) {
                    CartItem cartItem = s.getValue(CartItem.class);
                    cartItems.add(cartItem);
                    manageCount(cartItem);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.onTrashClickListener(id -> mRef.child(id).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Deleted Successfuly!", Toast.LENGTH_SHORT).show();
                if (cartItems.size() < 1) {
                    cartItems.clear();
                    counts.setText("0 games");
                    totalGames.setText("0 games");
                    subTotal.setText("$0");
                    totalP.setText("$0");
                    adapter.notifyDataSetChanged();
                }
            }
        }));
        adapter.onCartClickListener(this::goToGameDetail);
        adapter.onPlusListener(id -> plusOrMinus(id, CartFragment.PLUS_NUMBER));
        adapter.onMinusListener(id -> plusOrMinus(id, CartFragment.MINUS_NUMBER));
    }

    private void goToGameDetail(String gameId) {
        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference("games").child(gameId);
        gamesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Game game = task.getResult().getValue(Game.class);
                navigateToGameDetail(game);
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void plusOrMinus(String id, int n) {
        DatabaseReference theRef = mRef.child(id);
        theRef.get().addOnCompleteListener(s -> {
            CartItem item = s.getResult().getValue(CartItem.class);
            if (item != null) {
                if (n == CartFragment.PLUS_NUMBER) {
                    item.plus();
                } else {
                    item.minus();
                }
                theRef.setValue(item);
            }
        });
    }


    private void manageCount(CartItem cartItem) {
        allGamesCount += cartItem.getAmount();
        counts.setText(allGamesCount+" games");
        totalGames.setText(allGamesCount+" games");
        manageSubTotalPrice(cartItem);
    }

    private void manageSubTotalPrice(CartItem cartItem) {
        subTotalPrice += Double.parseDouble(cartItem.getPrice())*cartItem.getAmount();
        String subTotalPriceFormated = String.format("%.2f", subTotalPrice);
        String perc = "";
        if (theDiscount > 0) {
            perc = "\n"+theDiscount*100+"%";
        }
        subTotal.setText("$"+subTotalPriceFormated+perc);

        manageTotalPrice();
    }

    private void manageTotalPrice() {
        totalPrice = subTotalPrice * (1-theDiscount);
        String totalPriceFormated = String.format("%.2f", totalPrice);
        totalP.setText("$"+totalPriceFormated);
    }

    private void checkoutBtnClickListener() {
        checkoutBtn.setOnClickListener(v -> {
            navigateToCheckOut();
        });
    }

    private void navigateToGameDetail(Game game) {
        NavDirections action = CartFragmentDirections.actionCartFragmentToGameDetailFragment(game, game.getTitle());
        navController.navigate(action);
    }

    private void navigateToUserLogin() {
        NavDirections action = CartFragmentDirections.actionGlobalUserFragment();
        navController.navigate(action);
    }

    private void navigateToCheckOut() {
        NavDirections action = CartFragmentDirections.actionCartFragmentToCheckoutFragment(totalPrice.toString(), cartItems);
        navController.navigate(action);
    }

}