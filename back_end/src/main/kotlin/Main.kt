import com.google.common.collect.Sets
import com.google.gson.Gson
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.default
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File
import java.lang.IllegalStateException
import java.util.stream.Collectors


class Core(calendarIds: Set<String>) {
    private var dataManager = DataManager()
    private var scheduler : Scheduler

    init {
        val loaders : List<Loader> = listOf(Weather(), GoogleCalendar(calendarIds))
        scheduler = Scheduler(dataManager, loaders)
    }

    fun start() {
        scheduler.start()
    }
    fun stop() {
        scheduler.stop()
    }

    fun getWeatherInfo() : WeatherInfo? {
        return dataManager.getWeather()
    }

    fun getCalendar() : List<Event> {
        return dataManager.getCalendar()
    }

    fun addCalendar(calendarIds : Collection<String>): AddCalendarAnswer {
        val loaders = scheduler.getLoaders()
        for (loader in loaders) {
            if (loader is GoogleCalendar) {
                return loader.addCalendarId(calendarIds)
            }
        }

        throw IllegalStateException("No GoogleCalendar loader found.")
    }
}

data class CalendarIds(
    val calendarIds: List<String>
)

fun main(args: Array<String>) {
    val arguments = ArgParser(args).parseInto(::CliArgs)
    val core = Core(parseCSV(arguments.calendarIdsPath))
    core.start()

    val server = embeddedServer(Netty, port = arguments.port) {
        install(CORS) {
            anyHost()
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

        routing {
            static {
                resource("static/index.html")
                default("static/index.html")
            }
            static ("static") {
                resources("static")
            }

            get("/meteo") {
                call.respond(HttpStatusCode.OK, message = core.getWeatherInfo() ?: "Weather Info are not loaded.")
            }
            get("/calendar") {
                call.respond(HttpStatusCode.OK, message = core.getCalendar())
            }
            post("/calendar") {
                val body = call.receiveText()
                val ids = Gson().fromJson(body, CalendarIds::class.java)
                val addCalendar = core.addCalendar(ids.calendarIds)

                addToCSV(arguments.calendarIdsPath, addCalendar.added)
                call.respond(HttpStatusCode.Accepted, message = addCalendar)
            }
        }

    }
    server.start(wait = true)

}

fun parseCSV(calendarIdsPath: String): Set<String> {
    val file = File(calendarIdsPath)
    if (!file.exists()) {
        println("File \"$calendarIdsPath\" does not exist, it will be created.")
        file.createNewFile()
        return Sets.newHashSet()
    }

    val content = file.readLines().stream().collect(Collectors.joining())
    if (content.isEmpty()) return Sets.newHashSet()
    return content.split(',').stream().collect(Collectors.toSet())
}

fun addToCSV(calendarIdsPath: String, calendarIds: List<String>) {
    val currentCalendars = parseCSV(calendarIdsPath)
    var calendars = Sets.newHashSet<String>()
    calendars.addAll(currentCalendars)
    calendars.addAll(calendarIds)

    val file = File(calendarIdsPath)
    file.writeText(calendars.stream().collect(Collectors.joining(",")))
}

class CliArgs(parser: ArgParser) {
    companion object {
        const val DEFAULT_CALENDAR_IDS_CSV = "calendar_ids.csv"
    }

    val port : Int by parser.storing(
        "-p", "--port",
        help = "port used by the server") {
        toInt()
    }.default(8080)

    val calendarIdsPath : String by parser.storing(
        "-c", "--calendarIdsPath",
        help = "path to csv file containing calendarIds")
        .default(DEFAULT_CALENDAR_IDS_CSV)
}
