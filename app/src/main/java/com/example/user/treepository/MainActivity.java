package com.example.user.treepository;

import android.*;
import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Location;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//import com.firebase.client.ValueEventListener;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ChildEventListener;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   OnMapReadyCallback,
                   OnMarkerClickListener {

    SupportMapFragment sMapFragment;

    private GoogleMap mMap;
    private Intent gmapIntent;

    //hashmap associates database keys with tree markers
    private static HashMap<String, Marker> treeMarkers = new HashMap<String, Marker>();
    //key of the tree which was most recently clicked
    public static String currentTreeKey = "Default Tree";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sMapFragment = SupportMapFragment.newInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sMapFragment.getMapAsync(this);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.map, sMapFragment).addToBackStack("Drawer").commit();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            drawer.openDrawer(GravityCompat.START);
        }


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

        int id = item.getItemId();

        if (sMapFragment.isAdded())
            sFm.beginTransaction().hide(sMapFragment).addToBackStack("Map").commit();

        if (id == R.id.nav_map) {
            setTitle("Tree Map");
            if (!sMapFragment.isAdded()) {
                sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
                setTitle("Tree Map");
            }
            else {
                sFm.beginTransaction().show(sMapFragment).commit();
                setTitle("Tree Map");
            }

        } else if (id == R.id.searchbtn) {
            Intent intent = new Intent(MainActivity.this, SearchResult.class);
            startActivity(intent);

        } else if (id == R.id.nav_contact) {
            fm.beginTransaction().replace(R.id.content_frame, new ImportFragment()).addToBackStack("Contact").commit();
            setTitle("Contact Information");
        } else if (id == R.id.nav_login) {
            fm.beginTransaction().replace(R.id.content_frame, new LoginFragment()).addToBackStack("Login").commit();
            setTitle("Log In");
        } else if (id == R.id.nav_treeEdit) {
            fm.beginTransaction().replace(R.id.content_frame, new TreeEditFragment()).addToBackStack("Edit").commit();
            setTitle("Tree Edit");
        } else if (id == R.id.nav_treeInfo) {
            Intent intent = new Intent(MainActivity.this, TreeInfoFragment.class);
            startActivity(intent);
            setTitle("Detailed Tree Information");
        } else if (id == R.id.nav_registration) {
            fm.beginTransaction().replace(R.id.content_frame, new RegistrationFragment()).addToBackStack("Registration").commit();
            setTitle("Registration");
        } else if (id == R.id.nav_tour) {
            fm.beginTransaction().replace(R.id.content_frame, new TourFragment()).addToBackStack("Tour").commit();
            setTitle("Take the Tour");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //set up marker click listener
        mMap.setOnMarkerClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
                return;
            }
        }
        else{
            mMap.setMyLocationEnabled(true);
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String prevChildName) {
                //Getting the data from snapshot
                Float newLat =  Float.parseFloat(snapshot.child("latitude").getValue().toString());
                Float newLong = Float.parseFloat(snapshot.child("longitude").getValue().toString());

                //get latitude and longitude of tree
                LatLng nextTree = new LatLng(newLat, newLong);
                String treeTitle = snapshot.child("type").getValue().toString();

                //place tree marker on map
                Marker thisTreeMarker = mMap.addMarker(new MarkerOptions()
                        .position(nextTree)
                        .title(treeTitle)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.tree)));

                //associate database key with new marker
                thisTreeMarker.setTag(snapshot.getKey());
                treeMarkers.put(snapshot.getKey(), thisTreeMarker);
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(nextTree));
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String prevChildName) {
                //get data for the tree that was changed
                Float newLat =  Float.parseFloat(snapshot.child("latitude").getValue().toString());
                Float newLong = Float.parseFloat(snapshot.child("longitude").getValue().toString());

                //get latitude and longitude of tree
                LatLng nextTree = new LatLng(newLat, newLong);
                String treeTitle = snapshot.child("type").getValue().toString();

                //get marker associated with this tree
                Marker thisTreeMarker = treeMarkers.get(snapshot.getKey());

                //change relevant marker information
                thisTreeMarker.setPosition(nextTree);
                thisTreeMarker.setTitle(treeTitle);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

                //get marker associated with this tree
                Marker thisTreeMarker = treeMarkers.get(snapshot.getKey());
                treeMarkers.remove(snapshot.getKey());

                //remove marker for deleted tree from map
                thisTreeMarker.remove();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String prevChildKey) {

            }
        });
    }

    //handles marker click events by pulling up tree info page with appropriate information
    public boolean onMarkerClick(Marker clickedMarker) {
        //set the key of the tree which was clicked
        currentTreeKey = clickedMarker.getTag().toString();


        Intent intent = new Intent(MainActivity.this, TreeInfoFragment.class);
        startActivity(intent);
        setTitle("Detailed Tree Information");

        return true;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setTitle("Treasured Trees");
    }
}