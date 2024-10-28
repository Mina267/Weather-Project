package com.example.weather.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.Favourites
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: WeatherRepository) : ViewModel() {





    fun insertFavourite(favourite: Favourites) {
        viewModelScope.launch {
            repository.insertFavourite(favourite)
        }
    }

    fun setLocationSource(isGps: Boolean) {
        repository.setPreferredLocationSource(isGps)
    }

    fun setLocation(latitude: Double, longitude: Double) {
        repository.setActiveLocation(latitude, longitude)
    }

}

class SearchViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
