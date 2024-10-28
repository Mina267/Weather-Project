package com.example.weather.db

import androidx.room.TypeConverter
import com.example.weather.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConverterOnCall {

    @TypeConverter
    fun convertCurrentToString(current: Current) = Gson().toJson(current)

    @TypeConverter
    fun convertStringToCurrent(currentString: String) = Gson().fromJson(currentString,Current::class.java)

    @TypeConverter
    fun  listHourlyToString (value:List<Hourly>): String = Gson().toJson(value)

    @TypeConverter
    fun StringToHourlyList(value: String) = Gson().fromJson(value, Array<Hourly>::class.java).toList()

    @TypeConverter
    fun  listDailyToString (value:List<Daily>) = Gson().toJson(value)

    @TypeConverter
    fun stringToDailyList(value: String) = Gson().fromJson(value, Array<Daily>::class.java).toList()


    @TypeConverter
    fun listWeatherToString(value: List<Weather>) = Gson().toJson(value)

    @TypeConverter
    fun stringToWeatherList(value: String) = Gson().fromJson(value, Array<Weather>::class.java).toList()
}
