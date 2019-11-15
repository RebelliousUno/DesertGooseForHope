package uno.rebellious.desertgooseforhope

import com.gikk.twirk.Twirk
import com.gikk.twirk.events.TwirkListener
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import io.reactivex.subjects.BehaviorSubject
import java.io.IOException
import java.util.logging.Logger

class UnoBotBase constructor(private val twirk: Twirk) : TwirkListener {

    var omegaMode = BehaviorSubject.create<Boolean>()


    override fun onPrivMsg(sender: TwitchUser?, message: TwitchMessage?) {
        if (sender?.userName?.toLowerCase() == "rebelliousuno" && message?.content?.trim()?.toLowerCase() == "hey goose activate omega mode") {
            twirk.channelMessage("Hey Uno, Omega Mode Engaged!")
            omegaMode.onNext(true)
        }
        if (sender?.userName?.toLowerCase() == "rebelliousuno" && message?.content?.trim()?.toLowerCase() == "hey goose disable omega mode") {
            twirk.channelMessage("Hey Uno, Omega Mode Disabled!")
            omegaMode.onNext(false)
        }
    }

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