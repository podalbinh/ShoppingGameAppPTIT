package com.shopping.nhom5.frags;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shopping.nhom5.R;
import com.shopping.nhom5.adapters.OrderAdapter;
import com.shopping.nhom5.models.Order;


public class OrdersFragment extends Fragment {

    OrderAdapter adapter;
    FirebaseUser user;
    RecyclerView rc;
    Query query;
    Order mCurrentItemOrder;
    DatabaseReference mRef;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rc = view.findViewById(R.id.orders_rc);
        mRef = FirebaseDatabase.getInstance().getReference("orders");
        user = FirebaseAuth.getInstance().getCurrentUser();
        query = FirebaseDatabase.getInstance().getReference("orders").child(user.getUid()).orderByChild("createdAt");
        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();

        adapter = new OrderAdapter(options);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(adapter);
        registerForContextMenu(rc);
        adapter.setOnLongItemClickListener((v, order) -> {
            mCurrentItemOrder = order;
            v.showContextMenu();
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.order_contextual_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cancel_order) {
            if (mCurrentItemOrder.getStatus().equals(Order.COMPLETED)) {
                Toast.makeText(getContext(), getResources().getString(R.string.completed_already), Toast.LENGTH_SHORT).show();
            } else if(mCurrentItemOrder.getStatus().equals(Order.CANCELED)) {
                Toast.makeText(getContext(), getResources().getString(R.string.canceled_already), Toast.LENGTH_SHORT).show();
            } else {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getResources().getString(R.string.cancel_order))
                        .setMessage(getResources().getString(R.string.do_you_want_to_cancel))
                        .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {

                        })
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                            mCurrentItemOrder.setStatus(Order.CANCELED);
                            mRef.child(user.getUid()).child(mCurrentItemOrder.getRef()).setValue(mCurrentItemOrder).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.canceled_successfully), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .show();
            }
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}