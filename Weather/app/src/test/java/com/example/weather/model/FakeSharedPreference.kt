package com.example.weather.model

import com.example.weather.sharedpreference.SharedPreferenceDataSource

class FakeSharedPreference : SharedPreferenceDataSource {
    override fun setLocationSource(isGps: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isGpsLocation(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setTempUnit(tempUnit: String) {
        TODO("Not yet implemented")
    }

    override fun getTempUnit(): String? {
        TODO("Not yet implemented")
    }

    override fun setWindSpeedUnit(windSpeedUnit: String) {
        TODO("Not yet implemented")
    }

    override fun getWindSpeedUnit(): String? {
        TODO("Not yet implemented")
    }

    override fun setLanguage(language: String) {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): String? {
        TODO("Not yet implemented")
    }

    override fun setActiveLocation(longitude: Double, latitude: Double) {
        TODO("Not yet implemented")
    }

    override fun getActiveLocation(): Pair<Double, Double> {
        TODO("Not yet implemented")
    }

    override fun setActiveNetworkLocation(longitude: Double, latitude: Double) {
        TODO("Not yet implemented")
    }

    override fun getActiveNetworkLocation(): Pair<Double, Double> {
        TODO("Not yet implemented")
    }

    override fun clearAllData() {
        TODO("Not yet implemented")
    }

}