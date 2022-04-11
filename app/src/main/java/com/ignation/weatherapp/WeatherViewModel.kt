package com.ignation.weatherapp

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ignation.weatherapp.network.WeatherApi
import com.ignation.weatherapp.network.model.currentweather.WeatherResponse

const val DEGREE_DELTA = 273.15

class WeatherViewModel : ViewModel() {

    private val _response = MutableLiveData<WeatherResponse>()
    val response: LiveData<WeatherResponse> = _response

    fun setResponse(weatherResponse: WeatherResponse) {
        _response.value = weatherResponse
    }

    suspend fun getResponseByLocation(location: Location): WeatherResponse {
        return WeatherApi.retrofitService.getWeatherByCoordinates(location.latitude, location.longitude, API_KEY)
    }

    suspend fun getResponseByName(name: String): WeatherResponse {
        return WeatherApi.retrofitService.getWeatherByCityName(name, API_KEY)
    }

    fun convertKelvinToCelsius(): String {
        val degree = _response.value!!.main.temp.minus(DEGREE_DELTA)
        var result = degree.toInt().toString()
        if (degree >= 0.0) {
            result = "+$result"
        }
        return result
    }
}