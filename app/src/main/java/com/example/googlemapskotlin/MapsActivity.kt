package com.example.googlemapskotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemapskotlin.databinding.ActivityMapsBinding
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(listener)
        // Add a marker in Sydney and move the camera
        /*
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
         */
        locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener=object:LocationListener{
            override fun onLocationChanged(location: Location) {
                mMap.clear()
                val currentLocation= LatLng(location.latitude,location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15f))
                val geocoder= Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val addressList= geocoder.getFromLocation(location.latitude,location.longitude,1)
                    if(addressList.size>0){
                        println(addressList.get(0).toString())
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            val lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(lastKnownLocation!=null){
                val lastKnownLatLng=LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnownLatLng).title("Last Known Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng,15f))
            }

            }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size>0){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    val listener= object:GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            val geocoder=Geocoder(this@MapsActivity, Locale.getDefault())
            if(p0!=null){
                var address=""
                try {
                    val addresssList=geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if(addresssList.size>0){
                        if(addresssList.get(0).thoroughfare!=null){
                            address += addresssList.get(0).thoroughfare
                        }
                        if(addresssList.get(0).subThoroughfare!=null){
                            address+=addresssList.get(0).subThoroughfare
                        }
                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(address))
            }
        }

    }
}