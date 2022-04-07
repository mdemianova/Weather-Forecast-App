package com.ignation.weatherapp

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ignation.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

const val TAG: String = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var userSettings: UserSettings
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userSettings = UserSettings(this)

        sharedPreferences = getSharedPreferences(
        getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val savedLocation = sharedPreferences.getString(LAST_LOCATION, DEFAULT_VALUE)!!
        if (savedLocation != DEFAULT_VALUE) {
            performSearchByName(savedLocation)
        }

        binding.showLocation.setOnClickListener {
            editLocation()
        }

        viewModel.response.observe(this) {
            sharedPreferences.edit().putString(LAST_LOCATION, it.name).apply()
        }
    }

    override fun onStart() {
        super.onStart()
        getLocation()
    }

    private fun getLocation() {
        if (userSettings.checkPermission(this)) {
            Log.d(TAG, "getLocation: Has permission")
            if (userSettings.isLocatingEnabled()) {
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
            userSettings.requestPermissions(this)
        }
    }

    private fun bindViews() {
        binding.manualLocation.visibility = View.GONE
        binding.showLocation.visibility = View.VISIBLE
        binding.showLocation.text = viewModel.response.value?.name
        binding.degrees.text = getString(R.string.degree_text, viewModel.convertKelvinToCelsius())
        binding.manualLocation.text.clear()
    }

    private fun editLocation() {
        if (binding.showLocation.visibility == View.VISIBLE) {
            binding.showLocation.visibility = View.GONE
        }
        binding.manualLocation.visibility = View.VISIBLE
        binding.searchButton.visibility = View.VISIBLE

        clickSearchButton()
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
        binding.manualLocation.visibility = View.VISIBLE
        binding.searchButton.visibility = View.VISIBLE
        clickSearchButton()
    }

    private fun clickSearchButton() {
        binding.searchButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            binding.progressBar.visibility = View.VISIBLE
            val cityName = binding.manualLocation.text.toString()
            performSearchByName(cityName)
        }
    }

    private fun performSearchByName(cityName: String) {
        var isError = false
        CoroutineScope(Dispatchers.Main).launch {
            try {
                viewModel.response.value = viewModel.getResponseByName(cityName)
            } catch (e: HttpException) {
                isError = true
            }
            if (!isError) {
                binding.progressBar.visibility = View.GONE
                binding.searchButton.visibility = View.INVISIBLE
                bindViews()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "No information about $cityName",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}