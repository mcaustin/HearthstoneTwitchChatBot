package com.github.mcaustin.twitch

import com.gikk.twirk.TwirkBuilder
import com.github.mcaustin.db.CustomCommandDAO
import com.github.mcaustin.deck.DeckCodeBuilder
import org.apache.logging.log4j.LogManager

/**
 * Connects to twitch and starts listening for messages
 */
class TwitchBot(val channelName: String, val accountName: String, val oAuthSecret: String) {

    private val logger = LogManager.getLogger(TwitchBot::class.java)
    private val twirk = TwirkBuilder(channelName, accountName, oAuthSecret)
        .setVerboseMode(false)
        .build()

    fun startBot() {
        val deckCodeBuilder = DeckCodeBuilder()

        logger.info("Adding message listener")
        twirk.addIrcListener(TwitchMessageListener(twirk, deckCodeBuilder))

        logger.info("Connecting to Twitch...")
        if (twirk.connect()) {
            logger.info("Success!")
        } else {
            logger.error("Failed to connect to channel $channelName with account $accountName")
            twirk.close()
        }

        CustomCommandDAO.getCommands().forEach {
            logger.info("Custom Command ${it.key} -> ${it.value}")
        }
    }
}
