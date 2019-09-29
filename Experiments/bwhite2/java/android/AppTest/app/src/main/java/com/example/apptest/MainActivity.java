package com.example.apptest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION
            }, 1337);
        }
        LocationServices.getFusedLocationProviderClient(this)
            .getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    TextView text = findViewById(R.id.text);
                    text.setText("Altitude: " + location.getAltitude() +
                        "\nLatitude: " + location.getLatitude() +
                        "\nLongitude: " + location.getLongitude());
                }
            });
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(LocationRequest.create(), new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    TextView text = findViewById(R.id.text);
                    text.setText("Altitude: " + location.getAltitude() +
                        "\nLatitude: " + location.getLatitude() +
                        "\nLongitude: " + location.getLongitude());
                }
            }, Looper.getMainLooper());
    }
}