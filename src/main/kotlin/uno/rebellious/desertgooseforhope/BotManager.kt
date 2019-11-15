package uno.rebellious.desertgooseforhope

import com.gikk.twirk.Twirk
import com.gikk.twirk.TwirkBuilder
import com.gikk.twirk.events.TwirkListener
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uno.rebellious.desertgooseforhope.model.Channel

import uno.rebellious.desertgooseforhope.model.Settings
import java.security.SecureRandom
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

object BotManager {

    private val scanner = Scanner(System.`in`).useDelimiter("\n").toObservable().share()

    private val SETTINGS = Settings()
    private var twirkThread: Thread? = null
    private val logger = Logger.getLogger("desertGooseForHope")
    private val items = arrayListOf("MIC FIVE!", "KitKats", "Shift Banner", "Random Dance Party Button", "Controller", "Bell", "Porridge/Oatmeal", "Ham").map { "/me Steals your $it" }
    private val magicTerms = arrayListOf("/me Creates a food token", "/me Turns into a 3/3 elk")
    private val honks = arrayListOf("Butt soft through yonder window HONK", "Honk", "HONK!", "Honk?!", "honk", "honk?", "!?knoH")
    private var shouts = ArrayList<String>()

    private var omegaHonks = arrayListOf("Driver shifts?  Where we're going we don't need no driver shifts!", "Dance party until we drop", "/me Honks in Omega", "Crash that bus!", "Can we get another hour?!", "For the children", "Bussing Makes Me Feel Good")
    private var omegaItems  = arrayListOf("Ice Cream Chords", "Bus Down", "Mystery Box", "Confetti Cannon", "Omega Shift Sign", "Tyres").map { "/me Steals your $it" }

    private var omegaShouts = ArrayList<String>()

    private fun startHonkTimerForChannel(twirk: Twirk, listener: UnoBotBase) {
        var delay: Long = 0L
        var omegaChanged: Boolean
        listener.omegaMode
            .distinctUntilChanged()
            .startWith(false)
            .subscribe() {
                omegaChanged = true
            }

        GlobalScope.launch {
            logger.log(Level.INFO, "Min Delay: ${SETTINGS.delayMin}")
            logger.log(Level.INFO, "Max Delay: ${SETTINGS.delayMax}")
            while (true) {
                omegaChanged = false

                val omegaMode = listener.omegaMode.value ?: false
                if (shouts.isEmpty() && !omegaMode) {
                    shouts = ArrayList(items + magicTerms + honks)
                }
                if (omegaShouts.isEmpty() && omegaMode) {
                    omegaShouts = ArrayList(omegaHonks + omegaItems)
                }
                delay = if (!omegaMode) {
                    (SecureRandom.getInstanceStrong().nextInt(SETTINGS.delayMax - SETTINGS.delayMin) + SETTINGS.delayMin) * 60 * 1000L
                } else {
                    20L * 1000 * 60 // Every 20 minutes
                }
                logger.log(Level.INFO, "Next Delay $delay")

                while (delay>=0 && !omegaChanged) { //Check that Omega hasn't been activated or deactivated
                    delay(1000*60) // Delay for a minute
                    delay -= 1000*60 // remove a minute
                    logger.log(Level.INFO, "Remaing delay $delay")
                }

                val shout = if (!omegaMode) {
                    shouts.random()
                } else {
                    omegaShouts.random()
                }
                shouts.remove(shout)
                omegaShouts.remove(shout)
                twirk.channelMessage(shout)
            }
        }
    }

    fun startTwirkForChannel() {
        val channel = Channel(SETTINGS.channel ?: "rebelliousuno", "", "", "")
        twirkThread = Thread(Runnable {
            val shouldStop = BehaviorSubject.create<Boolean>()
            shouldStop.onNext(false)
            val nick = if (channel.nick.isBlank()) SETTINGS.nick else channel.nick
            val password = if (channel.token.isBlank()) SETTINGS.password else channel.token

            val twirk = TwirkBuilder("#${channel.channel}", nick, password)
                .setVerboseMode(true)
                .build()
            twirk.connect()
            val listener = getListener(twirk)
            twirk.addIrcListener(listener)
            startHonkTimerForChannel(twirk, listener as UnoBotBase)

            scanner
                .takeUntil { it == ".quit" }
                .subscribe {
                    if (it == ".quit") {
                        println("Quitting $channel")
                        twirk.close()
                    } else {
                        twirk.channelMessage(it)
                    }
                }
        })
        twirkThread?.name = channel.channel
        twirkThread?.start()
    }

    fun stopTwirkForChannel() {
        twirkThread?.interrupt()
    }

    private fun getListener(twirk: Twirk): TwirkListener? {
        val listener = UnoBotBase(twirk)
        listener.omegaMode.onNext(false)
        return listener
    }
}