package com.example.weather.model

import android.util.Log
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.example.weather.db.WeatherLocalDataSource
import com.example.weather.network.NetworkConnectionStatus
import com.example.weather.network.WeatherRemoteDataSource
import com.example.weather.sharedpreference.SharedPreferenceDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.List
import kotlin.math.log

const val ROUND_VALUE : Int = 3; 
class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val preferences: SharedPreferenceDataSource,
    private val networkStatus: NetworkConnectionStatus

    ) : WeatherRepository {

    companion object {
        private const val WEATHER_DATA_EXPIRATION_TIME = 15 * 60 * 1000

        @Volatile
        private var INSTANCE: WeatherRepositoryImpl? = null
        fun getInstance(
            remoteDataSource: WeatherRemoteDataSource,
            localDataSource: WeatherLocalDataSource,
            preferences: SharedPreferenceDataSource,
            networkStatus: NetworkConnectionStatus
        ): WeatherRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepositoryImpl(remoteDataSource, localDataSource, preferences, networkStatus).also { INSTANCE = it }
            }
        }
    }





    private suspend fun fetchLocalWeather(
        latitude: Double,
        longitude: Double,
        minTimestamp: Long = System.currentTimeMillis() - WEATHER_DATA_EXPIRATION_TIME
    ): OneCallWeather? {
        val roundedLat = roundToDecimal(latitude, ROUND_VALUE)
        val roundedLon = roundToDecimal(longitude, ROUND_VALUE)
        val tempUnit = preferences.getTempUnit() ?: "metric"
        val language = preferences.getLanguage() ?: "en"
        val windSpeedUnit = preferences.getWindSpeedUnit() ?: "metric"

        return localDataSource.getStoredLocalWeather(
            roundedLat, roundedLon, language, windSpeedUnit, tempUnit, minTimestamp
        ).firstOrNull()
    }

    private suspend fun fetchAndCacheRemoteWeather(
        latitude: Double,
        longitude: Double
    ): OneCallWeather {
        val currentTime = System.currentTimeMillis()
        val roundedLat = roundToDecimal(latitude, ROUND_VALUE)
        val roundedLon = roundToDecimal(longitude, ROUND_VALUE)
        val tempUnit = preferences.getTempUnit() ?: "metric" // "metric", "imperial", or "standard"
        val language = preferences.getLanguage() ?: "en"
        val windSpeedUnit = preferences.getWindSpeedUnit() ?: "metric" // Desired saving unit: "metric" or "imperial"


        // Fetch weather data using tempUnit and language parameters
        val remoteWeather = remoteDataSource.getOneCallWeather(latitude, longitude, tempUnit, language).first()
        remoteWeather.lastUpdated = currentTime

        Log.i("WindSpeed", "windSpeedUnit: " + "${windSpeedUnit}")
        Log.i("WindSpeed", "tempUnit: " + "${tempUnit}")
        Log.i("WindSpeed", "remoteWeather.current.windSpeed: " + "${remoteWeather.current.windSpeed}")

        // Convert wind speed to the preferred unit for saving
        remoteWeather.current.windSpeed = when {
            // Fetch in m/s but save as miles/hour
            (tempUnit == "metric" || tempUnit == "standard") && windSpeedUnit == "imperial" -> remoteWeather.current.windSpeed * 2.237
            // Fetch in miles/hour but save as m/s
            tempUnit == "imperial" && windSpeedUnit == "metric" -> remoteWeather.current.windSpeed / 2.237
            // No conversion needed if fetched and saved units match
            else -> remoteWeather.current.windSpeed
        }



        Log.i("WindSpeed", "convertedWindSpeed: " + "${remoteWeather.current.windSpeed}")


        // Insert weather data into local storage with the converted wind speed
        localDataSource.insertWeather(
            remoteWeather.copy(
                lat = roundedLat,
                lon = roundedLon,
                lang = language,
                wind = windSpeedUnit, // Save in the desired wind speed unit
                units = tempUnit,     // Save the temperature unit setting as a reference

            )
        )
        return remoteWeather
    }


    override suspend fun getWeather(latitude: Double, longitude: Double): Flow<OneCallWeather> =
        flow {
            val localWeather = fetchLocalWeather(latitude, longitude)
            if (localWeather == null) {
                networkStatus.isNetworkAvailable.collect { isNetworkAvailable ->
                    if (isNetworkAvailable) {
                        fetchAndCacheRemoteWeather(latitude, longitude)
                        val newLocalWeather = fetchLocalWeather(latitude, longitude) // Re-fetch from local after caching

                        if (newLocalWeather != null) {
                            emit(newLocalWeather)  // Emit the cached local data
                        } else {
                            Log.e("WeatherCheck", "Data fetch failed; no data to display.")
                        }
                    } else {
                        getCacheLocalWeather()
                        Log.e("WeatherCheck", "No network available and no cached data")
                    }
                }
            } else {
                Log.i("WeatherCheck", "Returning cached data last updated at: ${localWeather.lastUpdated}")
                emit(localWeather)
            }
        }

    override suspend fun getWeather(): Flow<OneCallWeather> {
        val (latitude, longitude) = preferences.getActiveLocation()
        return getWeather(latitude, longitude)
    }


    override suspend fun getCacheLocalWeather(): Flow<OneCallWeather> = flow {
        var lat: Double;
        var lon: Double;
        if (getPreferredLocationSource()) {
            lat =  roundToDecimal(getActiveNetworkLocation().first, ROUND_VALUE)
            lon =  roundToDecimal(getActiveNetworkLocation().second, ROUND_VALUE)


            Log.i("WeatherCheck", "getActiveNetworkLocation: " + "${lat}")

        }
        else
        {
            lat = getActiveLocation().first
            lon = getActiveLocation().second
            Log.i("WeatherCheck", "getActiveLocation: " + "${lat}")

        }

        val cachedWeather = fetchLocalWeather(lat, lon, 0)
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
        favourite.lat = roundToDecimal(favourite.lat, ROUND_VALUE)
        favourite.lon = roundToDecimal(favourite.lon, ROUND_VALUE)
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
        val formattedValue = String.format(Locale.ENGLISH, "%.${places}f", value)
        val normalizedValue = convertToEnglishNumber(formattedValue)
        return normalizedValue.toDouble()
    }
}

