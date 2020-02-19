package com.example.grabguyod;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.graphics.Color;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
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






import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;


public class map extends FragmentActivity implements OnMapReadyCallback, PermissionsListener{
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
    private mapLocationCallback callback = new mapLocationCallback(this);
    private Location mLastLocation;
    private Location locs;
    public  Button dmlogout;
    private FirebaseAuth user;
    private  int circlerad = 2;
    private boolean driverfound = false;
     final List <String> keyNamelist = new ArrayList<String>();
    final List <Double>  latlist = new ArrayList<Double>();
    final List <Double>  lnglist = new ArrayList<Double>();
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private SymbolManager symbolManager;
    private Symbol symbol;
    private static final String AIRPORT_ICON_HARBOR = "airport-15";

    private int nor;
    private LatLng ridercoord;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mauth = FirebaseAuth.getInstance();

        //LOGOUT ??? //

       /* dmlogout = (Button) findViewById(R.id.logout);
        dmlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    Intent intent = new Intent(map.this, Main3Activity.class);
                    startActivity(intent);
                    finish();
                    return;
                }


            }
        });
*/








    }
            @Override
            public void onMapReady(@NonNull final MapboxMap mapbox) {
                this.mapbox = mapbox;

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            if(user != null) {
                                onDestroy();
                                Intent intent = new Intent(map.this, Main3Activity.class);
                                startActivity(intent);
                                FirebaseAuth.getInstance().signOut();
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
                                 showCrosshair();
                                initLocationEngine();
                                getriders(style);





















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
       private static  class mapLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

           private final WeakReference<map> activityWeakReference;

           mapLocationCallback(map activity) {
               this.activityWeakReference = new WeakReference<>(activity);
           }

           public void onSuccess(LocationEngineResult result) {

               map activity = activityWeakReference.get();

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
               map activity = activityWeakReference.get();
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



    private void getriders(final Style style){
        DatabaseReference drivloc = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        drivloc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                int size = (int) dataSnapshot.getChildrenCount();



             /*  keyNamelist.clear();
                latlist.clear();
                lnglist.clear();*/


               for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String keyName = areaSnapshot.getKey();
                   keyNamelist.add(keyName);
                     Double lat = areaSnapshot.child("l").child("0").getValue(Double.class);
                    latlist.add(lat);
                     Double lng = areaSnapshot.child("l").child("1").getValue(Double.class);
                    lnglist.add(lng);



                   symbolManager = new SymbolManager(mapView, mapbox, style);
                   symbolManager.setIconAllowOverlap(true);
                   symbolManager.setTextAllowOverlap(true);
                   symbol = symbolManager.create(new SymbolOptions()
                           .withLatLng(new LatLng(lat, lng))
                           .withIconImage(AIRPORT_ICON_HARBOR)
                           .withIconSize(2.0f)
                           .withDraggable(true));





             }














            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    /*public void addmarker(@NonNull Style style){
            getriders();
        symbolManager = new SymbolManager(mapView, mapbox, style);
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setTextAllowOverlap(true);
        symbol = symbolManager.create(new SymbolOptions()
                .withLatLng(new LatLng(latlist.size(), lnglist.size()))
                .withIconImage(AIRPORT_ICON_HARBOR)
                .withIconSize(2.0f)
                .withDraggable(true));


    }*/











}

