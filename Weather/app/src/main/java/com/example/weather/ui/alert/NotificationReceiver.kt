package com.example.weather.ui.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weather.R
import com.example.weather.db.WeatherDataBase
import com.example.weather.db.WeatherLocalDataSourceImpl
import com.example.weather.model.AlertsData
import com.example.weather.model.WeatherRepositoryImpl
import com.example.weather.network.ApiService
import com.example.weather.network.NetworkConnectionStatusImpl
import com.example.weather.network.RetrofitHelper
import com.example.weather.network.WeatherRemoteDataSourceImpl
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Check and set preferred language locale
        val localizedContext = applyPreferredLanguage(context)

        val alertData = intent.getSerializableExtra(ALERT_REQUEST_KEY) as? AlertsData
        Log.i("alert", "onReceive: $alertData")

        localizedContext?.let { myContext ->
            val repository = WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitHelper.getInstance().create(ApiService::class.java)),
                WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(myContext).weatherDao()),
                SharedPreferenceDataSourceImpl.getInstance(myContext),
                NetworkConnectionStatusImpl.getInstance(myContext)
            )

            CoroutineScope(Dispatchers.IO).launch {
                alertData?.let {
                    repository.deleteAlert(it.time)
                    repository.getWeather(it.latitude, it.longitude).collectLatest { weather ->
                        val weatherState = weather.current.weather.firstOrNull()?.description
                            ?: myContext.getString(R.string.no_weather_data)
                        val temperature = weather.current.temp.toInt()
                        val notificationMessage = myContext.getString(R.string.notification_message, weatherState, temperature)
                        sendNotification(myContext, notificationMessage)
                    }
                }
            }
        }
    }

    private fun sendNotification(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "weather_alert_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, context.getString(R.string.weather_alert), NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.weather_alert))
            .setContentText(message)
            .setSmallIcon(R.drawable.notifications_24px)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(2, notification)
    }

    private fun applyPreferredLanguage(context: Context): Context {
        val preferredLanguage = SharedPreferenceDataSourceImpl.getInstance(context).getLanguage()
        return if (preferredLanguage == "ar") {
            changeLocale(context, "ar")
        } else {
            context
        }
    }

    private fun changeLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    companion object {
        const val ALERT_REQUEST_KEY = "alert_request_key"
    }
}
