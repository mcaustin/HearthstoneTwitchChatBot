package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.deck.analysis.DeckAnalyzer
import com.github.mcaustin.deck.DeckCodeBuilder
import com.github.mcaustin.deck.analysis.OddEvenAnalysis
import com.github.mcaustin.db.ViewerDeckRequest
import com.github.mcaustin.db.ViewerDeckRequestLocalDbDAO
import com.github.mcaustin.deck.analysis.CoreSetDuplicateAnalysis
import com.github.mcaustin.deck.analysis.NetDeckAnalysis
import com.github.mcaustin.twitch.KeithNumbersConstants
import com.github.mcaustin.twitch.TwitchBot
import org.apache.logging.log4j.LogManager
import java.lang.RuntimeException

class SubmitDeckCodeCommand(
    private val deckCodeBuilder: DeckCodeBuilder,
    private val twirk: Twirk
) : CommandExecutor {

    private val logger = LogManager.getLogger(SubmitDeckCodeCommand::class.java)

    private val deckAnalyzers: List<DeckAnalyzer> = listOf(
        OddEvenAnalysis(),
        NetDeckAnalysis(twirk),
        CoreSetDuplicateAnalysis()
    )

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        message?.content?.let { messageContent ->
            val codeIndex = messageContent.indexOf("AAE")
            if (codeIndex >= 0) {
                handleDeckRequest(messageContent, codeIndex, message, sender)
            }
        }
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?): Boolean {
        return message?.content?.let {
            it.indexOf("AAE") >= 0
        } ?: false
    }

    private fun handleDeckRequest(
        messageContent: String,
        codeIndex: Int,
        message: TwitchMessage?,
        sender: TwitchUser?
    ) {
        val subString = messageContent.substring(codeIndex)
        val deckCode = subString.split(" ").first()

        val deck = try {
            deckCodeBuilder.buildDeck(deckCode)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            logger.error(e)
            twirk.channelMessage("Could not parse deck code: [ $deckCode ]")
            return
        }
        val twitchResponseBuilder = StringBuilder(deck.description())

        if (isPlayYourDeckReward(message)) {
            logger.info("play your deck reward redemption detected.")
            if (sender != null) {

                try {
                    ViewerDeckRequestLocalDbDAO.addRequest(
                        ViewerDeckRequest(
                            deckCode = deckCode,
                            viewerId = sender.displayName,
                            heroClass = deck.heroClass,
                            format = deck.format.name,
                            tags = deck.tags.toSet().ifEmpty { null }
                        )
                    )

                    val requestCount = ViewerDeckRequestLocalDbDAO.requestCount(sender.displayName)
                    twitchResponseBuilder.append(" ${sender.displayName} now has $requestCount recorded decks")
                } catch (e: RuntimeException) {
                    logger.error(e)
                }
            }
        }

        val extraAnalysis = StringBuilder()
        deckAnalyzers.forEach { deckAnalyzer ->
            deckAnalyzer.analyze(deck)?.let {
                extraAnalysis.append("$it ")
            }
        }
        twitchResponseBuilder.append(" $extraAnalysis")

        if (twitchResponseBuilder.length > 500) {
            twirk.channelMessage(twitchResponseBuilder.substring(0, 500))
        } else {
            twirk.channelMessage(twitchResponseBuilder.toString())
        }
    }

    private fun isPlayYourDeckReward(message: TwitchMessage?) = message?.tagMap?.containsKey("custom-reward-id") == true
        && message.tagMap?.get("custom-reward-id").equals(KeithNumbersConstants.REWARD_PLAY_YOUR_DECK)
}
