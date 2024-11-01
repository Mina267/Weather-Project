package com.example.weather.ui.alert

import com.example.weather.model.AlertsData

interface AlarmHandler {
    fun schedule(item: AlertsData)
    fun cancel(item: AlertsData)
}