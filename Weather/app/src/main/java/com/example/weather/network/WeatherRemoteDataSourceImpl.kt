package com.example.weather.network

import com.example.weather.model.OneCallWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRemoteDataSourceImpl (private val apiService: ApiService) : WeatherRemoteDataSource {

    override suspend fun getOneCallWeather(latitude: Double, longitude: Double, units: String, lang: String): Flow<OneCallWeather> {
        return flow {
            val response = apiService.getAlertForWeather(latitude, longitude, units = units, lang = lang)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(it)
                }
            } else {
                throw Exception("Failed to fetch weather data")
            }

        }

    }


    companion object {
        @Volatile
        private var INSTANCE: WeatherRemoteDataSourceImpl? = null

        fun getInstance(apiService: ApiService): WeatherRemoteDataSourceImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRemoteDataSourceImpl(apiService).also { INSTANCE = it }
            }
        }
    }
}