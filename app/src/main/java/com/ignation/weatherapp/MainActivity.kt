package com.ignation.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ignation.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val PERMISSIONS_RQ = 100
const val TAG: String = "WeatherLog"

class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var locationManager: LocationManager

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLocation()
    }

    private fun getLocation() {
        if (checkPermission()) {
            Log.d(TAG, "getLocation: Has permission")
            if (isLocatingEnabled()) {
                Log.d(TAG, "getLocation: Locating enabled")
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.response.value = viewModel.getResponse(location)
                        Log.d(TAG, "getLocation: ${viewModel.response.value!!.name}")
                    }
                }
            } else {
                Log.d(TAG, "getLocation: GPS is off")
                viewModel.showDenyMessage(this)
            }
        } else {
            Log.d(TAG, "getLocation: No permission")
            requestPermissions()
        }
    }

    private fun bindViews() {
        binding.degrees.text = viewModel.convertKelvinToCelsius().toString()
    }

    private fun checkPermission(): Boolean {
        val finePermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarsePermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return finePermission || coarsePermission
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (!checkPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSIONS_RQ
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: called")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_RQ && permissions.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    private fun isLocatingEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}