import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.*

class DataManagerTest : StringSpec() {
    companion object {
        val DATE_1 = Date(1L, 1)
        val DATE_2 = Date(2L, 1)
        val DATE_3 = Date(3L, 1)

        val EVENT_1 = Event(DATE_1, DATE_2, "o1", "s1")
        val EVENT_2 = Event(DATE_2, DATE_3, "o1", "s1")

        val OWM_1 = Owm(
            Coordinate(1.5, 6.908),
            Arrays.asList(WInfo(1L, "a", "desc", "ic")),
            "b1",
            Main(10.0, 12.2, 14.1, 5.3, 10.1),
            50L,
            Wind(34.3, 89.1),
            Clouds(456L),
            9L,
            Sys(1L, 2L, 3.4, "FR", 5L, 6L),
            8L,
            "name",
            90875L)
        val WEATHER_1 = WeatherInfo(10.0, 14.1, "desc")
        val OWM_2 = Owm(
            Coordinate(4.623, 6.8),
            Arrays.asList(WInfo(1L, "zaz", "desc2", "ic")),
            "b1f",
            Main(23.4, 12.2, 0.004, 5.3, 35.0),
            503L,
            Wind(34.3, 89.1),
            Clouds(456L),
            1L,
            Sys(1L, 2L, 3.4, "FR", 5L, 6L),
            90L,
            "name second",
            90875L)
        val WEATHER_2 = WeatherInfo(23.4, 0.004, "desc2")
    }

    init {
        "Could init DataManager" {
            val dataManager = DataManager()
            dataManager.getEvents() shouldBe emptyList()
            dataManager.getWeather() shouldBe null
        }

        "Could set calendar" {
            val dataManager = DataManager()

            dataManager.setEvents(Arrays.asList(EVENT_1))
            dataManager.getEvents() shouldBe Arrays.asList(EVENT_1)

            dataManager.setEvents(Arrays.asList(EVENT_1, EVENT_2))
            dataManager.getEvents() shouldBe Arrays.asList(EVENT_1, EVENT_2)

            dataManager.setEvents(Arrays.asList(EVENT_2))
            dataManager.getEvents() shouldBe Arrays.asList(EVENT_2)
        }

        "Could set weather info" {
            val dataManager = DataManager()

            dataManager.setWeather(OWM_1)
            dataManager.getWeather() shouldBe WEATHER_1

            dataManager.setWeather(OWM_2)
            dataManager.getWeather() shouldBe WEATHER_2
        }
    }


}