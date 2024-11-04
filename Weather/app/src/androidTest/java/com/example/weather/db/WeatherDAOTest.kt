package com.example.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather.model.Favourites
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WeatherDAOTest {
    private lateinit var database: WeatherDataBase
    private lateinit var weatherDao: WeatherDAO

    /*
     *  Rule to suspend function calls
     */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    /*
     * Setup the database
     */
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).build()

        weatherDao = database.weatherDao()
    }


    /*
     *  Close the database
     */
    @After
    fun closeDb() = database.close()

    @Test
    fun addFavourite_insertsFavouriteSuccessfully() = runTest {
        // Given
        val favourite = Favourites(37.7749, -122.4194)

        // When
        weatherDao.insertFavourite(favourite)
        val retrievedFavourite = weatherDao.getFavourite(37.7749, -122.4194)

        // Then
        assertThat(retrievedFavourite, IsEqual(favourite))

    }

    @Test
    fun removeFavourite_deletesFavouriteSuccessfully() = runTest {
        // Given
        val favourite = Favourites(37.7749, -122.4194)
        weatherDao.insertFavourite(favourite)

        // When
        weatherDao.deleteFavourite(37.7749, -122.4194)
        val deletedFavourite = weatherDao.getFavourite(37.7749, -122.4194)

        // Then
        assertThat(deletedFavourite, IsEqual(null))

    }


}