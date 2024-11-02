package com.example.weather.model

import com.example.weather.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class FakeWeatherRemoteDataSource : WeatherRemoteDataSource {
    override suspend fun getOneCallWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<OneCallWeather> {
        TODO("Not yet implemented")
    }

}
