package com.example.weather.sharedpreference

interface SharedPreferenceDataSource {
    fun setLocationSource(isGps: Boolean)
    fun isGpsLocation(): Boolean
    fun setTempUnit(tempUnit: String)
    fun getTempUnit(): String?
    fun setWindSpeedUnit(windSpeedUnit: String)
    fun getWindSpeedUnit(): String?
    fun setLanguage(language: String)
    fun getLanguage(): String?
    fun setActiveLocation(longitude: Double, latitude: Double)
    fun getActiveLocation(): Pair<Double, Double>
    fun setActiveNetworkLocation(longitude: Double, latitude: Double)
    fun getActiveNetworkLocation(): Pair<Double, Double>
    fun clearAllData()
}