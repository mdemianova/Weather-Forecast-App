package com.ignation.weatherapp

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ignation.weatherapp.network.WeatherApi
import com.ignation.weatherapp.network.model.WeatherResponse

const val DEGREE_DELTA = 273.15

class WeatherViewModel : ViewModel() {

    val response = MutableLiveData<WeatherResponse>()

    suspend fun getResponse(location: Location): WeatherResponse {
        return WeatherApi.retrofitService.getWeatherByCoordinates(location.latitude, location.longitude, API_KEY)
    }

    fun convertKelvinToCelsius(): Double {
        return response.value!!.main.temp.minus(DEGREE_DELTA)
    }

    fun showDenyMessage(context: Context) {
        Toast.makeText(context, "Please turn on GPS location", Toast.LENGTH_SHORT).show()
    }
}