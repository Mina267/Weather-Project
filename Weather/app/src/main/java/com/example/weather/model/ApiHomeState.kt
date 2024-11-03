package com.example.weather.model


sealed class ApiHomeState {
    data class Success(val data: OneCallWeather) : ApiHomeState()
    data class Failure(val error: Throwable) : ApiHomeState()
    data object Loading : ApiHomeState()
}
