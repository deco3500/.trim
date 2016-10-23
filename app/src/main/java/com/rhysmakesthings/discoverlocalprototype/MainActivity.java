package com.rhysmakesthings.discoverlocalprototype;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private boolean mapReady = false;
    private static ArrayList<ArrayList<String>> markers = new ArrayList<ArrayList<String>>();
    private static HashMap<ArrayList<String>,LatLng> markerC = new HashMap<ArrayList<String>,LatLng>();
    Marker myMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String type = intent.getType();
        String action = intent.getAction();
        setContentView(R.layout.main_layout);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Button btn = (Button) findViewById(R.id.add);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddForm.class));
            }
        });
        if (Intent.ACTION_SEND.equals(action)) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    Intent i = new Intent(MainActivity.this, AddForm.class);
                    i.putExtra(Intent.EXTRA_TEXT,sharedText);
                    startActivity(i);
                }
            }
        }
        if (intent.getStringExtra("url") != null){
            ArrayList<String> add = new ArrayList<String>();
            add.add(intent.getStringExtra("url"));
            add.add(intent.getStringExtra("title"));
            add.add(intent.getStringExtra("tags"));
            markers.add(add);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Assign the instance of the map to a local variable
        map = googleMap;

        // Check if location permissions are active, and if so, enable "My Location"
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, 12f));
        }

        // Otherwise display "No Permissions" layout
        else {
            setContentView(R.layout.no_permission);
        }

        // Changed temporarily for debugging.
        map.getUiSettings().setScrollGesturesEnabled(false);
        mapReady = true;
    }
    private void markLocation(){
        if (mapReady && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (myMarker != null){
                myMarker.remove();
            }
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                LatLng location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                myMarker = map.addMarker(
                        new MarkerOptions().position(location).icon(
                                BitmapDescriptorFactory.fromAsset("run.png")));
                map.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
        }
    }
    private void placeChallenge(ArrayList<String> c){
        if (mapReady && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LatLng location = null;
            if (markerC.containsKey(c)){
                location = markerC.get(c);
            }
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            System.out.println(lastLocation);
            if (lastLocation != null || location != null) {
                if (location == null){
                    location = new LatLng(lastLocation.getLatitude()+0.0001f, lastLocation.getLongitude());
                    markerC.put(c, location);
                }
                map.addMarker(
                        new MarkerOptions().position(location).title(c.get(1)).snippet(c.get(2)));
            }
        }
    }
    public void onConnected(Bundle b) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationRequest req = new LocationRequest();
            req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            req.setInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, this);
        }
    }

    public void onConnectionSuspended(int a) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        for (ArrayList<String> s: markers){
            placeChallenge(s);
        }
        markLocation();
    }
}
