import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

data class AddCalendarAnswer (
    val added : List<String>,
    val notAdded : Map<String, String> /* name followed by error */
)

class UnknownCalendars(message: String) : Exception(message)

class GoogleCalendar(private val googleCredentialsFile: String, calendarIds: Set<String>) : Loader {
    companion object {
        private const val APPLICATION_NAME = "Google Calendar for PiBoard"

        private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
        private val SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)
        private const val TOKENS_DIRECTORY_PATH = "tokens"

        private const val MAX_RESULT = 20
    }

    private val service : Calendar
    private val calendarIds = Sets.newConcurrentHashSet<String>()

    init {
        service = Calendar
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME).build()

        val addCalendarAnswer = addCalendarIds(calendarIds)
        if (!addCalendarAnswer.notAdded.isEmpty()) {
            throw UnknownCalendars("These calendars are unknown for the connected user: " + addCalendarAnswer.notAdded.keys)
        }
    }

    fun addCalendarIds(calendarIds: Collection<String>) : AddCalendarAnswer {
        val added = Lists.newArrayList<String>()
        val notAdded = Maps.newHashMap<String,String>()
        for (calendar in calendarIds) {
            try {
                service.calendars().get(calendar).execute()
                this.calendarIds.add(calendar)
                added.add(calendar)
            } catch (e: GoogleJsonResponseException) {
                notAdded[calendar] = e.details?.message
            }
        }
        return AddCalendarAnswer(added, notAdded)
    }

    override fun operationName(): String {
        return "Google calendar"
    }

    override suspend fun load(dataManager: DataManager) {
        // List the next 10 events from the primary calendar.
        val now = DateTime(System.currentTimeMillis())

        for (calendarId in calendarIds) {
            val events = service.events()
                .list(calendarId)
                .setMaxResults(MAX_RESULT)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()

            dataManager.setEvents(events.items.map { event ->
                Event(
                    convert(event.start.dateTime),
                    convert(event.end.dateTime),
                    event.organizer.displayName ?: "",
                    event.summary ?: ""
                )
            })
        }
    }

    private fun convert(date : DateTime) : Date {
        return Date(date.value / 1000, date.timeZoneShift / 60)
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param  HTTP_TRANSPORT The network HTTP Transport.
     *
     * @return An authorized Credential object.
     *
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val inputStream = File(googleCredentialsFile).inputStream()
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(
                FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH))
            )
            .setAccessType("offline").build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}