package com.example.wjmas_000.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    MapView mMapView;
    GoogleMap map;
    static int MY_PERMISSIONS_ACCESS_FINE_LOCATION;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapboi);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.setMyLocationEnabled(true);
                //map.setOnMyLocationButtonClickListener();
                //map.setOnMyLocationClickListener(getActivity());


                LatLng mylatlong = new LatLng(((GameActivity) getActivity()).getLatitude(), ((GameActivity) getActivity()).getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(mylatlong));
                map.moveCamera(CameraUpdateFactory.zoomIn());

                // Instantiates a new Polygon object at points to define a rectangle
                //top left, bottom left, bottom right, top right

                PolygonOptions rectOptions = new PolygonOptions().add(
                        new LatLng(42.030000, -93.653965),
                        new LatLng(42.022987, -93.653965),
                        new LatLng(42.023051, -93.638737),
                        new LatLng(42.029745, -93.638866)
                        );

                // Get back the mutable Polygon
                Polygon polygon = map.addPolygon(rectOptions);
                //setting game bounds
                LatLngBounds llb = new LatLngBounds(new LatLng(42.023051, -93.638737), new LatLng(42.029745, -93.638866));

                if (!llb.contains(mylatlong)) {
                    Toast.makeText(getActivity(), "HEY YOU ARE OUT OF BOUNDS", Toast.LENGTH_SHORT).show();
                }

                ///*
                ArrayList<String> seekerUsernames = new ArrayList<String>(10);
                ArrayList<LatLng> seekerLocations = new ArrayList<LatLng>(10);

                //obtain this from backend, remember to dynamically remove the hiders and seekers from the map

                for (int i = 0; i < seekerUsernames.size(); i++) {
                    map.addMarker(new MarkerOptions().position(seekerLocations.get(i)).title(seekerUsernames.get(i)));
                }

                //HIDERS
                ArrayList<String> hiderUsernames = new ArrayList<String>(10);
                ArrayList<LatLng> hiderLocations = new ArrayList<LatLng>(10);

                for (int i = 0; i < hiderUsernames.size(); i++) {
                    map.addMarker(new MarkerOptions().position(hiderLocations.get(i)).title(hiderUsernames.get(i)));
                }
                //*/
            }
        });

        return rootView;
    }


}
