package com.example.weather.model


sealed class ApiState {
    data class Success(val data: OneCallWeather) : ApiState()
    data class Failure(val error: Throwable) : ApiState()
    data object Loading : ApiState()
}
