package com.aodev.weatherforecast.repository

import com.aodev.weatherforecast.api.ApiService
import javax.inject.Inject

class ForeCastRepository
@Inject
constructor(private val apiService: ApiService) {
    suspend fun getForecast(cityName: String, appid: String, units: String) =
        apiService.getForecast(cityName, appid, units)
}