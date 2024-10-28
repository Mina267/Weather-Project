package com.example.weather.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.Favourites
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _unit = MutableLiveData<String>().apply {
        value = repository.getPreferredTempUnit() ?: "metric"
    }
    val unit: LiveData<String> get() = _unit

    private val _language = MutableLiveData<String>().apply {
        value = repository.getPreferredLanguage() ?: "en"
    }
    val language: LiveData<String> get() = _language

    private val _locationSource = MutableLiveData<Boolean>().apply {
        value = repository.getPreferredLocationSource()
    }
    val locationSource: LiveData<Boolean> get() = _locationSource

    private val _windSpeedUnit = MutableLiveData<String>().apply {
        value = repository.getPreferredWindSpeedUnit() ?: "metric"
    }
    val windSpeedUnit: LiveData<String> get() = _windSpeedUnit

    fun setUnit(value: String) {
        _unit.value = value
        repository.setPreferredTempUnit(value)
    }

    fun setLanguage(value: String) {
        _language.value = value
        repository.setPreferredLanguage(value)
    }

    fun setLocationSource(isGps: Boolean) {
        _locationSource.value = isGps
        repository.setPreferredLocationSource(isGps)
    }

    fun setWindSpeedUnit(value: String) {
        _windSpeedUnit.value = value
        repository.setPreferredWindSpeedUnit(value)
    }



}
class SettingsViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
