package com.example.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime


@Entity(tableName = "alerts_table")
data class AlertsData(
    @PrimaryKey
    val time: Long,
    var latitude: Double,
    var longitude: Double
) : Serializable
