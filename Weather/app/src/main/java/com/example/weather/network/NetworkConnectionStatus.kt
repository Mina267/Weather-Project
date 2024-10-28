package com.example.weather.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkConnectionStatus private constructor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isRegistered = false

    companion object {
        @Volatile
        private var instance: NetworkConnectionStatus? = null

        fun getInstance(context: Context): NetworkConnectionStatus {
            return instance ?: synchronized(this) {
                instance ?: NetworkConnectionStatus(context.applicationContext).also { instance = it }
            }
        }
    }

    fun isNetworkAvailable(): Boolean {
        val network: Network? = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false
    }

    fun registerNetworkCallback(listener: NetworkChangeListener) {
        if (!isRegistered) {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build()

            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    listener.onNetworkAvailable()
                }

                override fun onLost(network: Network) {
                    listener.onNetworkLost()
                }
            }

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
            isRegistered = true
        }
    }

    fun unregisterNetworkCallback() {
        if (isRegistered) {
            connectivityManager.unregisterNetworkCallback(networkCallback!!)
            isRegistered = false
        }
    }

    interface NetworkChangeListener {
        fun onNetworkAvailable()
        fun onNetworkLost()
    }
}
