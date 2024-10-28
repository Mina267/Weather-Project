package com.example.weather.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale

class LocationHandler(private val context: Context, private val fusedLocationProviderClient: FusedLocationProviderClient) {

    private val REQUEST_LOCATION_CODE = 100

    /* Check if location permissions are granted */
    fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /* Request location permissions if needed */
    fun requestPermissions(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), REQUEST_LOCATION_CODE
        )
    }

    /* Check if location services (GPS or Network) are enabled */
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /* Open location settings if services are disabled */
    fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    /* Method to get a single accurate location update and invoke a callback with the result */
    @SuppressLint("MissingPermission")
    fun getSingleAccurateLocation(onLocationRetrieved: (latitude: Double, longitude: Double) -> Unit) {
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationRetrieved(location.latitude, location.longitude)
                } else {
                    Log.e("LocationError", "Failed to get accurate location")
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationError", "Error occurred: ${e.message}")
            }
    }

    /* Convert latitude and longitude to address */
    fun getAddress(lat: Double, lon: Double): String {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val currentAddress = StringBuilder("")
                if (!address.subAdminArea.isNullOrBlank()) currentAddress.append(address.subAdminArea)
                return currentAddress.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    /* Request continuous */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(onLocationRetrieved: (latitude: Double, longitude: Double) -> Unit) {
        val locationRequest = LocationRequest.Builder(0).apply {
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        }.build()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        onLocationRetrieved(location.latitude, location.longitude)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }
}
