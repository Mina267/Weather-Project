package com.example.weather.ui.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.AlertsData
import com.example.weather.model.Favourites
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel (private val repository: WeatherRepository, private val alarmHandler: AlarmHandler) : ViewModel()  {
    private val _weatherAlerts = MutableStateFlow<List<AlertsData>>(emptyList())
    val weatherAlerts: StateFlow<List<AlertsData>> = _weatherAlerts

    init {
        getWeatherAlerts()
    }

    fun getWeatherAlerts() {
        viewModelScope.launch {
            repository.getAllAlerts().collect {
                _weatherAlerts.value = it
            }
        }
    }

    fun insertAlert(alertTimeInMillis: Long) {
        viewModelScope.launch {
            val (latitude, longitude) = if (repository.getPreferredLocationSource()) {
                repository.getActiveNetworkLocation()
            } else {
                repository.getActiveLocation()
            }

            val alert = AlertsData(alertTimeInMillis, latitude, longitude)

            repository.insertAlert(alert)
            alarmHandler.schedule(alert)
        }
    }


    fun deleteAlert(alert: AlertsData) {
        viewModelScope.launch {
            repository.deleteAlert(alert.time)
            alarmHandler.cancel(alert)

        }
    }

    fun deleteAllAlerts() {
        viewModelScope.launch {
            repository.deleteAllAlerts()
        }
    }

    

}


class AlertViewModelFactory(
    private val repository: WeatherRepository,
    private val alarmHandler: AlarmHandler
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(repository, alarmHandler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}