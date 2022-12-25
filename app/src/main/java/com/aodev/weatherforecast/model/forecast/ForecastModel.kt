package com.aodev.weatherforecast.model.forecast

data class ForecastModel(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WeatherData>,
    val message: Int
)