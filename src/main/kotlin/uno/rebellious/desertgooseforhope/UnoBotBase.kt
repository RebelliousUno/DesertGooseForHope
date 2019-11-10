package uno.rebellious.desertgooseforhope

import com.gikk.twirk.Twirk
import com.gikk.twirk.events.TwirkListener
import java.io.IOException
import java.util.logging.Logger

class UnoBotBase constructor(private val twirk: Twirk) : TwirkListener {

    override fun onDisconnect() {
        Logger.getLogger("UnoBotBase").info("Disconnected")
        try {
            if (!twirk.connect())
                twirk.close()
        } catch (e: IOException) {
            twirk.close()
        } catch (e: InterruptedException) {
        }
        BotManager.stopTwirkForChannel()
        BotManager.startTwirkForChannel()
        Logger.getLogger("UnoBotBase").info("Thread Count : ${Thread.activeCount()}")
    }
}