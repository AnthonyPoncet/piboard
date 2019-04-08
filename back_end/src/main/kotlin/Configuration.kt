import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.gson.Gson
import com.sun.org.apache.xml.internal.security.utils.Constants.configurationFile
import com.xenomachina.argparser.InvalidArgumentException
import java.io.File
import java.util.stream.Collectors

/**
 * owmApiKey is mandatory in JSON while calendarIds is optional.
 */
class Configuration(private val owmApiKey: String, private var calendarIds : MutableSet<String>) {

    companion object {
        fun fromJsonFile(filePath : String) : Configuration {
            val file = File(filePath)

            if (!file.exists()) {
                throw InvalidArgumentException("$configurationFile passed as configuration file does not exist.")
            }
            if (file.extension != "json") {
                throw InvalidArgumentException("$configurationFile passed as configuration file is not a json but a ${file.extension}.")
            }

            val content = file.readLines().stream().collect(Collectors.joining())
            val fromJson : Configuration = Gson().fromJson(content, Configuration::class.java)

            if (fromJson.owmApiKey == null) {
                throw InvalidArgumentException("Missing owmApiKey in configuration file.")
            }

            if (fromJson.calendarIds == null) fromJson.calendarIds = mutableSetOf()

            return fromJson
        }
    }

    fun getOwmApiKey() : String {
        return owmApiKey
    }

    fun getCalendarIds() : Set<String> {
        return calendarIds
    }

    fun addCalendarIds(calendarIds: Collection<String>) : Boolean {
        return this.calendarIds.addAll(calendarIds)
    }

    @Synchronized
    fun updateJsonFile() {
        val file = File(configurationFile)
        val toJson = Gson().toJson(this, Configuration::class.java)
        file.writeText(toJson)
    }
}
