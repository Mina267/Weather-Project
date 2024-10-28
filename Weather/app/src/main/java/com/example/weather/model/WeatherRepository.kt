package com.example.weather.model

import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): Flow<OneCallWeather>
    suspend fun getCacheLocalWeather(): Flow<OneCallWeather>
    suspend fun getAllFavourites(): Flow<List<Favourites>>
    suspend fun insertFavourite(favourite: Favourites)
    suspend fun deleteFavourite(lat: Double, lon: Double)
    fun setPreferredLocationSource(isGps: Boolean)
    fun setPreferredTempUnit(tempUnit: String)
    fun setPreferredWindSpeedUnit(windSpeedUnit: String)
    fun setPreferredLanguage(language: String)
    fun getPreferredLocationSource(): Boolean
    fun getPreferredTempUnit(): String?
    fun getPreferredWindSpeedUnit(): String?
    fun getPreferredLanguage(): String?
    suspend fun getWeather(): Flow<OneCallWeather>;
    fun getActiveLocation(): Pair<Double, Double>
    fun setActiveLocation(longitude: Double, latitude: Double)

    fun setActiveNetworkLocation(longitude: Double, latitude: Double)
    fun getActiveNetworkLocation(): Pair<Double, Double>
}