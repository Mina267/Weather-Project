package com.example.weather.sharedpreference

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceDataSourceImpl private constructor(context: Context) :
    SharedPreferenceDataSource {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val SHARED_PREF_NAME: String = "SharedPrefSetting"

        private const val KEY_LOCATION_SOURCE = "location_source"
        private const val KEY_TEMP_UNIT = "temp_unit"
        private const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE_GPS = "longitudeGPS"
        private const val KEY_LATITUDE_GPS = "latitudeGPS"

        @Volatile
        private var instance: SharedPreferenceDataSourceImpl? = null

        fun getInstance(context: Context): SharedPreferenceDataSourceImpl {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferenceDataSourceImpl(context).also { instance = it }
            }
        }
    }

    override fun setLocationSource(isGps: Boolean) {
        editor.putBoolean(KEY_LOCATION_SOURCE, isGps).apply()
    }

    override fun isGpsLocation(): Boolean {
        return sharedPreferences.getBoolean(KEY_LOCATION_SOURCE, true)
    }

    override fun setTempUnit(tempUnit: String) {
        editor.putString(KEY_TEMP_UNIT, tempUnit).apply()
    }

    override fun getTempUnit(): String? {
        return sharedPreferences.getString(KEY_TEMP_UNIT, "metric")
    }

    override fun setWindSpeedUnit(windSpeedUnit: String) {
        editor.putString(KEY_WIND_SPEED_UNIT, windSpeedUnit).apply()
    }

    override fun getWindSpeedUnit(): String? {
        return sharedPreferences.getString(KEY_WIND_SPEED_UNIT, "metric")
    }

    override fun setLanguage(language: String) {
        editor.putString(KEY_LANGUAGE, language).apply()
    }

    override fun getLanguage(): String? {
        return sharedPreferences.getString(KEY_LANGUAGE, "en")
    }

    override fun setActiveLocation(longitude: Double, latitude: Double) {
        editor.putString(KEY_LONGITUDE, longitude.toString()).apply()
        editor.putString(KEY_LATITUDE, latitude.toString()).apply()
    }

    override fun getActiveLocation(): Pair<Double, Double> {
        val longitude = sharedPreferences.getString(KEY_LONGITUDE, "30.0444")?.toDouble() ?: 30.0444
        val latitude = sharedPreferences.getString(KEY_LATITUDE, "31.2357")?.toDouble() ?: 31.2357
        return Pair(longitude, latitude)
    }

    override fun setActiveNetworkLocation(longitude: Double, latitude: Double) {
        editor.putString(KEY_LONGITUDE_GPS, longitude.toString()).apply()
        editor.putString(KEY_LATITUDE_GPS, latitude.toString()).apply()
    }

    override fun getActiveNetworkLocation(): Pair<Double, Double> {
        val longitude = sharedPreferences.getString(KEY_LONGITUDE_GPS, "30.0444")?.toDouble() ?: 30.0444
        val latitude = sharedPreferences.getString(KEY_LATITUDE_GPS, "31.2357")?.toDouble() ?: 31.2357
        return Pair(longitude, latitude)
    }

    override fun clearAllData() {
        editor.clear().apply()
    }
}
