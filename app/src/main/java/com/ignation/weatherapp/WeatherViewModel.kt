package com.ignation.weatherapp

import android.content.Context
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ignation.weatherapp.network.WeatherApi
import com.ignation.weatherapp.network.model.WeatherResponse

const val DEGREE_DELTA = 273.15

class WeatherViewModel : ViewModel() {

    val response = MutableLiveData<WeatherResponse>()

    suspend fun getResponseByLocation(location: Location): WeatherResponse {
        return WeatherApi.retrofitService.getWeatherByCoordinates(location.latitude, location.longitude, API_KEY)
    }

    suspend fun getResponseByName(name: String): WeatherResponse {
        return WeatherApi.retrofitService.getWeatherByCityName(name, API_KEY)
    }

    fun convertKelvinToCelsius(): String {
        val degree = response.value!!.main.temp.minus(DEGREE_DELTA)
        var result = String.format("%.1f", degree)
        if (degree >= 0.0) {
            result = "+$result"
        }
        return result
    }

    fun showDenyMessage(context: Context) {
        Toast.makeText(context, "Please turn on GPS location", Toast.LENGTH_SHORT).show()
    }
}