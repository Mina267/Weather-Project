package com.example.weather.model

import android.util.Log
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.example.weather.db.WeatherLocalDataSource
import com.example.weather.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.List
import kotlin.math.log


class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val preferences: SharedPreferenceDataSourceImpl
) : WeatherRepository {

    companion object {
        private const val WEATHER_DATA_EXPIRATION_TIME = 15 * 60 * 1000

        @Volatile
        private var INSTANCE: WeatherRepositoryImpl? = null
        fun getInstance(
            remoteDataSource: WeatherRemoteDataSource,
            localDataSource: WeatherLocalDataSource,
            preferences: SharedPreferenceDataSourceImpl
        ): WeatherRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepositoryImpl(remoteDataSource, localDataSource, preferences).also { INSTANCE = it }
            }
        }
    }





    private suspend fun fetchLocalWeather(
        latitude: Double,
        longitude: Double
    ): OneCallWeather? {
        val roundedLat = roundToDecimal(latitude, 1)
        val roundedLon = roundToDecimal(longitude, 1)
        val tempUnit = preferences.getTempUnit() ?: "metric"
        val language = preferences.getLanguage() ?: "en"
        val windSpeedUnit = preferences.getWindSpeedUnit() ?: "metric"
        val minTimestamp = System.currentTimeMillis() - WEATHER_DATA_EXPIRATION_TIME

        return localDataSource.getStoredLocalWeather(
            roundedLat, roundedLon, language, windSpeedUnit, tempUnit, minTimestamp
        ).firstOrNull()
    }

    private suspend fun fetchAndCacheRemoteWeather(
        latitude: Double,
        longitude: Double
    ): OneCallWeather {
        val currentTime = System.currentTimeMillis()
        val roundedLat = roundToDecimal(latitude, 1)
        val roundedLon = roundToDecimal(longitude, 1)
        val tempUnit = preferences.getTempUnit() ?: "metric"
        val language = preferences.getLanguage() ?: "en"
        val windSpeedUnit = preferences.getWindSpeedUnit() ?: "metric"


        val remoteWeather = remoteDataSource.getOneCallWeather(latitude, longitude, tempUnit, language).first()
        remoteWeather.lastUpdated = currentTime
        localDataSource.insertWeather(
            remoteWeather.copy(
                lat = roundedLat,
                lon = roundedLon,
                lang = language,
                wind = windSpeedUnit,
                units = tempUnit
            )
        )
        return remoteWeather
    }

    override suspend fun getWeather(latitude: Double, longitude: Double): Flow<OneCallWeather> =
        flow {
            val localWeather = fetchLocalWeather(latitude, longitude)
            if (localWeather == null) {
                emit(fetchAndCacheRemoteWeather(latitude, longitude))
            } else {
                Log.i("WeatherCheck", "Return cached data last updated at: ${localWeather.lastUpdated}")
                emit(localWeather)
            }
        }

    override suspend fun getWeather(): Flow<OneCallWeather> {
        val (latitude, longitude) = preferences.getActiveLocation()
        return getWeather(latitude, longitude)
    }
/*
    override suspend fun getCacheLocalWeather(
        latitude: Double,
        longitude: Double
    ): Flow<OneCallWeather> = flow {
        val cachedWeather = fetchLocalWeather(latitude, longitude)
        if (cachedWeather != null) {
            emit(cachedWeather)
        } else {
            // Optionally handle a null case if needed, like emitting an error or logging
        }
    }*/


    override suspend fun getCacheLocalWeather(): Flow<OneCallWeather> = flow {
        var lat: Double;
        var lon: Double;
        if (getPreferredLocationSource()) {
            lat = getActiveNetworkLocation().first
            lon = getActiveNetworkLocation().second

        }
        else
        {
            lat = getActiveLocation().first
            lon = getActiveLocation().second

        }

        val cachedWeather = fetchLocalWeather(lat, lon)
        Log.i("WeatherCheck", "getCacheLocalWeather: " + "${cachedWeather?.current?.temp}")
        if (cachedWeather != null) {
            emit(cachedWeather)
        } else {

        }
    }

    override suspend fun getAllFavourites(): Flow<List<Favourites>> {

        return localDataSource.getAllFavourites()
    }
    override suspend fun insertFavourite(favourite: Favourites) {
        favourite.lat = roundToDecimal(favourite.lat, 1)
        favourite.lon = roundToDecimal(favourite.lon, 1)
        localDataSource.insertFavourite(favourite)
    }

    override suspend fun deleteFavourite(lat: Double, lon: Double) {
        localDataSource.deleteFavourite(lat, lon)
    }







    override fun setActiveLocation(longitude: Double, latitude: Double) { preferences.setActiveLocation(longitude, latitude) }
    override fun setActiveNetworkLocation(longitude: Double, latitude: Double) { preferences.setActiveNetworkLocation(longitude, latitude) }
    override fun setPreferredLocationSource(isGps: Boolean) { preferences.setLocationSource(isGps) }
    override fun setPreferredTempUnit(tempUnit: String) { preferences.setTempUnit(tempUnit) }
    override fun setPreferredWindSpeedUnit(windSpeedUnit: String) { preferences.setWindSpeedUnit(windSpeedUnit) }
    override fun setPreferredLanguage(language: String) { preferences.setLanguage(language) }


    override fun getPreferredLocationSource(): Boolean = preferences.isGpsLocation()
    override fun getPreferredTempUnit(): String? = preferences.getTempUnit()
    override fun getPreferredWindSpeedUnit(): String? = preferences.getWindSpeedUnit()
    override fun getPreferredLanguage(): String? = preferences.getLanguage()
    override fun getActiveLocation(): Pair<Double, Double> { return preferences.getActiveLocation() }
    override fun getActiveNetworkLocation(): Pair<Double, Double> { return preferences.getActiveNetworkLocation() }




    private fun convertToEnglishNumber(input: String): String {
        val format = NumberFormat.getInstance(Locale.ENGLISH)
        return format.parse(input)?.toString() ?: input
    }

    private fun roundToDecimal(value: Double, places: Int): Double {
        val formattedValue = String.format(Locale.ENGLISH, "%.${5}f", value)
        val normalizedValue = convertToEnglishNumber(formattedValue)
        return normalizedValue.toDouble()
    }
}

