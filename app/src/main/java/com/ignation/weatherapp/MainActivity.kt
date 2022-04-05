package com.ignation.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ignation.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
                    binding.progressBar.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.response.value = viewModel.getResponseByLocation(location)
                        binding.progressBar.visibility = View.GONE
                        bindViews()
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
        binding.manualLocation.visibility = View.GONE
        binding.showLocation.visibility = View.VISIBLE
        binding.showLocation.text = viewModel.response.value?.name
        binding.degrees.text = getString(R.string.degree_text, viewModel.convertKelvinToCelsius())
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
            } else {
                enterLocation()
            }
        }
    }

    private fun enterLocation() {
        Toast.makeText(this, "Enter the city name", Toast.LENGTH_SHORT).show()
        binding.searchButton.visibility = View.VISIBLE

        binding.searchButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            binding.progressBar.visibility = View.VISIBLE
            val cityName = binding.manualLocation.text.toString()
            var isError = false
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    viewModel.response.value = viewModel.getResponseByName(cityName)
                } catch (e: HttpException) {
                    isError = true
                }
                if (!isError) {
                    Log.d(TAG, "enterLocation: Temp ${viewModel.response.value!!.main.temp}")
                    Log.d(TAG, "enterLocation: Name ${viewModel.response.value!!.name}")
                    binding.progressBar.visibility = View.GONE
                    it.visibility = View.INVISIBLE
                    bindViews()
                } else {
                    Toast.makeText(this@MainActivity, "No information about $cityName", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }

            }
        }
    }

    private fun isLocatingEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}