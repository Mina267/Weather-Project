package com.example.weather.ui.favourite


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.Favourites
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherFavourites = MutableStateFlow<List<Favourites>>(emptyList())
    val weatherFavourites: StateFlow<List<Favourites>> = _weatherFavourites

    init {
        getWeatherFavourites()
    }

    fun getWeatherFavourites() {
        viewModelScope.launch {
            repository.getAllFavourites().collect {
                _weatherFavourites.value = it
            }
        }

    }
    fun setLocation(latitude: Double, longitude: Double) {
        repository.setActiveLocation(latitude, longitude)
    }

    fun setNetworkLocation(latitude: Double, longitude: Double) {
        repository.setActiveNetworkLocation(latitude, longitude)
    }
    fun getPreferredLocationSource(): Boolean {
        return repository.getPreferredLocationSource()
    }

    fun getPreferredTempUnit(): String? {
        return repository.getPreferredTempUnit()
    }

    fun getPreferredWindSpeedUnit(): String? {
        return repository.getPreferredWindSpeedUnit()
    }

    fun getPreferredLanguage(): String? {
        return repository.getPreferredLanguage()
    }

    fun getActiveLocation(): Pair<Double, Double> {
        return repository.getActiveLocation()
    }

    fun getActiveNetworkLocation(): Pair<Double, Double> {
        return repository.getActiveNetworkLocation()
    }

    fun setPreferredTempUnit(tempUnit: String) {
        repository.setPreferredTempUnit(tempUnit)
    }

    fun setPreferredWindSpeedUnit(windSpeedUnit: String) {
        repository.setPreferredWindSpeedUnit(windSpeedUnit)
    }

    fun setPreferredLanguage(language: String) {

        repository.setPreferredLanguage(language)
    }

    fun setActiveLocation(longitude: Double, latitude: Double) {
        repository.setActiveLocation(longitude, latitude)
    }


    fun deleteFavourite(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.deleteFavourite(lat, lon)
        }
    }

    fun insertFavourite(favourite: Favourites) {
        viewModelScope.launch {
            repository.insertFavourite(favourite)
        }
    }
    fun setLocationSource(isGps: Boolean) {
        repository.setPreferredLocationSource(isGps)
    }






}

class FavouriteViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            return FavouriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}