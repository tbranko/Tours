package com.railzapp.tours;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;


public class MapsActivity extends Activity implements GoogleMap.OnMapLongClickListener {

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapsInitializer.initialize(this);
        // Get a handle to the Map Fragment
        MapFragment map_fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        map = map_fragment.getMap();

        // TODO Add check here if GPS is available. If gms is not initialized, apk will crash
        map.setMyLocationEnabled(true);

        // Set default location (Brussels, wide area)
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(50.8199, 4.37265), 15.0f) );

        // Set event listener
        map.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        DialogSaveGPSLocation dialog = new DialogSaveGPSLocation(point);
        dialog.show(getFragmentManager(), null);
    }
}