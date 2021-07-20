package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.db.ViewerDeckRequestLocalDbDAO
import java.lang.StringBuilder
import java.lang.UnsupportedOperationException

class DeckStatsCommand(private val twirk: Twirk): CommandExecutor {

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        if (!canHandle(sender, message)) {
            throw UnsupportedOperationException("Can't handle that command")
        }

        message?.content?.let {
            handleStatsRequest()
        }
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) = message?.content?.equals("!deckstats") ?: false

    private fun handleStatsRequest() {

        var messageBuilder = StringBuilder()
        messageBuilder = topHeroClasses(messageBuilder)
        messageBuilder.append("| *** | ")
        messageBuilder = topViewerSubmission(messageBuilder)
        messageBuilder.append("| *** | ")
        messageBuilder = topCards(messageBuilder)

        twirk.channelMessage(messageBuilder.toString())
    }

    private fun topHeroClasses(message: StringBuilder): StringBuilder {
        val sorted = ViewerDeckRequestLocalDbDAO.groupRequestsByClass().toList().sortedBy { (_, value) -> value }.asReversed()

        val numPlaces = sorted.size.coerceAtMost(3)

        message.append("Top viewer-deck classes: ")
        return buildTopMessage(numPlaces, sorted, message)
    }

    private fun topViewerSubmission(message: StringBuilder): StringBuilder {
        val sorted = ViewerDeckRequestLocalDbDAO.groupRequestsByViewer().toList().sortedBy { (_, value) -> value }.reversed()

        val numPlaces = sorted.size.coerceAtMost(3)

        message.append("Top submissions by viewer: ")
        return buildTopMessage(numPlaces, sorted, message)
    }

    private fun topCards(message: StringBuilder): StringBuilder {
        val sorted = ViewerDeckRequestLocalDbDAO.cardsByOccurrence()
            .toList()
            .sortedBy { (_, value) -> value }
            .reversed()
            .map {
                Pair(it.first.name ?: "card-${it.first.dbfId}", it.second)
            }

        val numPlaces = sorted.size.coerceAtMost(3)

        message.append("Top cards: ")
        return buildTopMessage(numPlaces, sorted, message)
    }

    private fun buildTopMessage(
        numPlaces: Int,
        sorted: List<Pair<String, Int>>,
        message: StringBuilder
    ): StringBuilder {
        for (i in 0 until numPlaces) {
            val propertyName = sorted[i].first
            val numTimes = sorted[i].second
            message.append("$propertyName:($numTimes) ")
        }
        return message.append(" ")
    }
}
