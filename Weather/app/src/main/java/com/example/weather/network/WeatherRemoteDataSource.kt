package com.example.weather.network

import com.example.weather.model.OneCallWeather
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    suspend fun getOneCallWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<OneCallWeather>
}