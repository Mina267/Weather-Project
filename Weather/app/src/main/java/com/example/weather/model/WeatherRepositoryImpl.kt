package com.example.weather.model

import android.util.Log
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.example.weather.db.WeatherLocalDataSource
import com.example.weather.network.NetworkConnectionStatus
import com.example.weather.network.NetworkConnectionStatusImpl
import com.example.weather.network.WeatherRemoteDataSource
import com.example.weather.sharedpreference.SharedPreferenceDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
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

    // Define a CoroutineScope for network and database operations
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        networkStatus.registerNetworkCallback(
            object : NetworkConnectionStatusImpl.NetworkChangeListener {
                override fun onNetworkAvailable() {
                    repositoryScope.launch {
                        if (preferences.isGpsLocation())
                        {
                            Log.i("network", "onNetworkAvailable: isGpsLocation")
                            val (latitude, longitude) = preferences.getActiveNetworkLocation()
                            getWeather(latitude, longitude)
                            insertFavourite(Favourites(latitude, longitude))
                        }
                        else {
                            Log.i("network", "onNetworkAvailable: noGPS")
                            val (latitude, longitude) = preferences.getActiveLocation()
                            insertFavourite(Favourites(latitude, longitude))

                            getWeather()
                        }

                    }
                }

                override fun onNetworkLost() {
                    Log.i("WeatherCheck", "onNetworkLost")
                }
            }
        )
    }


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
        val tempUnit = preferences.getTempUnit() ?: "metric"
        val language = preferences.getLanguage() ?: "en"
        val windSpeedUnit = preferences.getWindSpeedUnit() ?: "metric"



        val remoteWeather = remoteDataSource.getOneCallWeather(latitude, longitude, tempUnit, language).first()
        remoteWeather.lastUpdated = currentTime

        Log.i("WindSpeed", "windSpeedUnit: " + "${windSpeedUnit}")
        Log.i("WindSpeed", "tempUnit: " + "${tempUnit}")
        Log.i("WindSpeed", "remoteWeather.current.windSpeed: " + "${remoteWeather.current.windSpeed}")

        /* Convert wind speed to the preferred unit for saving */
        remoteWeather.current.windSpeed = when {
            (tempUnit == "metric" || tempUnit == "standard") && windSpeedUnit == "imperial" -> remoteWeather.current.windSpeed * 2.237
            tempUnit == "imperial" && windSpeedUnit == "metric" -> remoteWeather.current.windSpeed / 2.237
            else -> remoteWeather.current.windSpeed
        }



        Log.i("WindSpeed", "convertedWindSpeed: " + "${remoteWeather.current.windSpeed}")


        localDataSource.insertWeather(
            remoteWeather.copy(
                lat = roundedLat,
                lon = roundedLon,
                lang = language,
                wind = windSpeedUnit,
                units = tempUnit,

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
                        val newLocalWeather = fetchLocalWeather(latitude, longitude)

                        if (newLocalWeather != null) {
                            emit(newLocalWeather)
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
            Log.e("WeatherCheck", "No cached data available")
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

    override suspend fun insertAlert(alert: AlertsData) {
        localDataSource.insertAlert(alert)
    }

    override fun getAllAlerts(): Flow<List<AlertsData>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun getAlertByTime(time: Long): AlertsData? {
        return localDataSource.getAlertByTime(time)
    }

    override suspend fun deleteAlert(time: Long) {
        localDataSource.deleteAlert(time)
    }

    override suspend fun deleteAllAlerts() {
        localDataSource.deleteAllAlerts()
    }

    override fun clear() {
        networkStatus.unregisterNetworkCallback()
        repositoryScope.cancel()

    }

}

