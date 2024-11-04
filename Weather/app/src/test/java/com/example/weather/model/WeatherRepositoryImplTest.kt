package com.example.weather.model


import FakeWeatherLocalDatasource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.db.WeatherLocalDataSource
import com.example.weather.network.NetworkConnectionStatus
import com.example.weather.network.WeatherRemoteDataSource
import com.example.weather.sharedpreference.SharedPreferenceDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class WeatherRepositoryImplTest {


    private lateinit var weatherLocalDatasource: WeatherLocalDataSource
    private lateinit var weatherRemoteDataSource: WeatherRemoteDataSource
    private lateinit var weatherRepo: WeatherRepository
    private lateinit var sharedPreference: SharedPreferenceDataSource
    private lateinit var networkStatus: NetworkConnectionStatus

    @Before
    fun setUp() {
        weatherLocalDatasource = FakeWeatherLocalDatasource()
        weatherRemoteDataSource = FakeWeatherRemoteDataSource()
        sharedPreference = FakeSharedPreference()
        networkStatus = FakeNetworkConnectionStatus(MutableStateFlow(true))

        weatherRepo = WeatherRepositoryImpl.getInstance(weatherRemoteDataSource, weatherLocalDatasource, sharedPreference, networkStatus)
    }

    @Test
    fun insertFavourite_checkFavourite() = runTest {
        // Given
        val favourites = Favourites(1.0, 2.0)

        // When
        weatherRepo.insertFavourite(favourites)
        val favouritesList = weatherRepo.getAllFavourites().first()

        // Then
        assertThat(favouritesList.contains(favourites), `is`(true))


    }

    @Test
    fun insertAlert_checkAlert() = runTest {
        // Given
        val alert = AlertsData(1, 1.0, 2.0)

        // When
        weatherRepo.insertAlert(alert)
        val alertList = weatherRepo.getAlertByTime(1)

        // Then
        assertThat(alertList, `is`(alert))

    }



}