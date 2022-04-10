package com.ignation.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ignation.weatherapp.network.model.currentweather.WeatherResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setViewModel() {
        viewModel = WeatherViewModel()
    }

    @Test
    fun setResponseReturnsTemperature100_1() {
        val mock = mockk<WeatherResponse>()
        every { mock.main.temp } returns 100.1
        viewModel.setResponse(mock)

        assertEquals(100.1, viewModel.response.value!!.main.temp, 0.0)
    }

    @Test
    fun convertKelvinToCelsiusForPositive() {
        val mock = mockk<WeatherResponse>()
        every { mock.main.temp } returns 280.45
        viewModel.setResponse(mock)
        val display = viewModel.convertKelvinToCelsius()

        assertEquals("Temperature display is wrong","+7", display)
    }

    @Test
    fun convertKelvinToCelsiusForNegative() {
        val mock = mockk<WeatherResponse>()
        every { mock.main.temp } returns 250.77
        viewModel.setResponse(mock)
        val display = viewModel.convertKelvinToCelsius()

        assertEquals("Temperature display is wrong","-22", display)
    }

    @Test
    fun convertKelvinToCelsiusFor0() {
        val mock = mockk<WeatherResponse>()
        every { mock.main.temp } returns 273.15
        viewModel.setResponse(mock)
        val display = viewModel.convertKelvinToCelsius()

        assertEquals("Temperature display is wrong","+0", display)
    }
}