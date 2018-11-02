package mchehab.com.googlemapslocationdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationResultListener {

    private final int PERMISSION_REQUEST = 1000;
    private final int LOCATION_REQUEST_CODE = 2000;

    private GoogleMap googleMap;
    private LocationHandler locationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationHandler = new LocationHandler(this, this,
                LOCATION_REQUEST_CODE, PERMISSION_REQUEST);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        locationHandler.getUserLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST){
            boolean isGranted = true;
            for (int i = 0; i < permissions.length; i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    isGranted = false;
                    break;
                }
            }
            if (!isGranted){
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Cannot display location without enabling permission")
                        .setPositiveButton("Ok", (dialog, which) -> locationHandler.getUserLocation())
                        .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                        return;
            }
            locationHandler.getUserLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                locationHandler.getUserLocation();
            }else{
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please enable location in order to display it on map")
                        .setPositiveButton("Enable", (dialog, which) -> locationHandler.getUserLocation())
                        .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                        .create()
                        .show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void getLocation(Location location) {
        googleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(latLng);
        googleMap.addMarker(marker);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }
}