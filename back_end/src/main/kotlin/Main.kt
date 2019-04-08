import com.google.gson.Gson
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File


data class CalendarIds(val calendarIds: List<String>)

class Core(googleCredentialsFile: String, configurationFile: String) {
    private var dataManager = DataManager()
    private var scheduler : Scheduler
    private val configuration = Configuration.fromJsonFile(configurationFile)

    init {
        val loaders : List<Loader> = listOf(Weather(configuration.getOwmApiKey()), GoogleCalendar(googleCredentialsFile, configuration.getCalendarIds()))
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

    fun getEvents() : List<Event> {
        return dataManager.getEvents()
    }

    fun addCalendars(calendarIds : Collection<String>) : AddCalendarAnswer {
        val loaders = scheduler.getLoaders()
        for (loader in loaders) {
            if (loader is GoogleCalendar) {
                val addCalendarAnswer = loader.addCalendarIds(calendarIds)
                configuration.addCalendarIds(addCalendarAnswer.added)
                configuration.updateJsonFile()
                return addCalendarAnswer
            }
        }

        throw IllegalStateException("No GoogleCalendar loader found.")
    }
}

data class Error(val error: String)

fun main(args: Array<String>) {
    mainBody {
        val arguments = ArgParser(args).parseInto(::CliArgs)
        val core = Core(arguments.googleCredentialsFile, arguments.configurationFile)
        core.start()

        val server = embeddedServer(Netty, port = arguments.port) {
            install(CORS) {
                anyHost()
            }
            install(Compression)
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }

            routing {
                get("/meteo") {
                    if (core.getWeatherInfo() != null) {
                        call.respond(HttpStatusCode.OK, message = core.getWeatherInfo()!!)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, message = Error("Weather Info not available on server."))
                    }
                }
                route("/calendar") {
                    get {
                        call.respond(HttpStatusCode.OK, message = core.getEvents())
                    }
                    post {
                        val body = call.receiveText()
                        val ids = Gson().fromJson(body, CalendarIds::class.java)
                        val addedCalendars = core.addCalendars(ids.calendarIds)

                        call.respond(HttpStatusCode.Accepted, message = addedCalendars)
                    }
                }

                static("/") {
                    resource("static/index.html")
                    defaultResource("static/index.html")
                }

                static("static") {
                    resources("static")
                }
            }

        }
        server.start(wait = true)
    }

}

class CliArgs(parser: ArgParser) {
    val port : Int by parser.storing(
        "-p", "--port",
        help = "port used by the server") {
        toInt()
    }.default(8080)

    val configurationFile : String by parser.storing(
        "-c", "--configFile",
        help = "json file containing configuration")
        .addValidator {
            Configuration.fromJsonFile(configurationFile)
        }

    val googleCredentialsFile : String by parser.storing(
        "-g", "--googleCredentialsFile",
        help = "json file containing google credential")
        .addValidator {
            val file = File(googleCredentialsFile)
            if (!file.exists()) {
                throw InvalidArgumentException("$googleCredentialsFile passed as googleCredentialsFile does not exist.")
            }
            if (file.extension != "json") {
                throw InvalidArgumentException("$googleCredentialsFile passed as googleCredentialsFile is not a json but a ${file.extension}.")
            }
        }
}
