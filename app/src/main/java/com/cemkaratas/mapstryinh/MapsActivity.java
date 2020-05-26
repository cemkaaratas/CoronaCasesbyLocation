package com.cemkaratas.mapstryinh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.SquareCap;

import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location userLastLocation;
    LatLng userLastKnownLocation;
    List<Address> addressList =  new ArrayList<>();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("LocationDatabase",MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS countries(id INTEGER PRIMARY KEY,country VARCHAR)");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        }
;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                userLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                userLastKnownLocation = new LatLng(userLastLocation.getLatitude(), userLastLocation.getLongitude());
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    addressList = geocoder.getFromLocation(userLastLocation.getLatitude(), userLastLocation.getLongitude(), 1);
                    if (addressList != null && addressList.size() > 0) {
                        System.out.println("Your Last Location : " + addressList.get(0).getCountryName().toLowerCase());
                        String countryName = addressList.get(0).getCountryName().toLowerCase();
                        insertCountryNameToSql(countryName);
                        System.out.println(countryName);
                        intent = new Intent(getApplicationContext(), SelectDate.class);
                        startActivity(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("NO LAST KNOWN ADDRESS");
                }

            }
        } else {
            userLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            userLastKnownLocation = new LatLng(userLastLocation.getLatitude(), userLastLocation.getLongitude());
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                addressList = geocoder.getFromLocation(userLastLocation.getLatitude(), userLastLocation.getLongitude(), 1);
                if (addressList != null && addressList.size() > 0) {
                    System.out.println("Your Last Location : " + addressList.get(0).getCountryName().toLowerCase());
                    String countryName = addressList.get(0).getCountryName().toLowerCase();
                    insertCountryNameToSql(countryName);
                    System.out.println(countryName);
                    intent = new Intent(getApplicationContext(), SelectDate.class);
                    startActivity(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("NO LAST KNOWN ADDRESS");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1 & ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED & grantResults.length > 0 ){
            userLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            userLastKnownLocation = new LatLng(userLastLocation.getLatitude(),userLastLocation.getLongitude());
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                addressList = geocoder.getFromLocation(userLastLocation.getLatitude(), userLastLocation.getLongitude(), 1);
                if (addressList != null && addressList.size() > 0) {
                    System.out.println("Your Last Location : " + addressList.get(0).getCountryName().toLowerCase());
                    String countryName = addressList.get(0).getCountryName().toLowerCase();
                    insertCountryNameToSql(countryName);
                    System.out.println(countryName);
                    intent = new Intent(getApplicationContext(), SelectDate.class);
                    startActivity(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("NO LAST KNOWN ADDRESS");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void insertCountryNameToSql(String countryName){
        try{
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("LocationDatabase",MODE_PRIVATE,null);
            String sqlQuery = "INSERT INTO countries(country) VALUES (?)";
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sqlQuery);
            sqLiteStatement.bindString(1,countryName);
            sqLiteStatement.executeInsert();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
