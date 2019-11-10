package uno.rebellious.desertgooseforhope

fun main(args: Array<String>) {
    BotManager.startTwirkForChannel()
}

data class Channel(val channel: String, val prefix: String, val nick: String, val token: String)