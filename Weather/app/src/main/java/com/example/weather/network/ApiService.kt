package com.example.weather.network

import com.example.weather.model.CurrentWeather
import com.example.weather.model.FiveDaysForecast
import com.example.weather.model.OneCallWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY ="834e51dc83e257a4be3163fdb23980a8"
const val EXCLUDE = "minutely,alerts"

interface ApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String ,
        @Query("lang") lang: String ,
    )
            : Response<CurrentWeather>

    @GET("data/2.5/forecast")
    suspend fun getFiveDaysForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    )
            : Response<FiveDaysForecast>

    @GET("data/3.0/onecall")
    suspend fun getAlertForWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY,
        @Query("exclude") exclude: String = EXCLUDE,
        @Query("units") units: String,
        @Query("lang") lang: String
    ):Response<OneCallWeather>
}