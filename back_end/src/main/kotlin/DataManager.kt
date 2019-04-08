data class WeatherInfo(
    val temperature: Double,
    val humidity: Double,
    val description: String
)

data class Date(val epochSec: Long, val shiftHour: Int)
data class Event(
    val start: Date,
    val end: Date,
    val organizer: String,
    val summary: String
)

class DataManager {
    private var weatherInfo : WeatherInfo? = null
    private var events : List<Event> = mutableListOf()

    @Synchronized
    fun setWeather(owm: Owm) {
        this.weatherInfo = WeatherInfo(owm.main.temp, owm.main.humidity, owm.weather[0].description)
    }

    @Synchronized
    fun getWeather(): WeatherInfo? {
        return weatherInfo
    }

    @Synchronized
    fun setEvents(events : List<Event>) {
        this.events = events
    }

    @Synchronized
    fun getEvents() : List<Event> {
        return events
    }
}