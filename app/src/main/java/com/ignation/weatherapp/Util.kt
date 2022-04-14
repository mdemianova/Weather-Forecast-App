package com.ignation.weatherapp

import android.widget.ImageView
import java.text.SimpleDateFormat

const val LAST_LOCATION = "last_location"
const val DEFAULT_VALUE = ""
const val PERMISSIONS_RQ = 100

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
    val sdf = SimpleDateFormat("d MMM")
    return sdf.format(long * 1000)
}

fun convertDegreeToString(degree: Double): String {
    var result = "${degree.toInt()}\u2103"
    if (degree >= 0.0) {
        result = "+$result"
    }
    return result
}