package com.example.weather.ui.alert
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.weather.model.AlertsData
const val ALERT_REQUEST_KEY = "alert_request_key"
class AlarmHandlerImpl(
    private val context: Context
) : AlarmHandler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlertsData) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(ALERT_REQUEST_KEY, item)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time,  // Use item.time directly as the exact time
            pendingIntent
        )
    }


    override fun cancel(item: AlertsData) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
}
