package com.example.wjmas_000.menu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private WebSocketClient cc;
    private String session;
    private String user;






    //TODO not sure if drawer works properly, needs testing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_login);
        NavigationView navigationView = findViewById(R.id.nav_view_login);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, new LoginFragment()).commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_main_login:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, new LoginFragment()).commit();
                break;
            case R.id.nav_create_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, new CreateAccountFragment()).commit();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, new LoginFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


    public void setSession(String s){
        session = s;
    }

    public String getSession(){
        return session;
    }

    public void setUsername(String s){
        user = s;
    }

    public String getUsername(){
        return user;
    }


}

