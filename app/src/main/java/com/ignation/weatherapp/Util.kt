package com.ignation.weatherapp

import android.text.format.DateFormat
import android.widget.ImageView
import java.util.*

fun setImage(description: String, view: ImageView) {
    val image: Int = when (description) {
        "Clear" -> R.drawable.sun
        "Rain" -> R.drawable.rain
        "Smoke" -> R.drawable.fog
        "Mist" -> R.drawable.fog
        "Clouds" -> R.drawable.cloudy
        "Drizzle" -> R.drawable.drizzle
        "Thunderstorm" -> R.drawable.thunder
        "Tornado" -> R.drawable.wind

        else -> R.drawable.small_clouds
    }

    view.setImageResource(image)
}

fun convertLongToDate(long: Long): String {
    return DateFormat.format("d MMM", Date(long)).toString()
}