package com.github.mcaustin.twitch

import com.gikk.twirk.TwirkBuilder
import com.github.mcaustin.deck.DeckCodeBuilder
import org.apache.logging.log4j.LogManager

/**
 * Connects to twitch and starts listening for messages
 */
class TwitchBot(val channelName: String, val accountName: String, val oAuthSecret: String): DonkeyHarvester.NewGameConsumer {

    private val logger = LogManager.getLogger(TwitchBot::class.java)
    private val donkeyHarvester = DonkeyHarvester()
    private val twirk = TwirkBuilder(channelName, accountName, oAuthSecret)
        .setVerboseMode(false)
        .build()

    private var lastGame: DonkeyHarvester.DonkeyGame? = null

    fun startBot() {
        val deckCodeBuilder = DeckCodeBuilder()

        logger.info("Booting donkey harvester...")
        donkeyHarvester.registerCallback(this)

        logger.info("Adding message listener")
        twirk.addIrcListener(TwitchMessageListener(twirk, deckCodeBuilder, donkeyHarvester))

        logger.info("Connecting to Twitch...")
        if (twirk.connect()) {
            logger.info("Success!")
        } else {
            logger.error("Failed to connect to channel $channelName with account $accountName")
            twirk.close()
        }
    }

    override fun newGamesFound(games: List<DonkeyHarvester.DonkeyGame>) {
        if (twirk.isConnected) {
            val currentGame = games.first()

            if (lastGame != null && lastGame != currentGame) {
                twirk.channelMessage(
                    "Keith is playing ${currentGame.heroClass} at Rank ${currentGame.legendRank}, play " +
                        "it yourself with deck code: ${currentGame.deckCode}"
                )
            }
            lastGame = currentGame
        }
    }
}
