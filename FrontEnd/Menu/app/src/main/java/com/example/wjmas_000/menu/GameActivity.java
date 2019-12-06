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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class GameActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private WebSocketClient cc;
    private double longitude;
    private double latitude;
    private LocationManager lm;

    String gameSessionId = "10cab074-218e-41b1-853a-abd5e76a19a8";
        //getIntent().getStringExtra("GAME_SESSION_ID");


    public LocationListener locListen = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("", "NonNull location");
            lm.removeUpdates(this);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Toast.makeText(GameActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
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

    //TODO not sure if drawer works properly, needs testing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar_game);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_game);
        NavigationView navigationView = findViewById(R.id.nav_view_game);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
            Toast.makeText(GameActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        } else {
            lm.requestLocationUpdates(bestProvider, 1000, 0, locListen);
        }

        Draft[] drafts = {new Draft_6455()};

        String w = "ws://coms-309-vb-1.misc.iastate.edu:8080/user/aaron/";

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
                    Toast toast = Toast.makeText(getApplicationContext(),
                            message,
                            Toast.LENGTH_SHORT);
                    toast.show();
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
            obj.put("session", gameSessionId);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            cc.send(obj.toString());
        } catch (Exception e)
        {
            Log.d("ExceptionSendMessage:", e.toString());
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new ChatFragment()).commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.nav_lobby:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new LobbyFragment()).commit();
                break;
            case R.id.nav_Map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new MapFragment()).commit();
                break;
            case R.id.nav_found_player:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new FoundPlayerFragment()).commit();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new ChatFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


}
