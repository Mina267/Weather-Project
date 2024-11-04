package com.example.weather.ui.favourite

import com.example.weather.model.AlertsData
import com.example.weather.model.Favourites
import com.example.weather.model.OneCallWeather
import com.example.weather.model.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherRepository : WeatherRepository {

    var isGps: Boolean = false
    var tempUnit: String? = null
    var windSpeedUnit: String? = null
    var language: String? = null
    private val favourites = MutableStateFlow<List<Favourites>>(emptyList())
    private var _activeLocation: Pair<Double, Double> = 0.0 to 0.0
    private var _activeNetworkLocation: Pair<Double, Double> = 0.0 to 0.0

    override fun setPreferredLocationSource(isGps: Boolean) {
        this.isGps = isGps
    }

    override fun setPreferredTempUnit(tempUnit: String) {
        this.tempUnit = tempUnit
    }

    override fun setPreferredWindSpeedUnit(windSpeedUnit: String) {
        this.windSpeedUnit = windSpeedUnit
    }

    override fun setPreferredLanguage(language: String) {
        this.language = language
    }

    override fun setActiveLocation(longitude: Double, latitude: Double) {
        _activeLocation = Pair(longitude, latitude)
    }

    override fun getActiveLocation(): Pair<Double, Double> {
        return _activeLocation
    }

    override fun setActiveNetworkLocation(longitude: Double, latitude: Double) {
        _activeNetworkLocation = Pair(longitude, latitude)
    }

    override fun getActiveNetworkLocation(): Pair<Double, Double> {
        return _activeNetworkLocation
    }

    override suspend fun getAllFavourites(): Flow<List<Favourites>> = favourites

    override suspend fun insertFavourite(favourite: Favourites) {
        val updatedList = favourites.value.toMutableList().apply { add(favourite) }
        favourites.value = updatedList
    }

    override suspend fun deleteFavourite(lat: Double, lon: Double) {
        val updatedList = favourites.value.toMutableList().apply {
            removeIf { it.lat == lat && it.lon == lon }
        }
        favourites.value = updatedList
    }

    override fun getPreferredLocationSource(): Boolean {
        return isGps
    }

    override fun getPreferredTempUnit(): String? {
        return tempUnit
    }

    override fun getPreferredWindSpeedUnit(): String? {
        return windSpeedUnit
    }

    override fun getPreferredLanguage(): String? {
        return language
    }

    override suspend fun getWeather(latitude: Double, longitude: Double): Flow<OneCallWeather> {
        // Stub implementation, update with actual mock data if needed
        TODO()
    }

    override suspend fun getWeather(): Flow<OneCallWeather> {
        // Stub implementation, update with actual mock data if needed
        TODO()
    }

    override suspend fun getCacheLocalWeather(): Flow<Result<OneCallWeather>> {
        TODO("Not yet implemented")
    }


    override suspend fun insertAlert(alert: AlertsData) {
        // Stub implementation, update with actual mock data if needed
        TODO()
    }

    override fun getAllAlerts(): Flow<List<AlertsData>> {
        // Stub implementation, update with actual mock data if needed
        TODO()
    }

    override suspend fun getAlertByTime(time: Long): AlertsData? {
        // Stub implementation, update with actual mock data if needed
        TODO()
    }

    override suspend fun deleteAlert(time: Long) {
        // Stub implementation, update with actual mock data if needed
        TODO()
    }

    override suspend fun deleteAllAlerts() {
        // Stub implementation, update with actual mock data if needed
        TODO()
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}
