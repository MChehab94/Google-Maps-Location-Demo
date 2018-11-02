package mchehab.com.kotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationResultListener {

    private val PERMISSION_REQUEST = 1000
    private val LOCATION_REQUEST_CODE = 2000

    private lateinit var googleMap: GoogleMap
    private lateinit var locationHandler: LocationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationHandler = LocationHandler(this, this, LOCATION_REQUEST_CODE, PERMISSION_REQUEST)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST){
            var isGranted = true
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    break
                }
            }
            if (!isGranted){
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Cannot display location without enabling permission")
                        .setPositiveButton("Ok") { dialog, which -> locationHandler.getUserLocation() }
                        .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                        .create()
                        .show()
                return
            }
            locationHandler.getUserLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                locationHandler.getUserLocation()
            }else{
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please enable location in order to display it on map")
                        .setPositiveButton("Enable") { dialog, which -> locationHandler.getUserLocation() }
                        .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                        .create()
                        .show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        locationHandler.getUserLocation()
    }

    @SuppressLint("MissingPermission")
    override fun getLocation(location: Location) {
        googleMap.isMyLocationEnabled = true
        val latLng = LatLng(location.latitude, location.longitude)
        val marker = MarkerOptions().position(latLng)
        googleMap.addMarker(marker)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f))
    }
}