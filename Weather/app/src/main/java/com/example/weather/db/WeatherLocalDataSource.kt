package com.example.weather.db

import com.example.weather.model.AlertsData
import com.example.weather.model.Favourites
import com.example.weather.model.OneCallWeather
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun getStoredLocalWeather(latitude: Double, longitude: Double, lang: String, wind : String, units : String , minTimestamp: Long): Flow<OneCallWeather>
    suspend fun insertWeather(oneCallWeather: OneCallWeather)
    suspend fun deleteWeatherForLocation(latitude: Double, longitude: Double)
    suspend fun insertFavourite(favourite: Favourites)
    suspend fun getAllFavourites(): Flow<List<Favourites>>
    suspend fun deleteFavourite(lat: Double, lon: Double)


    suspend fun insertAlert(alert: AlertsData)
    fun getAllAlerts(): Flow<List<AlertsData>>
    suspend fun getAlertByTime(time: Long): AlertsData?
    suspend fun deleteAlert(time: Long)
    suspend fun deleteAllAlerts()
}