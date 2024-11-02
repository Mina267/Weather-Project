package com.example.weather.model

import com.example.weather.network.NetworkConnectionStatus
import kotlinx.coroutines.flow.StateFlow

class FakeNetworkConnectionStatus(override val isNetworkAvailable: StateFlow<Boolean>) : NetworkConnectionStatus {
    override fun isNetworkAvailable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun registerNetworkCallback() {
        TODO("Not yet implemented")
    }

    override fun unregisterNetworkCallback() {
        TODO("Not yet implemented")
    }

}