package com.example.finalproject;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.finalproject.model.Model;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    NavController navController;
    public static NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_userDetailsFragment,
                R.id.nav_barbershops_list_Fragment,
                R.id.nav_queues_list_Fragment,
                R.id.nav_barbershopDetailsFragment,
                R.id.nav_newQueue,
                R.id.nav_editBarbershopFragment,
//                R.id.nav_hoursListFragment,
                R.id.nav_editUserFragment,
                R.id.nav_barbershopCalendarFragment)
                .setDrawerLayout(drawer)
                .build();


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.message_icon) {
            navController.navigate(R.id.nav_userDetailsFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_MyAccount:
                    navController.navigate(R.id.nav_userDetailsFragment);
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case R.id.menu_queues_list:
                    if(Model.instance.getUser().isBarbershop())
                        navController.navigate(R.id.nav_barbershopCalendarFragment);
                    else
                        navController.navigate(R.id.nav_queues_list_Fragment);
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case R.id.menu_barbershopsList:
                    navController.navigate(R.id.nav_barbershops_list_Fragment);
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case R.id.menu_logout:
                    //SignOut from session:
                    Model.instance.signOut();
                    //Pop the all prev pages to start over:
                    while(navController.popBackStack());
                    //Set user on Model to null
                    Model.instance.setUser(null,()->{});
                    //Set item menu visible if he is np visible:
                    if(!(navigationView.getMenu().getItem(0).getSubMenu().getItem(1).isVisible()))
                        navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setVisible(true);
                    drawer.closeDrawer(GravityCompat.START);
                    navController.navigate(R.id.nav_login);
                    break;
                default:
                    break;
            }

            return true;
    }

    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}