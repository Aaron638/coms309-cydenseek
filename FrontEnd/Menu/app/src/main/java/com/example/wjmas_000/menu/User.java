package com.example.wjmas_000.menu;

import com.google.android.gms.maps.model.LatLng;

public class User {

    public LatLng getLatlo() {
        return latlo;
    }

    public void setLatlo(LatLng latlo) {
        this.latlo = latlo;
    }

    //overloaded method that lets you set LatLng using doubles instead
    public void setLatlo(double latitude, double longitude) {
        LatLng tempLatLong = new LatLng(latitude, longitude);
        this.latlo = tempLatLong;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private LatLng latlo;
    private String session;
    private String username;

    public User(String session, String username){
        this.session = session;
        this.username = username;
        //We initially set the location of all users at the Iowa State Campanille
        latlo = new LatLng(42.025430, -93.646036);
    }
}
