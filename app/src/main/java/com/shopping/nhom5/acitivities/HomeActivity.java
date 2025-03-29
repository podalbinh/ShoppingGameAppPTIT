package com.shopping.nhom5.acitivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.shopping.nhom5.R;

public class HomeActivity extends AppCompatActivity {

    EditText searchBarEt;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    NavController navController;
    DrawerLayout drawerLayout;
    NavigationView navView;
    AppBarConfiguration appBarConfiguration;

    private void initView() {
        searchBarEt = findViewById(R.id.edit_text_main_search);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        setupNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).setOpenableLayout(drawerLayout).build();
        setupToolbar();
        setupBottomNav();
        setupNavigationDrawer();
        hideBottomNavOnGameDetail();
    }



    private void setupNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment != null ? navHostFragment.getNavController() : null;
    }


    private void setupToolbar() {
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> NavigationUI.onNavDestinationSelected(item, navController));
    }

    private void setupBottomNav() {
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    private void setupNavigationDrawer() {
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void hideBottomNavOnGameDetail() {
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {

        if(navDestination.getId() == R.id.searchFragment) {
                toolbar.getMenu().clear();
                searchBarEt.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.main_menu);
                bottomNavigationView.setVisibility(View.VISIBLE);
                searchBarEt.setVisibility(View.GONE);
            }
        });
    }



}