package com.zero.shareby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG=MainActivity.class.getSimpleName();
    public static final String MAP_KEY="map_key";
    FirebaseAuth auth;
    NavigationView navigationView;
    private DrawerLayout drawer;

    ViewPager viewPager;
    PagerAdapter pagerAdapter;

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

        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        viewPager= findViewById(R.id.viewpager);
        pagerAdapter=new PagerAdapter(this,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tab=findViewById(R.id.tabs);
        tab.setupWithViewPager(viewPager);

        auth=FirebaseAuth.getInstance();
        navigationView.setCheckedItem(R.id.nav_home);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_home:
                viewPager.setCurrentItem(0);
                break;

            case R.id.nav_logout:
                if(auth.getCurrentUser()!=null){
                    AuthUI.getInstance().signOut(getApplicationContext());
                    SharedPreferences.Editor prefEditor= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    prefEditor.putBoolean(MAP_KEY,true);
                    prefEditor.putBoolean("uploaded",false);
                    prefEditor.clear();
                    prefEditor.commit();
                    viewPager.setCurrentItem(0);
                }
                break;

            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this,UserProfile.class));
                break;

            case R.id.nav_share:
                Intent shareIntent=new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TITLE,"Shareby");
                shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey!, Check this awesome app for sharing things in your neiborhood..");
                startActivity(Intent.createChooser(shareIntent,"Complete action using"));
                break;

            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView nav_image=navigationView.getHeaderView(0).findViewById(R.id.nav_profile_image);
        TextView nav_user_name=navigationView.getHeaderView(0).findViewById(R.id.nav_profile_name);
        navigationView.setCheckedItem(R.id.nav_home);
        if (auth.getCurrentUser()!=null){
            nav_user_name.setText(auth.getCurrentUser().getDisplayName());
            if (auth.getCurrentUser().getPhotoUrl()==null) {
                nav_image.setImageResource(R.drawable.sign);
            }
            else{
                Log.d(TAG,"Glide image load");
                Glide.with(getApplicationContext())
                        .load(auth.getCurrentUser().getPhotoUrl())
                        .into(nav_image);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            finish();
    }

}
