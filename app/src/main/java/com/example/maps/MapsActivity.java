package com.example.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maps.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements MarkerAdapter.ItemListener, OnMapReadyCallback {
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ActivityMapsBinding binding;

    BottomSheetBehavior behavior;
    RecyclerView recyclerView;
    private MarkerAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;
    ArrayList<Marker> markers = new ArrayList<Marker>();

    LatLng spb = new LatLng(59.6, 30.2);
    LatLng ny = new LatLng(40.7, -73.9);
    LatLng moscow = new LatLng(55.5, 37.4);
    LatLng kiev = new LatLng(50.3, 30.3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getLocationPermission();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (slideOffset == 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        markers.add(new Marker(spb, "Saint Petersburg"));
        markers.add(new Marker(ny, "New York"));
        markers.add(new Marker(moscow, "Moscow"));
        markers.add(new Marker(kiev, "Kiev"));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MarkerAdapter(markers, this);
        recyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onItemClick(Marker marker) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker.getLocation());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);

        mMap.animateCamera(cu);

//        Snackbar.make(coordinatorLayout,item + " is selected", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for(Marker marker : markers) {
            addMarkerToMap(marker.getLocation(), marker.getName());
        }

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private void addMarkerToMap(LatLng latLng, String title){
        if(null != mMap) {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .draggable(true));
        }
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        try{
//            if(mLocationPermissionsGranted){
//                final Task location = mFusedLocationProviderClient.getLastLocation();
//
//            }
//        }catch (SecurityException e){
//            Toast.makeText(MapsActivity.this, "Security exception", Toast.LENGTH_SHORT).show();
//        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }
            else
                ActivityCompat.requestPermissions(this,
                        permissions,1234);
        }
        else
            ActivityCompat.requestPermissions(this,
                    permissions,1234);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = false;
                    return;
                }
            }
            mLocationPermissionsGranted = true;
            initMap();
        }
    }
}