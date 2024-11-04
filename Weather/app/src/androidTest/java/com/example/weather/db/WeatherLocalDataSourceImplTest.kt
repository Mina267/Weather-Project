package com.example.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weather.model.AlertsData
import com.example.weather.model.Favourites
import com.example.weather.model.OneCallWeather
import com.example.weather.model.CurrentWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceImplTest {

    private lateinit var localDatasource: WeatherLocalDataSource
    private lateinit var database: WeatherDataBase

    // Rule to Execute Synchronous using Architecture Components
    @JvmField
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Setup the database
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries().build()

        localDatasource = WeatherLocalDataSourceImpl(database.weatherDao())
    }

    @Test
    fun insertFavourite_insertsAndRetrievesFavouriteSuccessfully() = runTest {
        // Given
        val favourite = Favourites( 37.7749, -122.4194)

        // When
        localDatasource.insertFavourite(favourite)
        val favouritesList = localDatasource.getAllFavourites().first()

        // Then
        assertThat(favouritesList.size, greaterThan(0))
        assertThat(favouritesList[0], `is`(favourite))
    }

    @Test
    fun insertAlert_insertsAndRetrievesAlertSuccessfully() = runTest {
        // Given
        val alert = AlertsData(
            time = System.currentTimeMillis(),
            latitude = 37.7749,
            longitude = -122.4194
        )

        // When ; Call the insert function
        localDatasource.insertAlert(alert)

        // When: Retrieve the alert
        val retrievedAlert = localDatasource.getAlertByTime(alert.time)

        // Then
        assertThat(retrievedAlert, `is`(alert))
    }



}
