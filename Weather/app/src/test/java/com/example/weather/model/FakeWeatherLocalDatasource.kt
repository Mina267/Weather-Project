import com.example.weather.db.WeatherLocalDataSource
import com.example.weather.model.AlertsData
import com.example.weather.model.Favourites
import com.example.weather.model.OneCallWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class FakeWeatherLocalDatasource : WeatherLocalDataSource {

    // In-memory storage for favorites and alerts
    private val favouritesList = mutableListOf<Favourites>()
    private val alertsMap = mutableMapOf<Long, AlertsData>()

    override suspend fun insertFavourite(favourite: Favourites) {
        favouritesList.add(favourite)
    }

    override suspend fun getAllFavourites(): Flow<List<Favourites>> {
        return flow { emit(favouritesList) }
    }

    override suspend fun insertAlert(alert: AlertsData) {
        alertsMap[alert.time] = alert
    }

    override suspend fun getAlertByTime(time: Long): AlertsData? {
        return alertsMap[time]
    }

    // Other methods can remain unimplemented or throw NotImplementedError if they're not needed for the test cases
    override suspend fun getStoredLocalWeather(
        latitude: Double,
        longitude: Double,
        lang: String,
        wind: String,
        units: String,
        minTimestamp: Long
    ): Flow<OneCallWeather> {
                TODO("Not yet implemented")

    }

    override suspend fun insertWeather(oneCallWeather: OneCallWeather) {
                TODO("Not yet implemented")

    }

    override suspend fun deleteWeatherForLocation(latitude: Double, longitude: Double) {
                TODO("Not yet implemented")

    }

    override suspend fun deleteFavourite(lat: Double, lon: Double) {
                TODO("Not yet implemented")

    }

    override fun getAllAlerts(): Flow<List<AlertsData>> {
                TODO("Not yet implemented")

    }

    override suspend fun deleteAlert(time: Long) {
                TODO("Not yet implemented")

    }

    override suspend fun deleteAllAlerts() {
                TODO("Not yet implemented")

    }
}
