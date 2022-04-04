package com.ignation.weatherapp

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ignation.weatherapp.network.WeatherApi
import com.ignation.weatherapp.network.model.WeatherResponse

const val DEGREE_DELTA = 273.15

class WeatherViewModel : ViewModel() {

    var currentLocation = MutableLiveData<Location>()

    suspend fun getResponse(location: Location): WeatherResponse {
        return WeatherApi.retrofitService.getWeather()
    }

    private fun convertKelvinToCelsius(tempKelvin: Double): Double {
        return tempKelvin - DEGREE_DELTA
    }

    fun showDenyMessage(context: Context) {
        Toast.makeText(context, "Please turn on GPS location", Toast.LENGTH_SHORT).show()
    }
}