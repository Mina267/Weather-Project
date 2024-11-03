package com.example.weather.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkConnectionStatusImpl private constructor(context: Context) : NetworkConnectionStatus {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // This StateFlow will emit the network status changes
    private val _isNetworkAvailable = MutableStateFlow(isNetworkAvailable())
    override val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    companion object {
        @Volatile
        private var instance: NetworkConnectionStatusImpl? = null

        fun getInstance(context: Context): NetworkConnectionStatusImpl {
            return instance ?: synchronized(this) {
                instance ?: NetworkConnectionStatusImpl(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun isNetworkAvailable(): Boolean {
        val network: Network? = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }

    override fun registerNetworkCallback(listener: NetworkChangeListener) {
        if (networkCallback == null) {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    listener.onNetworkAvailable()
                    _isNetworkAvailable.value = true
                }

                override fun onLost(network: Network) {
                    listener.onNetworkLost()
                    _isNetworkAvailable.value = false
                }
            }

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
        }
    }

    override fun unregisterNetworkCallback() {
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
        }
    }

    interface NetworkChangeListener {
        fun onNetworkAvailable()
        fun onNetworkLost()
    }
}
