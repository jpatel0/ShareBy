package com.zero.shareby;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG=MainActivity.class.getSimpleName();
    public static final String MAP_KEY="map_key";
    FirebaseAuth auth;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer=findViewById(R.id.nav_drawer_layout);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.nav_open,R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        auth=FirebaseAuth.getInstance();
        /*ImageView nav_image=findViewById(R.id.nav_profile_image);
        TextView nav_user_name=findViewById(R.id.nav_profile_name);

        if (mAuth.getCurrentUser()!=null){
            nav_user_name.setText(mAuth.getCurrentUser().getDisplayName());
            if (mAuth.getCurrentUser().getPhotoUrl()==null) {
                nav_image.setImageResource(R.drawable.sign);
            }
            else{
                Glide.with(MainActivity.this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .into(nav_image);
            }
        }*/

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                break;

            case R.id.nav_logout:
                if(auth.getCurrentUser()!=null){
                    AuthUI.getInstance().signOut(getApplicationContext());
                    SharedPreferences.Editor prefEditor= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    prefEditor.putBoolean(MAP_KEY,true);
                    prefEditor.putBoolean("uploaded",false);
                    prefEditor.commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                }
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new UserProfileFragment()).commit();
                break;

                default:
                    break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            finish();
    }

}
