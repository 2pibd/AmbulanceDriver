package com.twopibd.dactarbari.ambulance.drivers.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.twopibd.dactarbari.ambulance.drivers.listeners.locatiionRetrivedListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;





public class GPSOpenActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        getPermissions();


    }
    private void getPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now

                            mGoogleApiClient = new GoogleApiClient.Builder(GPSOpenActivity.this)
                                    .addApi(LocationServices.API)
                                    .addConnectionCallbacks(GPSOpenActivity.this)
                                    .addOnConnectionFailedListener(GPSOpenActivity.this).build();

                            mGoogleApiClient.connect();



                            /*

                            SmartLocation.with(context).location()
                                    .oneFix().start(GPSOpenActivity.this);

                             */


                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        requestLocation();

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        requestLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    GPSOpenActivity.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location!=null&& locatiionRetrivedListener.myLocationRetriver_!=null) {
            locatiionRetrivedListener.myLocationRetriver_.onLocationretrived(location, getLocationName(location));
        }

    }

    private void requestLocation() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        {

                            /*
                            SmartLocation.with(context).location()
                                    .oneFix().start(this);

                             */

                        }

                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(GPSOpenActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    //    @Override
//    public void onLocationUpdated(Location location) {
//
////        locatiionRetrivedListener.myLocationRetriver_.onLocationretrived(location,getLocationName(location));
//
//
//     //
//        /*
//
//        SharedData.address=getLocationName(location);
//        SharedData.location=location;
//
//         */
//
//    }
    private String getLocationName(Location temp_location) {
        String name="";
        // Toast.makeText(context, ""+temp_location.getLatitude()+"\n"+temp_location.getLongitude(), Toast.LENGTH_LONG).show();


        if (temp_location!=null){
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(temp_location.getLatitude(), temp_location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


               if (true) {
                   String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                   String city = addresses.get(0).getLocality();
                   String state = addresses.get(0).getAdminArea();
                   String country = addresses.get(0).getCountryName();
                   String postalCode = addresses.get(0).getPostalCode();
                   String knownName = addresses.get(0).getFeatureName();
                 //  name = knownName + "\n" + address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country;
                   name = country;
                 //  Log.i("citys",city);
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            name="No location available";
        }
        return  name;



    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        if (location!=null) {
          //  Log.i("citys","Changed");

            locatiionRetrivedListener.myLocationRetriver_.onLocationretrived(location, getLocationName(location));
        }

    }
}