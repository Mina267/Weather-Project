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

    /**
     *  setUp function
     * */
    @Before
    fun setUp() {
        repo = FakeWeatherRepository()
        favViewModel = FavouriteViewModel(repo)
    }

    @Test
    fun setPreferredLocationSource_UpdatePreferredLocationSourceAndCheck ()  {
        favViewModel.setLocationSource(true)
        val isGps = favViewModel.getPreferredLocationSource()
        assertThat(isGps, `is`(true))
    }

    @Test
    fun setPreferredTempUnit_UpdatePreferredTempUnitAndCheck()  {
        favViewModel.setPreferredTempUnit("Celsius")
        val tempUnit = favViewModel.getPreferredTempUnit()
        assertThat(tempUnit, `is`("Celsius"))

    }

    @Test
    fun setPreferredWindSpeedUnit_UpdateWindSpeedUnitAndCheck() {
        favViewModel.setPreferredWindSpeedUnit("km/h")
        val windSpeedUnit = favViewModel.getPreferredWindSpeedUnit()
        assertThat(windSpeedUnit, `is`("km/h"))

    }

    @Test
    fun setPreferredLanguage_UpdatePreferredLanguageAndCheck()  {
        favViewModel.setPreferredLanguage("English")
        val language = favViewModel.getPreferredLanguage()
        assertThat(language, `is`("English"))

    }

    @Test
    fun setActiveLocation_UpdatePreferredActiveLocationAndCheck() = runTest  {
        favViewModel.setActiveLocation(1.0, 2.0)
        val activeLocation = favViewModel.getActiveLocation()
        assertThat(activeLocation, `is`(Pair(1.0, 2.0)))


    }


    @Test
    fun insertFavourite_UpdateFavouriteAndCheckList() = runTest  {
        val favourite = Favourites(1.0, 2.0)
        favViewModel.insertFavourite(favourite)
        val favouritesList = favViewModel.weatherFavourites.first()
        assertThat(favouritesList.contains(favourite), `is`(true))


    }



}