package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.db.DeckStatsDAO
import com.github.mcaustin.db.ViewerDeckRequestLocalDbDAO
import java.lang.UnsupportedOperationException
import java.time.Instant

class LastDeckCommand(private val twirk: Twirk) : CommandExecutor {

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        if (!canHandle(sender, message)) {
            throw UnsupportedOperationException("Can't handle that command")
        }

        message?.content?.let {
            handleLastDeckRequest(sender, it)
        }
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) =
        message?.content?.startsWith("!lastdeck") ?: false

    private fun handleLastDeckRequest(sender: TwitchUser?, messageContent: String) {
        val tokens = messageContent.split(" ")
        val viewerName = getViewerName(tokens, sender)

        viewerName?.let { viewer ->
            val lastDeck = DeckStatsDAO.getViewerLastDeck(viewer)

            lastDeck?.let {
                twirk.channelMessage("$viewer's last submitted deck: $it")
            } ?: twirk.channelMessage("$viewer has no recorded decks ")
        }
    }

    private fun getViewerName(
        tokens: List<String>,
        sender: TwitchUser?
    ): String? {
        return if (tokens.size == 2) {
            tokens[1]
        } else if (sender != null && tokens.size == 1) {
            sender.displayName
        } else {
            null
        }
    }
}
