package com.ignation.weatherapp

import com.ignation.weatherapp.network.model.WeatherResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeatherApiService {
    //@GET("?lat={lat}&lon={lon}&appid=$API_KEY")
    @GET("weather?lat=35&lon=139&appid=$API_KEY")
    suspend fun getWeather(
        //@Path("lat") lat: Double,
        //@Path("lon") lon: Double
    ): WeatherResponse
}

object WeatherApi {
    val retrofitService : WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
