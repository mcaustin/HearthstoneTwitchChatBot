package com.github.mcaustin.twitch

import com.gikk.twirk.Twirk
import com.gikk.twirk.events.TwirkListener
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.deck.DeckCodeBuilder
import com.github.mcaustin.twitch.commands.TwitchCommandInterpreter
import org.apache.logging.log4j.LogManager.getLogger

class TwitchMessageListener(twirk: Twirk, deckCodeBuilder: DeckCodeBuilder): TwirkListener {

    private val commandInterpreter = TwitchCommandInterpreter(twirk, deckCodeBuilder)
    private val logger = getLogger(TwitchMessageListener::class.java)

    override fun onPrivMsg(sender: TwitchUser?, message: TwitchMessage?) {
        super.onPrivMsg(sender, message)

        logger.info("${sender?.displayName}: ${message?.content}")
        try {
            commandInterpreter.interpret(sender, message)
        } catch (e: RuntimeException) {
            println("Problem handling command")
            e.printStackTrace()
        }
    }
}
