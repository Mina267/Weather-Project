package com.example.weather.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectionStatus {
    fun isNetworkAvailable(): Boolean
    fun registerNetworkCallback()
    fun unregisterNetworkCallback()
    val isNetworkAvailable: StateFlow<Boolean>

}