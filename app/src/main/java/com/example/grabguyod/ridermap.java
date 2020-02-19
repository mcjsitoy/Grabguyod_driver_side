package com.example.grabguyod;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;

public  class ridermap extends FragmentActivity implements OnMapReadyCallback, PermissionsListener {
    private static final LatLng BOUND_CORNER_NW = new LatLng(7.165823, 125.646832);
    private static final LatLng BOUND_CORNER_SE = new LatLng(7.161096, 125.657170);
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    private final List<List<Point>> points = new ArrayList<>();
    private final List<Point> outerPoints = new ArrayList<>();
    private FirebaseAuth mauth;


    private MapView mapView;
    private MapboxMap mapbox;
    private PermissionsManager permissionsManager;
    private LocationEngine locationengine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private ridermapLocationCallback callback = new ridermapLocationCallback(this);













    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.ridermap);
        mapView = findViewById(R.id.rmapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mauth = FirebaseAuth.getInstance();
    }





    @Override
    public void onMapReady(@NonNull final MapboxMap mapbox) {
        this.mapbox = mapbox;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null) {
                    onDestroy();
                    Intent intent = new Intent(ridermap.this, Main3Activity.class);
                    FirebaseAuth.getInstance().signOut();
                    startActivity(intent);
                    finish();

                }

                }

        });





        mapbox.setStyle(new Style.Builder().fromUri("mapbox://styles/mcjsitoy/ck08cyj3p125l1cp6x5dj7veq"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        enableLocationComponent(style);
                        mapbox.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);

                        mapbox.setMinZoomPreference(10);
                        showBoundsArea(style);
                        //MARKER ADD//
                        /*SymbolManager symbolManager = new SymbolManager(mapView, mapbox, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconIgnorePlacement(true);*/
                        //MARKER ADD//
                        showCrosshair();
                        initLocationEngine();











                    }

                });
    }

    private void showBoundsArea(@NonNull Style loadedMapStyle) {
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthWest().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthEast().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthEast().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getSouthEast().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getSouthEast().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getSouthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getSouthWest().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthWest().getLatitude()));
        points.add(outerPoints);

        loadedMapStyle.addSource(new GeoJsonSource("source-id",
                Polygon.fromLngLats(points)));

        loadedMapStyle.addLayer(new FillLayer("layer-id", "source-id").withProperties(
                fillColor(Color.RED ),
                fillOpacity(.25f)
        ));
    }

    private void showCrosshair() {
        View crosshair = new View(this);
        crosshair.setLayoutParams(new FrameLayout.LayoutParams(15, 15, Gravity.CENTER ));
        crosshair.setBackgroundColor(Color.GREEN );
        mapView.addView(crosshair);
    }






    private void enableLocationComponent(@NonNull Style loadedMapStyle){



        if(PermissionsManager.areLocationPermissionsGranted(this)){
            LocationComponent locationComponent = mapbox.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

        }else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }



    }
    @SuppressWarnings("MissingPermission")
    public void initLocationEngine(){
        locationengine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
        locationengine.requestLocationUpdates(request, callback, getMainLooper());
        locationengine.getLastLocation(callback);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapbox.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }


            });


        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

   private static class ridermapLocationCallback  implements LocationEngineCallback<LocationEngineResult>{

        static boolean driverfound = false;
        static String driverfoundID;
        static int radius= 1;
        private final WeakReference<ridermap> activityWeakReference;

        ridermapLocationCallback(ridermap activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }



        public void onSuccess(final LocationEngineResult  result) {
            ridermap activity = activityWeakReference.get();



            if (activity != null) {

                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }
                Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
                        String.valueOf(result.getLastLocation().getLatitude()),
                        String.valueOf(result.getLastLocation().getLongitude())),
                        Toast.LENGTH_SHORT).show();





            }


            if (activity.mapbox != null && result.getLastLocation() != null){

                Location location = result.getLastLocation();
                activity.mapbox.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                //FEED RIDER DATA TO FIREBASE//
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                //FEED DATA TO FIREBASE//



                //getdriver info//


                //getdriverinfo//






                //CHECK IF ANY AVAILABLE DRIVERS NEARBY//
              /*  DatabaseReference dlocation = FirebaseDatabase.getInstance().getReference().child("driveravailable");
                GeoFire custfire = new GeoFire(dlocation);
                GeoQuery geoQuery = custfire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), radius);
                geoQuery.removeAllListeners();


                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            if(!driverfound) {
                                driverfound = true;
                                driverfoundID = key;

                                DatabaseReference driverref = FirebaseDatabase.getInstance().getReference().child("users").child("drivers").child(driverfoundID);
                                String customerid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                HashMap hashMap = new HashMap();
                                hashMap.put("customerrideid", customerid);
                                driverref.updateChildren(hashMap);



                            }
                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {
                            if(!driverfound){
                                radius++;

                                DatabaseReference dlocation = FirebaseDatabase.getInstance().getReference().child("driveravailable");
                                GeoFire custfire = new GeoFire(dlocation);
                                Location location1 = result.getLastLocation();
                                GeoQuery geoQuery = custfire.queryAtLocation(new GeoLocation(location1.getLatitude(), location1.getLongitude()), radius);
                                geoQuery.removeAllListeners();

                            }

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });*/

                //CHECK IF ANY AVAILABLE DRIVERS NEARBY//






            }






        }

        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            ridermap activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }



    }












    @SuppressWarnings("MissingPermmisions")
    @Override
    protected void onStart() {
        super.onStart();



        mapView.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();




        if(locationengine != null){
            locationengine.removeLocationUpdates(callback);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(userId);

        }








        mapView.onStop();




    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationengine != null){
            locationengine.removeLocationUpdates(callback);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(userId);
        }

        mapView.onDestroy();


    }


    private void getdriverlocation(){
        DatabaseReference driverlocs = FirebaseDatabase.getInstance().getReference().child("driveravailable");

        driverlocs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    }







