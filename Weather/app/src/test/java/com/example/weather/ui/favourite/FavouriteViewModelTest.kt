package com.example.weather.ui.favourite


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.model.Favourites

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)

class FavouriteViewModelTest {

    private lateinit var repo: FakeWeatherRepository
    private lateinit var favViewModel: FavouriteViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    /*
     *  setUp function
     */
    @Before
    fun setUp() {
        repo = FakeWeatherRepository()
        favViewModel = FavouriteViewModel(repo)
    }

    @Test
    fun setPreferredLocationSource_UpdatePreferredLocationSourceAndCheck ()  {
        // Given
        favViewModel.setLocationSource(true)
        // When
        val isGps = favViewModel.getPreferredLocationSource()
        // Then
        assertThat(isGps, `is`(true))
    }

    @Test
    fun setPreferredTempUnit_UpdatePreferredTempUnitAndCheck()  {
        // Given
        favViewModel.setPreferredTempUnit("Celsius")
        // When
        val tempUnit = favViewModel.getPreferredTempUnit()
        // Then
        assertThat(tempUnit, `is`("Celsius"))

    }

    @Test
    fun setPreferredWindSpeedUnit_UpdateWindSpeedUnitAndCheck() {
        // Given
        favViewModel.setPreferredWindSpeedUnit("km/h")
        // When
        val windSpeedUnit = favViewModel.getPreferredWindSpeedUnit()
        // Then
        assertThat(windSpeedUnit, `is`("km/h"))

    }

    @Test
    fun setPreferredLanguage_UpdatePreferredLanguageAndCheck()  {
        // Given
        favViewModel.setPreferredLanguage("English")
        // When
        val language = favViewModel.getPreferredLanguage()
        // Then
        assertThat(language, `is`("English"))

    }

    @Test
    fun setActiveLocation_UpdatePreferredActiveLocationAndCheck() = runTest  {
        // Given
        favViewModel.setActiveLocation(1.0, 2.0)
        // When
        val activeLocation = favViewModel.getActiveLocation()
        // Then
        assertThat(activeLocation, `is`(Pair(1.0, 2.0)))


    }


    @Test
    fun insertFavourite_UpdateFavouriteAndCheckList() = runTest  {
        // Given
        val favourite = Favourites(1.0, 2.0)
        // When
        favViewModel.insertFavourite(favourite)
        val favouritesList = favViewModel.weatherFavourites.first()
        // Then
        assertThat(favouritesList.contains(favourite), `is`(true))


    }



}