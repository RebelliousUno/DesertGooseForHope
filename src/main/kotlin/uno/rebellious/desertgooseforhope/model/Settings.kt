package uno.rebellious.desertgooseforhope.model

import java.util.*

class Settings {
    private val props = Properties()
    private var settings: DataSettings?
    init {
        props.load(this.javaClass.classLoader.getResourceAsStream("settings.properties"))
        settings = DataSettings(props.getProperty("nick"), props.getProperty("password"), props.getProperty("channel"), props.getProperty("delayMin").toInt(), props.getProperty("delayMax").toInt())
    }

    val channel: String?
        get() {
            return settings?.CHANNEL
        }

    val nick: String?
        get() {
            return settings?.MY_NICK
        }

    val password: String?
        get() {
            return settings?.MY_PASS
        }

    val delayMin: Int
        get() {
            return settings?.DELAY_MIN ?: 30
        }
    val delayMax: Int
        get() {
            return settings?.DELAY_MAX ?: 60
        }
}

data class DataSettings(val MY_NICK: String, val MY_PASS: String, val CHANNEL: String, val DELAY_MIN: Int, val DELAY_MAX: Int)
