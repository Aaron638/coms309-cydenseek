package com.example.wjmas_000.menu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private WebSocketClient cc;

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    Activity acti = this;

    GoogleMap map;
    int websocketResponseIdx = 0;
    double longitude;
    double latitude;
    private LocationManager lm;
    String gamesession;
    String username;
    String userSession;
    String playerCode;
    String password;

    //SEEKERS
    ArrayList<String> seekerUsernames = new ArrayList<String>();
    ArrayList<LatLng> seekerLocations = new ArrayList<LatLng>();

    //HIDERS
    ArrayList<String> hiderUsernames = new ArrayList<String>();
    ArrayList<LatLng> hiderLocations = new ArrayList<LatLng>();


    public boolean isHider() {
        return hider;
    }

    public void setHider(boolean hider) {
        this.hider = hider;
    }

    boolean hider;
    boolean winner;

    public LocationListener locListen = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("", "NonNull location");
            lm.removeUpdates(this);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            sendLatLong();
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

        //Getting the passed intents
        Bundle bundle = getIntent().getExtras();
        gamesession = bundle.getString("GAME_SESSION_ID");
        username = bundle.getString("username");
        userSession = bundle.getString("userSession");
        password = bundle.getString("password");

        setGamesession(gamesession);
        setUsername(username);
        setUserSession(userSession);

        //This is for location services
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(lm.getBestProvider(criteria, true)).toString();
        Location location = lm.getLastKnownLocation(bestProvider);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Toast.makeText(GameActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
        } else {
            lm.requestLocationUpdates(bestProvider, 1000, 0, locListen);
        }

        //This is for the game websocket
        Draft[] drafts = {new Draft_6455()};
        String w = "ws://coms-309-vb-1.misc.iastate.edu:8080/" + gamesession + "/" + username;

        try {
            Log.d("Socket:", "Trying socket");
            cc = new WebSocketClient(new URI(w), (Draft) drafts[0]) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d("OPEN", "run() returned: " + "is connecting");
                    //Once the websocket client is open, we will attempt to connect with the user session, the latitude, and the longitude
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("session", userSession);
                        obj.put("latitude", latitude);
                        obj.put("longitude", longitude);
                        cc.send(obj.toString());
                    } catch (Exception e) {
                        Log.d("ExceptionSendMessage:", e.toString());
                        e.printStackTrace();
                    }
                }

                //Set the player code
                @Override
                public void onMessage(String message) {
                    websocketResponseIdx++;
                    Log.d("", "run() returned: " + message);
                    //backend returns: {"hider":true,"session":"a5ac6634-a996-4a21-8ffc-a0a2bb17ab72"}
                    //recieve the player code and hider boolean
                    //after 5 min get a new response
                    if (websocketResponseIdx == 1){
                        try {
                            JSONObject userIsHiderAndFindCode = new JSONObject(message);
                            setPlayerCode(userIsHiderAndFindCode.getString("session"));
                            setHider(userIsHiderAndFindCode.getBoolean("hider"));
                            //sendLatLong();

                        } catch (JSONException err) {
                            Log.d("Error", err.toString());
                            err.printStackTrace();
                        }
                        //On the second response after we send our location
                        //we get the locations of seekers
                        //and we get the obfuscated locations of all hiders
                    } else if (websocketResponseIdx == 2){
                        try {
                            JSONArray mapStateArray = new JSONArray(message);
                            JSONObject jsonPlayers = mapStateArray.getJSONObject(0);
                            JSONArray jsonArrHiders = jsonPlayers.getJSONArray("hiders");
                            JSONArray jsonArrSeekers = jsonPlayers.getJSONArray("seekers");

                            for (int i=0; i<jsonArrHiders.length(); i++){
                                JSONObject hider = jsonArrHiders.getJSONObject(i);
                                hiderUsernames.add(hider.getString("username"));
                                hiderLocations.add(new LatLng(hider.getDouble("latitude"), hider.getDouble("longitude")));
                                map.addMarker(new MarkerOptions()
                                        .position(hiderLocations.get(i))
                                        .title(hiderUsernames.get(i)));

                            }
                            for (int i=0; i<jsonArrSeekers.length(); i++){
                                JSONObject seeker = jsonArrHiders.getJSONObject(i);
                                seekerUsernames.add(seeker.getString("username"));
                                seekerLocations.add(new LatLng(seeker.getDouble("latitude"), seeker.getDouble("longitude")));
                                map.addMarker(new MarkerOptions()
                                        .position(seekerLocations.get(i))
                                        .title(seekerUsernames.get(i)));
                            }

                        } catch (JSONException e) {
                            Log.d("Error", e.toString());
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("CLOSE", "onClose() returned: " + reason);
                }


                @Override
                public void onError(Exception e) {
                    Log.d("Exception:", e.getMessage());
                }

            };
        } catch (URISyntaxException e) {
            Log.d("Exception:", e.getMessage());
            e.printStackTrace();
        }
        cc.connect();
        while (!cc.isOpen()) ;

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new PlayerListFragment()).commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_lobby:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new PlayerListFragment()).commit();
                break;
            case R.id.nav_Map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new MapFragment()).commit();
                break;
            case R.id.nav_found_player:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new FoundPlayerFragment()).commit();
                break;
            case R.id.nav_leave_game:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new LeaveGameFragment()).commit();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_game, new PlayerListFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void websocketSend(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            cc.send(obj.toString());
        } catch (Exception e) {
            Log.d("ExceptionSendMessage:", e.toString());
            e.printStackTrace();
        }
    }

    public void sendLatLong(){
        JSONObject userLatLong = new JSONObject();
        try {
            userLatLong.put("latitude", getLatitude());
            userLatLong.put("longitude", getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cc.send(userLatLong.toString());
    }

    public String getGamesession() {
        return gamesession;
    }

    public void setGamesession(String gamesession) {
        this.gamesession = gamesession;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    public String getPlayerCode() {
        return playerCode;
    }

    public void setPlayerCode(String playerCode) {
        this.playerCode = playerCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password = p;
    }


}

