package com.example.nazanin.iotlab3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nazanin.iotlab3.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button startBtn;
    private LinearLayout infolayout,cellInfolayout;
    private AlertDialog.Builder builder;
    private TextView timeTxt,latTxt,lonTxt,speedTxt,accTxt,gpsstatTxt;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);


    public static int PERMISSION_READ_STATE = 1;
    private static int PERMISSION_COURSE_STATE = 2;
    private static int PERMISSION_FINE_STATE = 3;
    public static final int GROUP_PERMISSION = 1;
    private ArrayList<String> permissionsNeeded = new ArrayList<>();
    private ArrayList<String> permissionsAvailable = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        infolayout = findViewById(R.id.infolayer);
        cellInfolayout = findViewById(R.id.celllayer);
        startBtn = findViewById(R.id.navBtn);
        timeTxt = findViewById(R.id.time);
        accTxt = findViewById(R.id.acc);
        latTxt = findViewById(R.id.lat);
        lonTxt = findViewById(R.id.lon);
        speedTxt = findViewById(R.id.speed);
        accTxt = findViewById(R.id.acc);
        gpsstatTxt = findViewById(R.id.gpsstat);
        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        permissionsAvailable.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsAvailable.add(Manifest.permission.READ_PHONE_STATE);
        permissionsAvailable.add(Manifest.permission.ACCESS_FINE_LOCATION);

        checkMobileGPSstatus();


        for (String permission : permissionsAvailable){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                permissionsNeeded.add(permission);
            }
        }
        //permissions
        if(permissionsNeeded.size()>0){
            RequestPermission(permissionsNeeded);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COURSE_STATE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_STATE);
            }

            else {

            }
        }

    }

    private void createDialogNavigation(){
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message).setTitle("Setting The GPS")
                .setCancelable(false)
                .setPositiveButton("settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Setting The GPS");
        alert.show();
    }
    private void checkMobileGPSstatus(){
        LocationManager lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE ) ;
        boolean gpsEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }

        if(!gpsEnabled){
            gpsstatTxt.setText("GPS is not active");
            createDialogNavigation();
        }
        else{
            gpsstatTxt.setText("GPS is active");
        }
    }
    private void RequestPermission(ArrayList<String> permissions){
        String[] permissionList = new String[permissions.size()];
        permissions.toArray(permissionList);
        ActivityCompat.requestPermissions(this,permissionList,GROUP_PERMISSION);
    }
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        Log.d("iust","on red");
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.DARK,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull final Style style) {
                        enableLocationComponent(style);
                        Log.d("iust","on stylem");
                    }
                });

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
            locationComponent.setRenderMode(RenderMode.GPS);

            initializeLocationEngine();
        } else {

        }
    }


    @SuppressLint("MissingPermission")
    private void initializeLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startScanning(View view) {

        infolayout.setVisibility(View.GONE);
        startBtn.setVisibility(View.GONE);
        cellInfolayout.setVisibility(View.VISIBLE);
        mapView.setVisibility(View.VISIBLE);
        mapView.getMapAsync(this);

    }
    private String getCurrentTime(){
        String date = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            date = df.format(Calendar.getInstance().getTime());
        }
        return date;
    }

    public void showGPSstatus(View view) {
        checkMobileGPSstatus();
    }

    private class MainActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;


        MainActivityLocationCallback(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {

            MainActivity activity = activityWeakReference.get();

            if (activity != null) {
                final Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                String time = getCurrentTime();
                Double lon = location.getLongitude();
                Double lat = location.getLatitude();
                float speed = location.getSpeed();
                float accuracy = location.getAccuracy();

                timeTxt.setText("Current Date & Time : "+time);
                lonTxt.setText("Longitude: "+lon);
                latTxt.setText("Latitude: "+lat);
                speedTxt.setText("Speed: "+speed);
                accTxt.setText("Accuracy: "+accuracy);

                mapboxMap.getStyle(new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                    }
                });

                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }




        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    // we override life cycles here because map and android both have their life cycles
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

    // we should end location updates and mapview to avoid memory leak
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}

