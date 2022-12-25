package com.aodev.weatherforecast.repository

import com.aodev.weatherforecast.api.ApiService
import javax.inject.Inject

class WeatherRepository
@Inject
constructor(private val apiService: ApiService) {
    suspend fun getWeather(cityName: String, appid: String, units: String) =
        apiService.getCurrentWeather(cityName, appid, units)
}