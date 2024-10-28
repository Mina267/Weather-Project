package com.example.weather.ui.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.Favourites
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherFavourites = MutableStateFlow<List<Favourites>>(emptyList())
    val weatherFavouritesMain: StateFlow<List<Favourites>> = _weatherFavourites

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

    fun deleteFavourite(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.deleteFavourite(lat, lon)
        }
    }






}

class MainViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}