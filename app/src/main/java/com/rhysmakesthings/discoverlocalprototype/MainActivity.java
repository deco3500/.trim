package com.rhysmakesthings.discoverlocalprototype;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private boolean mapReady = false;
    private static ArrayList<ArrayList<String>> markers = new ArrayList<ArrayList<String>>();
    private static HashMap<ArrayList<String>,LatLng> markerC = new HashMap<ArrayList<String>,LatLng>();
    public static ArrayList<String> friends = new ArrayList<String>();
    private static ArrayList<Integer> friendScore = new ArrayList<Integer>();
    Marker myMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!friends.contains("rhysmadethis\t(Score: 999999)")){
            friends.add("rhysmadethis\t(Score: 999999)");
            friendScore.add(999999);
        }

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
        Button btnP = (Button) findViewById(R.id.friend);
        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FriendActivity.class);
                String [] f = (String []) friends.toArray(new String[0]);
                i.putExtra("friendL", f);
                i.putExtra("friendS", friendScore);
                startActivity(i);
            }
        });
        Button btnProfile = (Button) findViewById(R.id.profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Profile");
                TextView t = new TextView(MainActivity.this);
                t.setText("Level: 8\nXP: 5000\nNext Level: 800xp\nScore: 5500\nArticles read: 23\nChallenges Issued: 9");
                LinearLayout f = new LinearLayout(MainActivity.this);
                f.setOrientation(LinearLayout.VERTICAL);
                f.addView(t);
                TextView lab = new TextView(MainActivity.this);
                lab.setText("My Interests");
                lab.setTypeface(null, Typeface.BOLD);
                f.addView(lab);
                TextView l = new TextView(MainActivity.this);
                l.setTypeface(null,Typeface.ITALIC);
                l.setText("#science, #education, #design");
                f.addView(l);
                EditText b = new EditText(MainActivity.this);
                b.setHint("Enter a new interest");
                f.addView(b);
                builder.setView(f);
                builder.show();
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
            add.add(intent.getStringExtra("challenged"));
            add.add("mine");
            markers.add(add);
        }
        if (intent.getStringArrayExtra("friendL") != null){
            friends = new ArrayList<String>(Arrays.asList(intent.getStringArrayExtra("friendL")));
            friendScore = intent.getIntegerArrayListExtra("friendS");
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
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, 18f));
            map.setMinZoomPreference(15f);
            map.setMaxZoomPreference(19.0f);
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
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(MainActivity.this);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(MainActivity.this);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(MainActivity.this);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());
                    snippet.setMovementMethod(LinkMovementMethod.getInstance());
                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
            if (lastLocation != null || location != null) {
                if (location == null){
                    location = new LatLng(lastLocation.getLatitude()+0.0001f, lastLocation.getLongitude());
                    markerC.put(c, location);
                }
                if (c.get(4).equals("mine")){
                    map.addMarker(
                            new MarkerOptions().position(location).title(c.get(1)).snippet(c.get(2)+"\n"+"You Challenged: "+c.get(3)+
                                    "\nTo Find: "+c.get(0)));
                } else if (c.get(4).equals("oor")){
                    float d = distance(location, new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    map.addMarker(
                            new MarkerOptions().position(location).icon(
                                    BitmapDescriptorFactory.fromAsset("oor.png")).title(c.get(1)).snippet(c.get(2)+"\n"+"Challenged By: "+c.get(3)+
                            "\nDistance: "+d+"m"));
                } else{
                    map.addMarker(
                            new MarkerOptions().position(location).icon(
                                    BitmapDescriptorFactory.fromAsset("collect.png")).title(c.get(1)).snippet(c.get(2)+"\n"+"Challenged By: "+c.get(3)+
                                    "\nCollect: "+c.get(0)));
                }

            }
        }
    }
    private float distance (LatLng llA, LatLng llB )
    {
        double lat_a = llA.latitude;
        double lat_b = llB.latitude;
        double lng_a = llA.longitude;
        double lng_b = llB.longitude;
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
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
        ArrayList<String> add = new ArrayList<String>();
        add.add("https://www.sciencenews.org/article/climate-change-shifts-how-long-ants-hang-coveted-real-estate?tgt=nr");
        add.add("Climate Change for Ants?");
        add.add("#science, #ants, #climate change");
        add.add("rhysmadethis");
        add.add("collect");
        if (!markers.contains(add)){
            markers.add(add);
            if (mapReady && ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                markerC.put(add, new LatLng(lastLocation.getLatitude()+0.0002f,lastLocation.getLongitude()-0.0001f));
            }
        }
        add = new ArrayList<String>();
        add.add("");
        add.add("UQ future plans...");
        add.add("#science, #ants, #climate change");
        add.add("rhysmadethis");
        add.add("oor");
        if (!markers.contains(add)){
            markers.add(add);
            if (mapReady && ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                markerC.put(add, new LatLng(lastLocation.getLatitude()-0.0004f,lastLocation.getLongitude()-0.0002f));
            }
        }

        for (ArrayList<String> s: markers){
            placeChallenge(s);
        }
        markLocation();
    }
}
