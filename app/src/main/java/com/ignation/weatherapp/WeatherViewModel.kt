package com.ignation.weatherapp

import android.location.Location
import androidx.lifecycle.ViewModel
import com.ignation.weatherapp.network.model.WeatherResponse

const val DEGREE_DELTA = 273.15

class WeatherViewModel : ViewModel() {

    suspend fun getResponse(location: Location): WeatherResponse {
        return WeatherApi.retrofitService.getWeather()
    }

    private fun convertKelvinToCelsius(tempKelvin: Double): Double {
        return tempKelvin - DEGREE_DELTA
    }

    fun showDeniedMessage() {
    }
}