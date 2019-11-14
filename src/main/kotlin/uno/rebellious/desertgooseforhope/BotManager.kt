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

    private fun startHonkTimerForChannel(twirk: Twirk) {
        GlobalScope.launch {
            logger.log(Level.INFO, "Min Delay: ${SETTINGS.delayMin}")
            logger.log(Level.INFO, "Max Delay: ${SETTINGS.delayMax}")
            while (true) {
                if (shouts.isEmpty())
                    shouts = ArrayList(items + magicTerms + honks)
                val delay =
                    (SecureRandom.getInstanceStrong().nextInt(SETTINGS.delayMax - SETTINGS.delayMin) + SETTINGS.delayMin) * 60 * 1000L
                logger.log(Level.INFO, "Next Delay $delay")
                delay(delay)
                val shout = shouts.random()
                shouts.remove(shout)
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
            twirk.addIrcListener(getOnDisconnectListener(twirk))
            startHonkTimerForChannel(twirk)

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

    private fun getOnDisconnectListener(twirk: Twirk): TwirkListener? {
        return UnoBotBase(twirk)
    }
}