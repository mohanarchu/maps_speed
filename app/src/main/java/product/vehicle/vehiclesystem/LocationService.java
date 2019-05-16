package product.vehicle.vehiclesystem;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = (long) 1000.0*2;
    private static final long FASTEST_INTERVAL = (long) 1000.0;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation,iStart, iEnd;
    static double distance = 0;
    double speed;


    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest=new LocationRequest();

        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }
        catch (SecurityException ignored)
        {

        }
    }
    protected void startLocationUpdates() {
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,  mLocationRequest, this);
        }
        catch (SecurityException e){}
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {

        Homemap.progressDialog.dismiss();
      mCurrentLocation=location;
        if (iStart==null)
        {

         iStart=iEnd=mCurrentLocation;
        }
        else
        {
            iEnd=mCurrentLocation;
        }
            updateUI();
           Homemap.mSpeed= location.getSpeed();
        Log.i("MapsActivity", "Locations: " + location.getLatitude() + " " + location.getLongitude());
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {

        if (Homemap.p==0)
        {

            distance = distance+( iStart.distanceTo(iEnd)/1000.00);
            Homemap.endTime=System.currentTimeMillis();
            long diff= Homemap.endTime-Homemap.startTime;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            if(Homemap.mSpeed>0.0) {
                String speed = String.format(Locale.ENGLISH, "%.0f",Homemap.mSpeed * 3.6) + "km/h";
                SpannableString s = new SpannableString(speed);
                s.setSpan(new RelativeSizeSpan(0.25f), s.length() - 4, s.length(), 0);
                Homemap.myspeed.setText(s);
            }
            else
                {
                Homemap.myspeed.setText(".......");
                }
            Homemap.time.setText( diff + "Min");

        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopUpdate();
        if(mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
           iStart= iEnd=null;
            distance=0;
        }
        return super.onUnbind(intent);
    }

    private void stopUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        distance=0;
    }

    public void connect()
    {
        mGoogleApiClient.connect();
    }


    public Location returnLocation()
    {
        return mCurrentLocation;
    }
    public class LocalBinder extends Binder {
        public LocationService getService(){
            return LocationService.this;
        }
    }
}