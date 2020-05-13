package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.mapbox.geojson.FeatureCollection;
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
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.symbolZOrder;

public class driver_requestList extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    DatabaseReference database_requestForm;
    ListView listViewRequest;
    List<addRequest> addRequestList;
    Button bt_back;
    private static final LatLng BOUND_CORNER_NW = new LatLng(7.165823, 125.646832);
    private static final LatLng BOUND_CORNER_SE = new LatLng(7.161096, 125.657170);
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    private final List<List<Point>> points = new ArrayList<>();
    private final List<Point> outerPoints = new ArrayList<>();
    private FirebaseAuth mauth;
    private FirebaseAuth.AuthStateListener firebaseauthlistener;

    private MapView mapView;
    private MapboxMap mapbox;
    private PermissionsManager permissionsManager;
    private LocationEngine locationengine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private driver_requestList.driver_requestListLocationCallback callback = new driver_requestListLocationCallback(this);
    private Location mLastLocation;
    private Location locs;
    public Button dmlogout;
    private FirebaseAuth user;
    private int circlerad = 2;
    private boolean driverfound = false;
    private SymbolManager symbolManager;
    private Symbol symbols;
    private FeatureCollection featurecollect;
    final List<String> keyNamelist = new ArrayList<String>();
    final List<Double> latlist = new ArrayList<Double>();
    final List<Double> lnglist = new ArrayList<Double>();
    private static final String DOT = "dot-10";
    public TextView tv_lat,tv_long;
    int counter = 0;
    Button ty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_driver_request_list);
        mapView = findViewById(R.id.mapView3);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mauth = FirebaseAuth.getInstance();
        ty = findViewById(R.id.button);
        listViewRequest = (ListView) findViewById(R.id.listview_driverRequestList);

        database_requestForm = FirebaseDatabase.getInstance().getReference("requestForm");
        addRequestList = new ArrayList<>();
        bt_back = findViewById(R.id.button_back);

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(driver_requestList.this, driver_driveMode.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        ty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });


        }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapbox) {
        this.mapbox = mapbox;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mapbox.setStyle(new Style.Builder().fromUri("mapbox://styles/mcjsitoy/ck751ro940bjw1io9o3bcc9mz"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // The map has finished rendering
                        enableLocationComponent(style);
                        mapbox.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);

                        mapbox.setMinZoomPreference(10);
                        showBoundsArea(style);


                        showCrosshair();
                        initLocationEngine();
                        symbolManager = new SymbolManager(mapView, mapbox, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);
                        getrider(style);

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

    private static  class driver_requestListLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<driver_requestList> activityWeakReference;

        driver_requestListLocationCallback(driver_requestList activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public void onSuccess(LocationEngineResult result) {

            driver_requestList activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                  /* Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
                           String.valueOf(result.getLastLocation().getLatitude()),
                           String.valueOf(result.getLastLocation().getLongitude())),
                           Toast.LENGTH_SHORT).show();*/
            }

            if (activity.mapbox != null && result.getLastLocation() != null){
                Location location = result.getLastLocation();
                activity.mapbox.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driveravailable");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
            }
        }

        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            driver_requestList activity = activityWeakReference.get();
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



        database_requestForm.orderByChild("request_Status").equalTo("Pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                addRequestList.clear();
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    addRequest addRequest = requestSnapshot.getValue(addRequest.class);
                    addRequestList.add(addRequest);
                }

                list_layout adapter = new list_layout(driver_requestList.this, addRequestList);
                listViewRequest.setAdapter(adapter);
                openthis();
                counter++;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driveravailable");

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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driveravailable");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(userId);
        }
        mapView.onDestroy();
    }

    private void addMarker(@NonNull Style loadedMapStyle){
        SymbolManager symbolManager = new SymbolManager(mapView, mapbox, loadedMapStyle);
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconIgnorePlacement(true);
    }

    private void getrider(final Style style){
        DatabaseReference drivloc = FirebaseDatabase.getInstance().getReference("requestForm");
        drivloc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildrenCount();
                for (int z = 0; z < dataSnapshot.getChildrenCount(); z++) {
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String keyName = areaSnapshot.getKey();
                        keyNamelist.add(keyName);
                        Double lat = areaSnapshot.child("lat").getValue(Double.class);
                        latlist.add(lat);
                        Double lng = areaSnapshot.child("lon").getValue(Double.class);
                        lnglist.add(lng);

                        lat = latlist.get(z);
                        lng = lnglist.get(z);
                        if (lat != null && lng != null ) {
                            SymbolManager symbolManager = new SymbolManager(mapView, mapbox, style);
                            symbols = symbolManager.create(new SymbolOptions()
                                    .withLatLng(new LatLng(lat, lng))
                                    .withIconImage(DOT)
                                    .withIconColor("red")
                                    .withIconSize(2.0f)
                                    .withDraggable(true));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {



            }
        });
    }

    //NOTIFICATION IF NEW REQUEST IS ADDED
    public void openthis(){
           /* modal_dialog exampleDialog = new modal_dialog();
            exampleDialog.show(getSupportFragmentManager(), "example dialog");*/
           if(counter != 0) {
               Toast.makeText(this, "Data Change", Toast.LENGTH_SHORT).show();
           }
    }

    public void refresh(){
        startActivity(getIntent());
    }
}

/*@Override*/
   /* protected void onStart() {
        super.onStart();


        mapView.onStart();


    }*/