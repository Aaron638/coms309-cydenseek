package com.example.wjmas_000.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapFragment extends Fragment {

    MapView mMapView;
    GoogleMap map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap){
                map = googleMap;

                LatLng latlong = new LatLng(42.0261684881417,-93.6484479904175);
                MarkerOptions option = new MarkerOptions();
                option.position(latlong).title("Iowa State University");
                map.addMarker(option);
                map.moveCamera(CameraUpdateFactory.newLatLng(latlong));
                //map.moveCamera(CameraUpdateFactory.zoomIn());

                //showing that we can draw on map
                // Instantiates a new Polygon object and adds points to define a rectangle
                PolygonOptions rectOptions = new PolygonOptions()
                        .add(new LatLng(42.35, -93.0),
                                new LatLng(42.45, -93.0),
                                new LatLng(42.45, -93.2),
                                new LatLng(42.35, -93.2),
                                new LatLng(42.35, -93.0));

                // Get back the mutable Polygon
                Polygon polygon = map.addPolygon(rectOptions);
            }
        });

        return rootView;
    }


}
