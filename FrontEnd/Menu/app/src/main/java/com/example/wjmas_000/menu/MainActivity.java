package com.example.wjmas_000.menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private WebSocketClient cc;
    private double longitude;
    private double latitude;
    private LocationManager lm;

    public LocationListener locListen = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("", "NonNull location");
            lm.removeUpdates(this);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //What do if rotation of device
        /*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MessageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_message);
        }
        */

        //This is for location

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(lm.getBestProvider(criteria, true)).toString();
        Location location = lm.getLastKnownLocation(bestProvider);
        if (location != null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        } else {
            lm.requestLocationUpdates(bestProvider, 1000, 0, locListen);
        }

        Draft[] drafts = {new Draft_6455()};

        String w = "ws://coms-309-vb-1.misc.iastate.edu:8080/user/joe/location";

        try{
            Log.d("Socket:", "Trying socket");
            cc = new WebSocketClient(new URI(w), (Draft) drafts[0]){
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d("OPEN", "run() returned: " + "is connecting");
                }

                @Override
                public void onMessage(String message) {
                    Log.d("", "run() returned: "+ message);
                    //???? what goes here?
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("CLOSE", "onClose() returned: " + reason);
                }


                @Override
                public void onError(Exception e)
                {
                    Log.d("Exception:", e.getMessage());
                }

            };
        } catch (URISyntaxException e) {
            Log.d("Exception:", e.getMessage());
            e.printStackTrace();
        }
        cc.connect();
        while(!cc.isOpen());
        try{
            JSONObject obj = new JSONObject();
            obj.put("session", "9ccea0b9-12a5-4baa-9a38-85bc66204190");
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            cc.send(obj.toString());
        } catch (Exception e)
        {
            Log.d("ExceptionSendMessage:", e.toString());
            e.printStackTrace();
        }



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_creategame:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CreateGameFragment()).commit();
                break;
            case R.id.nav_joingame:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new JoinFragment()).commit();
                break;
            case R.id.nav_leaderboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LeaderboardFragment()).commit();
                break;

            //case R.id.nav_map:
                //FragmentManager manager = getSupportFragmentManager();/*.beginTransaction().replace(R.id.fragment_container,
                //        new MapFragment()).commit();*/
                //manager.beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        //new MapFragment()).commit();
             //   launchMaps();

             //   break;

                /*
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
                break;
                */
                /*
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;

                 */
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                break;
/*
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;

 */
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //launches the maps activity
    private void launchMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}