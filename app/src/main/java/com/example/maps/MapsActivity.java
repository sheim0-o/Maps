package com.example.maps;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maps.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements ItemAdapter.ItemListener, OnMapReadyCallback {
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ActivityMapsBinding binding;

    BottomSheetBehavior behavior;
    RecyclerView recyclerView;
    private ItemAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getLocationPermission();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });



        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ArrayList<String> items = new ArrayList<>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
        items.add("Item 4");
        items.add("Item 5");
        items.add("Item 6");

        mAdapter = new ItemAdapter(items, this);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });



    }

    @Override
    public void onItemClick(String item) {

        Snackbar.make(coordinatorLayout,item + " is selected", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();


        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng spb = new LatLng(59.6, 30.2);
        LatLng ny = new LatLng(40.7, -73.9);
        LatLng moscow = new LatLng(55.5, 37.4);
        LatLng kiev = new LatLng(50.3, 30.3);

        addMarker(spb, "Saint Petersburg");
        addMarker(ny, "New York");
        addMarker(moscow, "Moscow");
        addMarker(kiev, "Kiev");

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

    private void addMarker(LatLng latLng, String title){
        if(null != mMap) {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .draggable(true));
        }
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if(task.isSuccessful()){
//                            Location currentLocation = (Location) task.getResult();
//
////                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
////                                    new LatLng(currentLocation.getLatitude(),
////                                            currentLocation.getLongitude()), 15F));
//
//                        }else{
//                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            }
        }catch (SecurityException e){
            Toast.makeText(MapsActivity.this, "Security exception", Toast.LENGTH_SHORT).show();
        }
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
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionsGranted = true;
                initMap();
            }
            else
                ActivityCompat.requestPermissions(this,
                        permissions,
                        1234);
        }
        else
            ActivityCompat.requestPermissions(this,
                    permissions,
                    1234);
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

















//
//import androidx.annotation.NonNull;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//
//import android.Manifest;
//import android.animation.ValueAnimator;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.view.View;
//import android.view.animation.TranslateAnimation;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//import android.widget.Toolbar;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.example.maps.databinding.ActivityMapsBinding;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.bottomsheet.BottomSheetBehavior;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
//    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
//    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
//
//    private Boolean mLocationPermissionsGranted = false;
//    private GoogleMap mMap;
//    private FusedLocationProviderClient mFusedLocationProviderClient;
//    private ActivityMapsBinding binding;
//
////    private Toolbar toolbar;
////    private RelativeLayout mapLayout;
////    private ConstraintLayout frontLayout;
////    private RelativeLayout.LayoutParams lp;
////    boolean showBackLayout = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
////        FloatingActionButton fab;
////        fab = findViewById(R.id.fab_filter);
////
////        View bottomSheet = findViewById(R.id.bottom_sheet);
////        //behavior = BottomSheetBehavior.from(bottomSheet);
////
////        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
////        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
////            @Override
////            public void onStateChanged(@NonNull View bottomSheet, int newState) {
////                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
////                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
////                }
////            }
////
////            @Override
////            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
////            }
////        });
////        fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
////                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
////                } else {
////                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
////                }
////            }
////        });
////        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
////            @Override
////            public void onStateChanged(@NonNull View bottomSheet, int newState) {
////                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
////                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
////                }
////            }
////
////            @Override
////            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
////                // React to dragging events
////            }
////        });
//
////        toolbar = findViewById(R.id.toolbar);
////        frontLayout = findViewById(R.id.frontLayout);
////        mapLayout = findViewById(R.id.mapLayout);
////        setActionBar(toolbar);
//        //configureBackdrop();
//
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        getLocationPermission();
//    }
//
//    private BottomSheetBehavior behavior;
//
////    private void configureBackdrop() {
////        // Get the fragment reference
////        //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.filter_fragment);
////
////        //BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(fragment.getView());
////
////        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
////
////            @Override
////            public void onStateChanged(@NonNull View bottomSheet, int newState) {
////
////                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
////                    bottomSheet.requestLayout();
////                    bottomSheet.invalidate();
////                }
////            }
////
////            @Override
////            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
////
////            }
////        });
////    }
//
////        if(!fragment) {
////
////
////
////            // Get the BottomSheetBehavior from the fragment view
////            BottomSheetBehavior.from(fragment)?.let { bsb ->
////                    // Set the initial state of the BottomSheetBehavior to HIDDEN
////                    bsb.state = BottomSheetBehavior.STATE_HIDDEN
////
////                // Set the trigger that will expand your view
////                fab_filter.setOnClickListener { bsb.state = BottomSheetBehavior.STATE_EXPANDED }
////
////                // Set the reference into class attribute (will be used latter)
////                mBottomSheetBehavior = bsb
////            }
////        }
////    }
//
////    private void dropLayout(){
////        showBackLayout = !showBackLayout;
////        lp = (RelativeLayout.LayoutParams) frontLayout.getLayoutParams();
////
////        if(showBackLayout){
////            ValueAnimator var = ValueAnimator.ofInt(mapLayout.getHeight());
////            var.setDuration(100);
////            var.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////                @Override
////                public void onAnimationUpdate(ValueAnimator animation) {
////                    lp.setMargins(0, (Integer) animation.getAnimatedValue(), 0, 0);
////                    frontLayout.setLayoutParams(lp);
////                }
////            });
////        }
////        else{
////            lp.setMargins(0,0,0,0);
////            frontLayout.setLayoutParams(lp);
////            TranslateAnimation anim = new TranslateAnimation(
////                    0,0,mapLayout.getHeight(),0
////            );
////
////            anim.setStartOffset(0);
////            anim.setDuration(200);
////            frontLayout.setAnimation(anim);
////        }
////    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng spb = new LatLng(59.6, 30.2);
//        LatLng ny = new LatLng(40.7, -73.9);
//        LatLng moscow = new LatLng(55.5, 37.4);
//        LatLng kiev = new LatLng(50.3, 30.3);
//
//        addMarker(spb, "Saint Petersburg");
//        addMarker(ny, "New York");
//        addMarker(moscow, "Moscow");
//        addMarker(kiev, "Kiev");
//
//        if (mLocationPermissionsGranted) {
//            getDeviceLocation();
//
//            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                    COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        }
//    }
//
//    private void addMarker(LatLng latLng, String title){
//        if(null != mMap) {
//            mMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .title(title)
//                    .draggable(true));
//        }
//    }
//
//    private void getDeviceLocation(){
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        try{
//            if(mLocationPermissionsGranted){
//                final Task location = mFusedLocationProviderClient.getLastLocation();
////                location.addOnCompleteListener(new OnCompleteListener() {
////                    @Override
////                    public void onComplete(@NonNull Task task) {
////                        if(task.isSuccessful()){
////                            Location currentLocation = (Location) task.getResult();
////
//////                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//////                                    new LatLng(currentLocation.getLatitude(),
//////                                            currentLocation.getLongitude()), 15F));
////
////                        }else{
////                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });
//            }
//        }catch (SecurityException e){
//            Toast.makeText(MapsActivity.this, "Security exception", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void initMap(){
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(MapsActivity.this);
//    }
//
//    private void getLocationPermission(){
//        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
//
//        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//            {
//                mLocationPermissionsGranted = true;
//                initMap();
//            }
//            else
//                ActivityCompat.requestPermissions(this,
//                        permissions,
//                        1234);
//        }
//        else
//            ActivityCompat.requestPermissions(this,
//                    permissions,
//                    1234);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        mLocationPermissionsGranted = false;
//
//        if (grantResults.length > 0) {
//            for (int i = 0; i < grantResults.length; i++) {
//                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                    mLocationPermissionsGranted = false;
//                    return;
//                }
//            }
//            mLocationPermissionsGranted = true;
//            initMap();
//        }
//    }
//}
//
//
//
//
//
////    @Override
////    public void onMapReady(GoogleMap googleMap) {
////        mMap = googleMap;
////
//////        LatLng spb = new LatLng(60, 31);
//////        addMarker(spb);
//////        mMap.moveCamera(CameraUpdateFactory.newLatLng(spb));
////
////        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
////        locationListener = new LocationListener() {
////            @Override
////            public void onLocationChanged(@NonNull Location location) {
////                userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
////                mMap.clear();
////                addMarker(userLatLng, "Your location");
////            }
////        };
////
////        askLocationPermission();
////    }
////    private void addMarker(LatLng latLng, String title){
////        if(null != mMap) {
////            mMap.addMarker(new MarkerOptions()
////                    .position(latLng)
////                    .title(title)
////                    .draggable(true));
////            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
////        }
////    }
////
////    private void askLocationPermission() {
////        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
////            @Override
////            public void onPermissionGranted(PermissionGrantedResponse response) {
////                if(ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
////                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(),
////                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
////                return;
////                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
////
////                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////                userLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
////                mMap.clear();
////                addMarker(userLatLng, "Your location");
////            }
////
////            @Override
////            public void onPermissionDenied(PermissionDeniedResponse response) {
////
////            }
////
////            @Override
////            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
////                token.continuePermissionRequest();
////            }
////        });
////    }
//
