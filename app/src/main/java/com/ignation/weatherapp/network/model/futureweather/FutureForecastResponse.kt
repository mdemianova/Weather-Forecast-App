package com.ignation.weatherapp.network.model.futureweather

data class FutureForecastResponse(
    val daily: List<Daily>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)