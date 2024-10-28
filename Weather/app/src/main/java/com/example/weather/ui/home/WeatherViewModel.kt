package com.example.weather.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.Current
import com.example.weather.model.OneCallWeather
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableStateFlow<OneCallWeather?>(null)
    val weatherData: StateFlow<OneCallWeather?> = _weatherData

    init {
        getCachedWeather()
    }
    fun refreshWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.getWeather(latitude, longitude).collect {
                _weatherData.value = it
            }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            repository.getWeather().collect {
                _weatherData.value = it
            }
        }

    }

    fun getCachedWeather() {
        viewModelScope.launch {
            repository.getCacheLocalWeather().collect {
                _weatherData.value = it
            }
        }
    }


    fun getLanguage(): String? {
        return repository.getPreferredLanguage()
    }

    fun getLocationSource(): Boolean {
        return repository.getPreferredLocationSource()
    }

    fun getActiveLocation() : Pair<Double, Double> {

        return repository.getActiveLocation()
    }

    fun setActiveLocation(longitude: Double, latitude: Double) {
        repository.setActiveLocation(longitude, latitude)
    }

    fun setActiveNetworkLocation(longitude: Double, latitude: Double) {
        repository.setActiveNetworkLocation(longitude, latitude)
    }


    fun getActiveNetworkLocation() : Pair<Double, Double> {
        return repository.getActiveNetworkLocation()
    }



    fun setPreferredLocationSource(isGps: Boolean) {
        repository.setPreferredLocationSource(isGps)
    }


    fun setPreferredTempUnit(tempUnit: String) {
        repository.setPreferredTempUnit(tempUnit)
    }





    fun mapCurrentToWeatherInfo(current: Current): List<WeatherInfo> {
        return listOf(
            WeatherInfo.UVIndex(current.uvi),
            WeatherInfo.Humidity(current.humidity),
            WeatherInfo.Wind(current.windSpeed, current.windDeg),
            WeatherInfo.Pressure(current.pressure),
            WeatherInfo.DewPoint(current.dewPoint),
            WeatherInfo.Visibility(current.visibility)
        )
    }
}


class WeatherViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}