package com.example.weather.model

import kotlin.collections.List

import androidx.annotation.NonNull
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "OneCallWeather", primaryKeys = ["lat", "lon", "lang", "units", "wind"])

data class OneCallWeather (

    @NonNull @SerializedName("lat"             ) var lat   : Double,
    @NonNull @SerializedName("lon"             ) var lon   : Double,
    @SerializedName("timezone"        ) var timezone       : String,
    @SerializedName("timezone_offset" ) var timezoneOffset : Int,
    @SerializedName("current"         ) var current        : Current,
    @SerializedName("hourly"          ) var hourly         : List<Hourly>,
    @SerializedName("daily"           ) var daily          : List<Daily>,
    @NonNull var lang   : String,
    @NonNull var units   : String,
    @NonNull var wind   : String,
    var lastUpdated: Long
)


data class Temp (

    @SerializedName("day"   ) var day   : Double,
    @SerializedName("min"   ) var min   : Double,
    @SerializedName("max"   ) var max   : Double,
    @SerializedName("night" ) var night : Double,
    @SerializedName("eve"   ) var eve   : Double,
    @SerializedName("morn"  ) var morn  : Double

)





data class Alerts (

    @SerializedName("sender_name" ) var senderName  : String,
    @SerializedName("event"       ) var event       : String,
    @SerializedName("start"       ) var start       : Int,
    @SerializedName("end"         ) var end         : Int,
    @SerializedName("description" ) var description : String,
    @SerializedName("tags"        ) var tags        : List<String>

)


data class Current (

    @SerializedName("dt"         ) var dt         : Int,
    @SerializedName("sunrise"    ) var sunrise    : Int,
    @SerializedName("sunset"     ) var sunset     : Int,
    @SerializedName("temp"       ) var temp       : Double,
    @SerializedName("feels_like" ) var feelsLike  : Double,
    @SerializedName("pressure"   ) var pressure   : Int,
    @SerializedName("humidity"   ) var humidity   : Int,
    @SerializedName("dew_point"  ) var dewPoint   : Double,
    @SerializedName("uvi"        ) var uvi        : Double,
    @SerializedName("clouds"     ) var clouds     : Int,
    @SerializedName("visibility" ) var visibility : Int,
    @SerializedName("wind_speed" ) var windSpeed  : Double,
    @SerializedName("wind_deg"   ) var windDeg    : Int,
    @SerializedName("wind_gust"  ) var windGust   : Double,
    @SerializedName("weather"    ) var weather    : List<Weather>

)



data class Daily (

    @SerializedName("dt"         ) var dt        : Int,
    @SerializedName("sunrise"    ) var sunrise   : Int,
    @SerializedName("sunset"     ) var sunset    : Int,
    @SerializedName("moonrise"   ) var moonrise  : Int,
    @SerializedName("moonset"    ) var moonset   : Int,
    @SerializedName("moon_phase" ) var moonPhase : Double,
    @SerializedName("summary"    ) var summary   : String,
    @SerializedName("temp"       ) var temp      : Temp,
    @SerializedName("feels_like" ) var feelsLike : FeelsLike,
    @SerializedName("pressure"   ) var pressure  : Int,
    @SerializedName("humidity"   ) var humidity  : Int,
    @SerializedName("dew_point"  ) var dewPoint  : Double,
    @SerializedName("wind_speed" ) var windSpeed : Double,
    @SerializedName("wind_deg"   ) var windDeg   : Int,
    @SerializedName("wind_gust"  ) var windGust  : Double,
    @SerializedName("weather"    ) var weather   : List<Weather>,
    @SerializedName("clouds"     ) var clouds    : Int,
    @SerializedName("pop"        ) var pop       : Double,
    @SerializedName("rain"       ) var rain      : Double,
    @SerializedName("uvi"        ) var uvi       : Double              

)



data class FeelsLike (

    @SerializedName("day"   ) var day   : Double,
    @SerializedName("night" ) var night : Double,
    @SerializedName("eve"   ) var eve   : Double,
    @SerializedName("morn"  ) var morn  : Double   

)



data class Hourly (

    @SerializedName("dt"         ) var dt         : Int,
    @SerializedName("temp"       ) var temp       : Double,
    @SerializedName("feels_like" ) var feelsLike  : Double,
    @SerializedName("pressure"   ) var pressure   : Int,
    @SerializedName("humidity"   ) var humidity   : Int,
    @SerializedName("dew_point"  ) var dewPoint   : Double,
    @SerializedName("uvi"        ) var uvi        : Double,
    @SerializedName("clouds"     ) var clouds     : Int,
    @SerializedName("visibility" ) var visibility : Int,
    @SerializedName("wind_speed" ) var windSpeed  : Double,
    @SerializedName("wind_deg"   ) var windDeg    : Int,
    @SerializedName("wind_gust"  ) var windGust   : Double,
    @SerializedName("weather"    ) var weather    : List<Weather>,
    @SerializedName("pop"        ) var pop        : Double              

)


data class Minutely (

    @SerializedName("dt"            ) var dt            : Int,
    @SerializedName("precipitation" ) var precipitation : Int   

)