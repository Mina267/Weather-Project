package com.example.weather.model

import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): Flow<OneCallWeather>
    suspend fun getCacheLocalWeather(): Flow<Result<OneCallWeather>>
    suspend fun getAllFavourites(): Flow<List<Favourites>>
    suspend fun insertFavourite(favourite: Favourites)
    suspend fun deleteFavourite(lat: Double, lon: Double)
    suspend fun getWeather(): Flow<OneCallWeather>;

    fun setPreferredLocationSource(isGps: Boolean)
    fun setPreferredTempUnit(tempUnit: String)
    fun setPreferredWindSpeedUnit(windSpeedUnit: String)
    fun setPreferredLanguage(language: String)
    fun getPreferredLocationSource(): Boolean
    fun getPreferredTempUnit(): String?
    fun getPreferredWindSpeedUnit(): String?
    fun getPreferredLanguage(): String?
    fun getActiveLocation(): Pair<Double, Double>
    fun setActiveLocation(longitude: Double, latitude: Double)

    fun setActiveNetworkLocation(longitude: Double, latitude: Double)
    fun getActiveNetworkLocation(): Pair<Double, Double>
    suspend fun insertAlert(alert: AlertsData)
    fun getAllAlerts(): Flow<List<AlertsData>>
    suspend fun getAlertByTime(time: Long): AlertsData?
    suspend fun deleteAlert(time: Long)
    suspend fun deleteAllAlerts()
    fun clear()
}