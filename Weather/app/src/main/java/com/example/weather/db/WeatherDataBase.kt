package com.example.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.model.AlertsData
import com.example.weather.model.Favourites
import com.example.weather.model.OneCallWeather


@Database(entities =     arrayOf(OneCallWeather::class, Favourites::class, AlertsData::class), version = 1)
@TypeConverters(ConverterOnCall::class)
abstract class WeatherDataBase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDAO

    companion object {
        @Volatile
        private var INSTANCE: WeatherDataBase? = null

        fun getInstance(ctx: Context): WeatherDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    WeatherDataBase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}