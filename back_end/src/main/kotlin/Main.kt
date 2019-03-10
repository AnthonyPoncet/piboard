import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


class Core {
    private var dataManager = DataManager()
    private var scheduler : Scheduler

    init {
        val loaders : List<Loader> = listOf(Weather(), GoogleCalendar())
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
}

fun main(args: Array<String>) {
    val arguments = ArgParser(args).parseInto(::CliArgs)
    val core = Core()
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
            get("/meteo") {
                call.respond(HttpStatusCode.OK, core.getWeatherInfo() ?: "Weather Info are not loaded.")
            }
            get("/calendar") {
                call.respond(HttpStatusCode.OK, core.getCalendar())
            }
        }

    }
    server.start(wait = true)

}

class CliArgs(parser: ArgParser) {
    val port : Int by parser.storing(
        "-p", "--port",
        help = "port used by the server") {
        toInt()
    }.default(8080)
}