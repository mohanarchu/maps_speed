package product.vehicle.vehiclesystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import product.vehicle.vehiclesystem.FragmentCalls.FragmentTransactionCall;

public class Homemap extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
   public static  double mSpeed;
   static TextView time;
   CardView view,view1;
   public static TextView odo;
    static TextView distance;
    public static TextView myspeed;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    TextView iv_trip, iv_profile;
    FusedLocationProviderClient mFusedLocationClient;
    public static ProgressDialog progressDialog;
    static int p = 0;
    static boolean status;
    LocationService myservices;
    LocationManager loaderManager;
    static long startTime, endTime;
    Button start,stop;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myservices = binder.getService();
            status = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    @Override
    protected void onDestroy() {
        if (status)
            unbindService();
        super.onDestroy();
    }

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }
    private void unbindService() {
        if (!status)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);

        status = false;

    }

    @Override
    public void onBackPressed() {
        if (!status)
            super.onBackPressed();
        else
            moveTaskToBack(true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemap);
        iv_profile = findViewById(R.id.iv_profile);
        iv_trip = findViewById(R.id.iv_trip);
        iv_profile.setOnClickListener(this);
        iv_trip.setOnClickListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myspeed = (TextView) findViewById(R.id.speed);
        distance = (TextView) findViewById(R.id.distance);
        time = (TextView) findViewById(R.id.time);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        TextView welcome = findViewById(R.id.welcome);
        TextView name = findViewById(R.id.name);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/athletic.ttf");
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "font/capture.ttf");

        TextView dis=findViewById(R.id.to_distance);
        odo=findViewById(R.id.odo);
        TextView trip2=findViewById(R.id.trip);
        welcome.setTypeface(typeface);
        name.setTypeface(typeface);
       view=(CardView)findViewById(R.id.visible);
        view1=(CardView)findViewById(R.id.contact);
        start.setTypeface(typeface1);
        stop.setTypeface(typeface1);
       start.setOnClickListener(this);
        stop.setOnClickListener(this);
        myspeed.setTypeface(typeface1);
        distance.setTypeface(typeface1);
        trip2.setTypeface(typeface1);
        iv_profile.setTypeface(typeface1);
        iv_trip.setTypeface(typeface1);
        time.setTypeface(typeface1);
        dis.setTypeface(typeface1);
        odo.setTypeface(typeface1);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((long) 1000.0 * 2);
        mLocationRequest.setFastestInterval((long) 1000.0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(String.valueOf(location.getSpeed()));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Homemap.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile:

                FragmentTransactionCall.TripHome(Homemap.this);
                Toast.makeText(this, "haii", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_trip:
                Animation slideup = AnimationUtils.loadAnimation(this, R.anim.slideup);
                iv_trip.startAnimation(slideup);
                FragmentTransactionCall.TripHome(Homemap.this);
                break;
            case R.id.start:
                checkGPS();
                loaderManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                assert loaderManager != null;
                if (!loaderManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    return;
                if (!status)
                    bindService();
                progressDialog = new ProgressDialog(Homemap.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("getting location");
                progressDialog.show();
                view1.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
                break;
            case R.id.stop:
                if (status)
                    unbindService();
                start.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                start.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                p = 0;
                break;
        }


    }
    private void checkGPS() {
        loaderManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!loaderManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showDiasble();
    }

    private void showDiasble() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Enable gps service")
                .setCancelable(false)
                .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                });
        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}

