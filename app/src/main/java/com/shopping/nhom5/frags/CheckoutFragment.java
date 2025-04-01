package com.shopping.nhom5.frags;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.CartItem;
import com.shopping.nhom5.models.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutFragment extends Fragment {


    public CheckoutFragment() {
        // Required empty public constructor
    }

    View v;
    NavController navController;
    Button payBtn;
    String totalPrice;
    CircularProgressIndicator indicator;
    List<CartItem> cartList;
    DatabaseReference mRef;
    FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        navController = Navigation.findNavController(v);
        initView();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("orders").child(user.getUid());
        totalPrice = "Pay $" + CheckoutFragmentArgs.fromBundle(getArguments()).getTotalPrice();
        payBtn.setText(totalPrice);
        cartList = CheckoutFragmentArgs.fromBundle(getArguments()).getCartList();
        payBtnClickListener();
    }

    private void initView() {
        payBtn = v.findViewById(R.id.pay_button);
        indicator = v.findViewById(R.id.circularProgressIndicator);
    }

    private void payBtnClickListener() {
        payBtn.setOnClickListener(v -> {
            v.setEnabled(false);
            indicator.setVisibility(View.VISIBLE);
            DatabaseReference myRef = mRef.push();
            Order order = new Order(myRef.getKey(), Double.parseDouble(CheckoutFragmentArgs.fromBundle(getArguments()).getTotalPrice()), cartList, Order.PENDING, user.getUid(), user.getEmail());
            myRef.setValue(order).addOnCompleteListener(task -> {
                v.setEnabled(true);
                indicator.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Ordered Successfully!", Toast.LENGTH_SHORT).show();
                    navigateToOrders();
                    FirebaseDatabase.getInstance().getReference("carts").child(user.getUid()).removeValue();
                    order.getGames().forEach(e -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("amount", e.getAmount());
                        FirebaseDatabase.getInstance().getReference("games").child(e.getId()).updateChildren(map);
                    });
                } else {
                    Toast.makeText(getContext(), "Failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void navigateToOrders() {
        NavDirections action = CheckoutFragmentDirections.actionCheckoutFragmentToOrdersFragment();
        navController.navigate(action);
    }
}