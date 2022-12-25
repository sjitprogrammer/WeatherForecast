package com.aodev.weatherforecast.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aodev.weatherforecast.model.Weather
import com.aodev.weatherforecast.model.forecast.ForecastModel
import com.aodev.weatherforecast.repository.ForeCastRepository
import com.aodev.weatherforecast.repository.WeatherRepository
import com.aodev.weatherforecast.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel
@Inject
constructor(
    private val repository: WeatherRepository,
    private val forecastRepository: ForeCastRepository
) : ViewModel() {
    private val _response = MutableLiveData<Weather>()
    private val _responseForecast = MutableLiveData<ForecastModel>()
    val weatherError = MutableLiveData<Boolean>()
    val weatherLoading = MutableLiveData<Boolean>()
    val forecastError = MutableLiveData<Boolean>()
    val forecastLoading = MutableLiveData<Boolean>()
    val weatherResponse: LiveData<Weather>
        get() = _response
    val forecastResponse: LiveData<ForecastModel>
        get() = _responseForecast


    private fun getWeather(city: String) = viewModelScope.launch {
        weatherLoading.value = true
        repository.getWeather(city, Constants.API_KEY, Constants.UNIT).let { response ->
            weatherLoading.value = false
            if (response.isSuccessful) {
                weatherError.value = false
                _response.postValue(response.body())
            } else {
                weatherError.value = true
            }
        }
    }

    private fun getForecast(city: String) = viewModelScope.launch {
        forecastLoading.value = true
        forecastRepository.getForecast(city, Constants.API_KEY, Constants.UNIT).let { response ->
            forecastLoading.value = false
            if (response.isSuccessful) {
                forecastError.value = false
                _responseForecast.postValue(response.body())
            } else {
                forecastError.value = true
            }
        }
    }

    fun searchCity(city: String) {
        Log.e("city", city)
        getWeather(city)
        getForecast(city)
    }

}