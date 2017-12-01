package com.sabiogo.pickingapp;

/**
 * Created by Federico on 29/11/2017.
 */

import android.app.Activity;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data_access.LogsDAO;

import static com.google.android.gms.common.api.GoogleApiClient.*;

public class Localizacion  {

  /*  double longitud = 0;
    double latitud = 0;
    private LocationManager mLocationManager;
    Context context;
    Activity activity;


    public Localizacion(Activity activity, Context context){
        this.context = context;
        this.activity = activity;

        chequearPermisos();
        mLocationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        GPSEncendido();
    }

    public void chequearPermisos(){
        int permissionCheck = ContextCompat.checkSelfPermission(activity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // ask permissions here using below code
            ActivityCompat.requestPermissions(activity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    102);
        }
    }


    private Boolean GPSEncendido() {
       *//* Boolean gpsEncendido;
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        gpsEncendido = Settings.Secure.isLocationProviderEnabled(contentResolver,LocationManager.GPS_PROVIDER);*//*

        //if(!gpsEncendido){

        // }
        //return gpsEncendido;

        if (!localizacionActiva())
            mostrarAlerta();
        else
            obtenerUbicacion();
        return localizacionActiva();
    }

    public void obtenerUbicacion(){
        if (!(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setBearingRequired(true);
            criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setSpeedRequired(true);
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            try{
                mLocationManager.requestSingleUpdate(criteria, locationListenerGPS, null);
                //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  1000, 1, locationListenerGPS);

            } catch (Exception ex){
                throw ex;
            }
            //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  1000, 1, locationListenerGPS);
        }
    }

    private boolean localizacionActiva() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void mostrarAlerta() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada. Por favor active su ubicación. ")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void chequearPermisos(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // ask permissions here using below code
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    102);
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            latitud = location.getLongitude();
            longitud = location.getLatitude();
            mLocationManager.removeUpdates(locationListenerGPS);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };

*/




}
