package com.example.weather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.model.AlertsData
import com.example.weather.model.Favourites
import com.example.weather.model.OneCallWeather
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: OneCallWeather)

    @Query("SELECT * FROM OneCallWeather WHERE lat = :latitude AND lon = :longitude LIMIT 1")
    fun getWeather(latitude: Double, longitude: Double): Flow<OneCallWeather>

    @Query("DELETE FROM OneCallWeather WHERE lat = :latitude AND lon = :longitude")
    suspend fun deleteWeather(latitude: Double, longitude: Double)


    @Query("SELECT * FROM OneCallWeather WHERE lat = :latitude AND lon = :longitude  AND lang = :lang AND wind = :wind AND units = :units  AND lastUpdated > :minTimestamp LIMIT 1")
    fun getValidWeather(latitude: Double, longitude: Double, lang: String, wind : String, units : String ,minTimestamp: Long): Flow<OneCallWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: Favourites)

    @Query("SELECT * FROM favourites_places")
    fun getAllFavourites(): Flow<List<Favourites>>

    @Query("DELETE FROM favourites_places WHERE lat = :lat AND lon = :lon")
    suspend fun deleteFavourite(lat: Double, lon: Double)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertsData)

    @Query("SELECT * FROM alerts_table")
    fun getAllAlerts(): Flow<List<AlertsData>>

    @Query("SELECT * FROM alerts_table WHERE time = :time LIMIT 1")
    suspend fun getAlertByTime(time: Long): AlertsData?

    @Query("DELETE FROM alerts_table WHERE time = :time")
    suspend fun deleteAlert(time: Long)

    @Query("DELETE FROM alerts_table")
    suspend fun deleteAllAlerts()
}
