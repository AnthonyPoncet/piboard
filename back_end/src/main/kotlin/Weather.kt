import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.BadResponseStatusException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode


data class Owm(
    val coord: Coordinate,
    val weather: List<WInfo>,
    val base: String, /* Internal parameter */
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    //val rain: Rain,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val id: Long, /* City ID */
    val name: String, /* City Name */
    val cod: Long /* Internal parameter */
)
/* City longitude and latitude */
data class Coordinate(val lon: Double, val lat: Double)
/* Weather condition id, Group of weather parameters (Rain, Snow, Extreme etc.), Weather condition within the group, Weather icon id */
data class WInfo(val id: Long, val main: String, val description: String, val icon: String)
/* Temperature (Celsius), Atmospheric pressure (hPa), Humidity (%), Minimum temperature at the moment, Maximum temperature at the moment*/
data class Main(val temp: Double, val pressure: Double, val humidity: Double, val temp_min: Double, val temp_max: Double)
/* Wind speed (meter/sec), Wind direction (degrees) */
data class Wind(val speed: Double, val deg: Double)
/* Rain volume for the last 1 hour (mm) */
//data class Rain(val 1h :Double)
/* Cloudiness (%) */
data class Clouds(val all: Long)
/* Internal parameter, Internal parameter, Internal parameter, Country code, Sunrise time (unix UTC), Sunset time (unix UTC) */
data class Sys(val type: Long, val id: Long, val message: Double, val country: String, val sunrise: Long, val sunset: Long)

class Weather(private val api_key : String) : Loader {

    private val client = HttpClient(Apache) {}

    override fun operationName(): String {
        return "Weather"
    }

    override suspend fun load(dataManager: DataManager) {
        try {
            val content = client.get<String>("http://api.openweathermap.org/data/2.5/weather?units=metric&lang=fr&q=paris,fr&appid=$api_key") {}
            val answer = Gson().fromJson(content, Owm::class.java)
            dataManager.setWeather(answer)
        } catch (e: BadResponseStatusException) {
            if (e.statusCode == HttpStatusCode.Unauthorized) {
                println("Weather is misconfigured, received Unauthorized with given api key: $api_key. Please check server configuration file.")
                return
            }
            throw e
        }
    }
}