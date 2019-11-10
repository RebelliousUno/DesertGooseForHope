package uno.rebellious.desertgooseforhope.model

import java.util.*

class Settings {
    private val props = Properties()

    init {
        props.load(this.javaClass.classLoader.getResourceAsStream("settings.properties"))
    }
    val channel: String? = props.getProperty("channel")
    val nick: String? = props.getProperty("nick")
    val password: String? = props.getProperty("password")
    val delayMin: Int = props.getProperty("delayMin")?.toInt() ?: 30
    val delayMax: Int = props.getProperty("delayMax")?.toInt() ?: 60
}
