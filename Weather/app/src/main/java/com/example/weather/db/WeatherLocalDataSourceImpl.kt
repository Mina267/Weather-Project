package com.example.weather.db

import android.content.Context
import com.example.weather.model.AlertsData
import com.example.weather.model.Favourites
import com.example.weather.model.OneCallWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class WeatherLocalDataSourceImpl(private val weatherDAO: WeatherDAO): WeatherLocalDataSource {

    companion object {

        @Volatile
        private var INSTANCE: WeatherLocalDataSourceImpl? = null

        fun getInstance(context: Context): WeatherLocalDataSourceImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherLocalDataSourceImpl(
                    WeatherDataBase.getInstance(context).weatherDao()
                ).also { INSTANCE = it }
            }
        }
    }

    override suspend fun getStoredLocalWeather(latitude: Double, longitude: Double, lang: String, wind : String, units : String , minTimestamp: Long): Flow<OneCallWeather> {

        return weatherDAO.getValidWeather(latitude, longitude, lang, wind, units, minTimestamp)
    }

    override suspend fun insertWeather(oneCallWeather: OneCallWeather) {
        weatherDAO.insertWeather(oneCallWeather)
    }


    override suspend fun deleteWeatherForLocation(latitude: Double, longitude: Double) {
        weatherDAO.deleteWeather(latitude, longitude)
    }

    override suspend fun insertFavourite(favourite: Favourites) {
        weatherDAO.insertFavourite(favourite)
    }

    override suspend fun getAllFavourites(): Flow<List<Favourites>> {
        return weatherDAO.getAllFavourites()
    }

    override suspend fun deleteFavourite(lat: Double, lon: Double) {
        weatherDAO.deleteFavourite(lat, lon)
    }

    override suspend fun insertAlert(alert: AlertsData) {
        weatherDAO.insertAlert(alert)
    }

    override fun getAllAlerts(): Flow<List<AlertsData>> {
        return weatherDAO.getAllAlerts()
    }

    override suspend fun getAlertByTime(time: Long): AlertsData? {
        return weatherDAO.getAlertByTime(time)
    }

    override suspend fun deleteAlert(time: Long) {
        weatherDAO.deleteAlert(time)
    }

    override suspend fun deleteAllAlerts() {
        weatherDAO.deleteAllAlerts()
    }


}
