import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface Loader {
    fun operationName() : String
    suspend fun load(dataManager: DataManager)
}

class Scheduler(private val dataManager: DataManager, private val loaders: List<Loader>, private val refreshDelay: Long = 5 * 60 * 1000) {

    private val executor = Executors.newSingleThreadScheduledExecutor()
    @Volatile var lastRefresh : LocalDateTime? = null
    @Volatile var refreshOnGoing = false

    fun start() {
        val c = Runnable { launchCoroutine() }
        executor.scheduleWithFixedDelay(c, 0, refreshDelay, TimeUnit.MILLISECONDS)
    }

    fun stop() {
        executor.shutdown()
    }

    private fun launchCoroutine() {
        GlobalScope.launch {
            try {
                refreshOnGoing = true

                if (lastRefresh != null && lastRefresh!!.isAfter(LocalDateTime.now().minusSeconds(20))) {
                    return@launch
                } else {
                    loaders.forEach {
                        try {
                            it.load(dataManager)
                        } catch (e: Exception) {
                            println("Exception in " + it.operationName() + " - " + e)
                        }
                    }

                    lastRefresh = LocalDateTime.now()
                }
            } finally {
                refreshOnGoing = false
            }
        }
    }

    fun getLoaders() : List<Loader> {
        return loaders
    }
}